package cn.sliew.flink.gateway.engine.endpoint.impl;

import cn.sliew.flink.gateway.engine.endpoint.RestEndpoint;
import cn.sliew.milky.common.exception.Rethrower;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.messages.webmonitor.JobIdsWithStatusOverview;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
import org.apache.flink.runtime.rest.FileUpload;
import org.apache.flink.runtime.rest.RestClient;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationInfo;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.handler.job.rescaling.RescalingStatusHeaders;
import org.apache.flink.runtime.rest.handler.job.rescaling.RescalingStatusMessageParameters;
import org.apache.flink.runtime.rest.handler.job.rescaling.RescalingTriggerHeaders;
import org.apache.flink.runtime.rest.handler.job.rescaling.RescalingTriggerMessageParameters;
import org.apache.flink.runtime.rest.handler.legacy.messages.ClusterOverviewWithVersion;
import org.apache.flink.runtime.rest.messages.*;
import org.apache.flink.runtime.rest.messages.checkpoints.*;
import org.apache.flink.runtime.rest.messages.cluster.JobManagerLogListHeaders;
import org.apache.flink.runtime.rest.messages.cluster.ShutdownHeaders;
import org.apache.flink.runtime.rest.messages.dataset.*;
import org.apache.flink.runtime.rest.messages.job.*;
import org.apache.flink.runtime.rest.messages.job.metrics.*;
import org.apache.flink.runtime.rest.messages.job.savepoints.*;
import org.apache.flink.runtime.rest.messages.job.savepoints.stop.StopWithSavepointRequestBody;
import org.apache.flink.runtime.rest.messages.job.savepoints.stop.StopWithSavepointTriggerHeaders;
import org.apache.flink.runtime.rest.messages.taskmanager.*;
import org.apache.flink.runtime.rest.util.RestConstants;
import org.apache.flink.runtime.webmonitor.handlers.*;
import org.apache.flink.runtime.webmonitor.threadinfo.JobVertexFlameGraph;
import org.apache.flink.util.ConfigurationException;
import org.apache.flink.util.concurrent.ExecutorThreadFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.sliew.milky.common.exception.Rethrower.toIllegalArgument;

public class RestEndpointImpl2 implements RestEndpoint {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4, new ExecutorThreadFactory("Flink-RestClusterClient-IO"));
    private final RestClient client;

    private final String address = "localhost";
    private final int port = 8081;

    public RestEndpointImpl2(Configuration configuration) {
        RestClient restClient = null;
        try {
            restClient = new RestClient(configuration, executorService);
        } catch (ConfigurationException e) {
            Rethrower.throwAs(e);
        }
        this.client = restClient;
    }

    @Override
    public CompletableFuture<DashboardConfiguration> config() throws IOException {
        return client.sendRequest(address, port, DashboardConfigurationHeaders.getInstance());
    }

    @Override
    public CompletableFuture<ClusterOverviewWithVersion> overview() throws IOException {
        return client.sendRequest(address, port, ClusterOverviewHeaders.getInstance());
    }

    @Override
    public CompletableFuture<EmptyResponseBody> shutdownCluster() throws IOException {
        return client.sendRequest(address, port, ShutdownHeaders.getInstance());
    }

    @Override
    public CompletableFuture<ClusterDataSetListResponseBody> datasets() throws IOException {
        return client.sendRequest(address, port, ClusterDataSetListHeaders.INSTANCE);
    }

    @Override
    public CompletableFuture<TriggerResponse> deleteDataSet(String datasetId) throws IOException {
        ClusterDataSetDeleteTriggerMessageParameters parameters = new ClusterDataSetDeleteTriggerMessageParameters();
        toIllegalArgument(() -> parameters.clusterDataSetIdPathParameter.resolveFromString(datasetId));
        return client.sendRequest(address, port, ClusterDataSetDeleteTriggerHeaders.INSTANCE, parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> deleteDataSetStatus(String triggerId) throws IOException {
        ClusterDataSetDeleteStatusMessageParameters parameters = new ClusterDataSetDeleteStatusMessageParameters();
        toIllegalArgument(() -> parameters.triggerIdPathParameter.resolveFromString(triggerId));
        return client.sendRequest(address, port, ClusterDataSetDeleteStatusHeaders.INSTANCE, parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<JarListInfo> jars() throws IOException {
        return client.sendRequest(address, port, JarListHeaders.getInstance());
    }

    @Override
    public CompletableFuture<JarUploadResponseBody> uploadJar(String filePath) throws IOException {
        FileUpload upload = new FileUpload(Paths.get(filePath), RestConstants.CONTENT_TYPE_BINARY);
        return client.sendRequest(address, port, JarUploadHeaders.getInstance(), EmptyMessageParameters.getInstance(), EmptyRequestBody.getInstance(), Collections.singleton(upload));
    }

    @Override
    public CompletableFuture<EmptyResponseBody> deleteJar(String jarId) throws IOException {
        JarDeleteMessageParameters parameters = new JarDeleteMessageParameters();
        toIllegalArgument(() -> parameters.jarIdPathParameter.resolveFromString(jarId));
        return client.sendRequest(address, port, JarDeleteHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<JobPlanInfo> jarPlan(String jarId, JarPlanRequestBody requestBody) throws IOException {
        JarPlanMessageParameters parameters = new JarPlanMessageParameters();
        toIllegalArgument(() -> parameters.jarIdPathParameter.resolveFromString(jarId));
        return client.sendRequest(address, port, JarPlanGetHeaders.getInstance(), parameters, requestBody);
    }

    @Override
    public CompletableFuture<JarRunResponseBody> jarRun(String jarId, JarRunRequestBody requestBody) throws IOException {
        JarRunMessageParameters parameters = new JarRunMessageParameters();
        toIllegalArgument(() -> parameters.jarIdPathParameter.resolveFromString(jarId));
        return client.sendRequest(address, port, JarRunHeaders.getInstance(), parameters, requestBody);
    }

    @Override
    public CompletableFuture<ClusterConfigurationInfo> jobmanagerConfig() throws IOException {
        return client.sendRequest(address, port, ClusterConfigurationInfoHeaders.getInstance());
    }

    @Override
    public CompletableFuture<LogListInfo> jobmanagerLogs() throws IOException {
        return client.sendRequest(address, port, JobManagerLogListHeaders.getInstance());
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobmanagerMetrics(Optional<String> get) throws IOException {
        JobManagerMetricsMessageParameters parameters = new JobManagerMetricsMessageParameters();
        get.ifPresent(metrics -> toIllegalArgument(() -> parameters.metricsFilterParameter.resolveFromString(metrics)));
        return client.sendRequest(address, port, JobManagerMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<MultipleJobsDetails> jobsOverview() throws IOException {
        return client.sendRequest(address, port, JobsOverviewHeaders.getInstance());
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobsMetric(Optional<String> get, Optional<String> agg, Optional<String> jobs) throws IOException {
        JobMetricsMessageParameters parameters = new JobMetricsMessageParameters();
        get.ifPresent(metrics -> toIllegalArgument(() -> parameters.metricsFilterParameter.resolveFromString(metrics)));
        jobs.ifPresent(job -> toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(job)));
        return client.sendRequest(address, port, JobMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<JobIdsWithStatusOverview> jobs() throws IOException {
        return client.sendRequest(address, port, JobIdsWithStatusesOverviewHeaders.getInstance());
    }

    @Override
    public CompletableFuture<JobDetailsInfo> jobDetail(String jobId) throws IOException {
        JobMessageParameters parameters = new JobMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        return client.sendRequest(address, port, JobDetailsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<JobSubmitResponseBody> jobSubmit(JobSubmitRequestBody requestBody) throws IOException {
        return client.sendRequest(address, port, JobSubmitHeaders.getInstance(), EmptyMessageParameters.getInstance(), requestBody);
    }

    @Override
    public CompletableFuture<EmptyResponseBody> jobTerminate(String jobId, String mode) throws IOException {
        JobCancellationMessageParameters parameters = new JobCancellationMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.terminationModeQueryParameter.resolveFromString(mode));
        return client.sendRequest(address, port, JobCancellationHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<JobAccumulatorsInfo> jobAccumulators(String jobId, Boolean includeSerializedValue) throws IOException {
        JobAccumulatorsMessageParameters parameters = new JobAccumulatorsMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.includeSerializedAccumulatorsParameter.resolveFromString(includeSerializedValue.toString()));
        return client.sendRequest(address, port, JobAccumulatorsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<CheckpointingStatistics> jobCheckpoints(String jobId) throws IOException {
        JobMessageParameters parameters = new JobMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        return client.sendRequest(address, port, CheckpointingStatisticsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<CheckpointConfigInfo> jobCheckpointConfig(String jobId) throws IOException {
        JobMessageParameters parameters = new JobMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        return client.sendRequest(address, port, CheckpointConfigHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<CheckpointStatistics> jobCheckpointDetail(String jobId, Long checkpointId) throws IOException {
        CheckpointMessageParameters parameters = new CheckpointMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.checkpointIdPathParameter.resolveFromString(checkpointId.toString()));
        return client.sendRequest(address, port, CheckpointStatisticDetailsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<TaskCheckpointStatisticsWithSubtaskDetails> jobCheckpointSubtaskDetail(String jobId, Long checkpointId, String vertexId) throws IOException {
        TaskCheckpointMessageParameters parameters = new TaskCheckpointMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.checkpointIdPathParameter.resolveFromString(checkpointId.toString()));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        return client.sendRequest(address, port, TaskCheckpointStatisticsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<JobConfigInfo> jobConfig(String jobId) throws IOException {
        JobMessageParameters parameters = new JobMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        return client.sendRequest(address, port, JobConfigHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<JobExceptionsInfoWithHistory> jobException(String jobId, String maxExceptions) throws IOException {
        JobExceptionsMessageParameters parameters = new JobExceptionsMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.upperLimitExceptionParameter.resolveFromString(maxExceptions));
        return client.sendRequest(address, port, JobExceptionsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<JobExecutionResultResponseBody> jobExecutionResult(String jobId) throws IOException {
        JobMessageParameters parameters = new JobMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        return client.sendRequest(address, port, JobExecutionResultHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobMetrics(String jobId, Optional<String> get) throws IOException {
        JobMetricsMessageParameters parameters = new JobMetricsMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        get.ifPresent(metrics -> toIllegalArgument(() -> parameters.metricsFilterParameter.resolveFromString(metrics)));
        return client.sendRequest(address, port, JobMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<JobPlanInfo> jobPlan(String jobId) throws IOException {
        JobMessageParameters parameters = new JobMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        return client.sendRequest(address, port, JobPlanHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<TriggerResponse> jobRescale(String jobId, Integer parallelism) throws IOException {
        RescalingTriggerMessageParameters parameters = new RescalingTriggerMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.rescalingParallelismQueryParameter.resolveFromString(parallelism.toString()));
        return client.sendRequest(address, port, RescalingTriggerHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> jobRescaleResult(String jobId, String triggerId) throws IOException {
        RescalingStatusMessageParameters parameters = new RescalingStatusMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.triggerIdPathParameter.resolveFromString(triggerId));
        return client.sendRequest(address, port, RescalingStatusHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<TriggerResponse> jobSavepoint(String jobId, SavepointTriggerRequestBody requestBody) throws IOException {
        SavepointTriggerMessageParameters parameters = new SavepointTriggerMessageParameters();
        toIllegalArgument(() -> parameters.jobID.resolveFromString(jobId));
        return client.sendRequest(address, port, SavepointTriggerHeaders.getInstance(), parameters, requestBody);
    }

    @Override
    public CompletableFuture<AsynchronousOperationResult<SavepointInfo>> jobSavepointResult(String jobId, String triggerId) throws IOException {
        SavepointStatusMessageParameters parameters = new SavepointStatusMessageParameters();
        toIllegalArgument(() -> parameters.jobIdPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.triggerIdPathParameter.resolveFromString(triggerId));
        return client.sendRequest(address, port, SavepointStatusHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<TriggerResponse> jobStop(String jobId, StopWithSavepointRequestBody requestBody) throws IOException {
        SavepointTriggerMessageParameters parameters = new SavepointTriggerMessageParameters();
        toIllegalArgument(() -> parameters.jobID.resolveFromString(jobId));
        return client.sendRequest(address, port, StopWithSavepointTriggerHeaders.getInstance(), parameters, requestBody);
    }

    @Override
    public CompletableFuture<JobVertexDetailsInfo> jobVertexDetail(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        return client.sendRequest(address, port, JobVertexDetailsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<JobVertexAccumulatorsInfo> jobVertexAccumulators(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        return client.sendRequest(address, port, JobVertexAccumulatorsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<JobVertexBackPressureInfo> jobVertexBackPressure(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        return client.sendRequest(address, port, JobVertexBackPressureHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<JobVertexFlameGraph> jobVertexFlameGraph(String jobId, String vertexId, String type) throws IOException {
        JobVertexFlameGraphParameters parameters = new JobVertexFlameGraphParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        toIllegalArgument(() -> parameters.flameGraphTypeQueryParameter.resolveFromString(type));
        return client.sendRequest(address, port, JobVertexFlameGraphHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobVertexMetrics(String jobId, String vertexId, String get) throws IOException {
        JobVertexMetricsMessageParameters parameters = new JobVertexMetricsMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        toIllegalArgument(() -> parameters.metricsFilterParameter.resolveFromString(get));
        return client.sendRequest(address, port, JobVertexMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<SubtasksAllAccumulatorsInfo> jobVertexSubtaskAccumulators(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        return client.sendRequest(address, port, SubtasksAllAccumulatorsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobVertexSubtaskMetrics(String jobId, String vertexId, String get, String agg, String subtasks) throws IOException {
        SubtaskMetricsMessageParameters parameters = new SubtaskMetricsMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        toIllegalArgument(() -> parameters.metricsFilterParameter.resolveFromString(get));
        toIllegalArgument(() -> parameters.subtaskIndexPathParameter.resolveFromString(subtasks));
        return client.sendRequest(address, port, SubtaskMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<SubtaskExecutionAttemptDetailsInfo> jobVertexSubtaskDetail(String jobId, String vertexId, Integer subtaskindex) throws IOException {
        SubtaskMessageParameters parameters = new SubtaskMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        toIllegalArgument(() -> parameters.subtaskIndexPathParameter.resolveFromString(subtaskindex.toString()));
        return client.sendRequest(address, port, SubtaskCurrentAttemptDetailsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<SubtaskExecutionAttemptDetailsInfo> jobVertexSubtaskAttemptDetail(String jobId, String vertexId, Integer subtaskindex, Integer attempt) throws IOException {
        SubtaskAttemptMessageParameters parameters = new SubtaskAttemptMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        toIllegalArgument(() -> parameters.subtaskIndexPathParameter.resolveFromString(subtaskindex.toString()));
        toIllegalArgument(() -> parameters.subtaskAttemptPathParameter.resolveFromString(attempt.toString()));
        return client.sendRequest(address, port, SubtaskExecutionAttemptDetailsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<SubtaskExecutionAttemptAccumulatorsInfo> jobVertexSubtaskAttemptAccumulators(String jobId, String vertexId, Integer subtaskindex, Integer attempt) throws IOException {
        SubtaskAttemptMessageParameters parameters = new SubtaskAttemptMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        toIllegalArgument(() -> parameters.subtaskIndexPathParameter.resolveFromString(subtaskindex.toString()));
        toIllegalArgument(() -> parameters.subtaskAttemptPathParameter.resolveFromString(attempt.toString()));
        return client.sendRequest(address, port, SubtaskExecutionAttemptAccumulatorsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobVertexSubtaskMetrics(String jobId, String vertexId, Integer subtaskindex, String get) throws IOException {
        SubtaskMetricsMessageParameters parameters = new SubtaskMetricsMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        toIllegalArgument(() -> parameters.subtaskIndexPathParameter.resolveFromString(subtaskindex.toString()));
        toIllegalArgument(() -> parameters.metricsFilterParameter.resolveFromString(get));
        return client.sendRequest(address, port, SubtaskMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<SubtasksTimesInfo> jobVertexSubtaskTimes(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        return client.sendRequest(address, port, SubtasksTimesHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<JobVertexTaskManagersInfo> jobVertexTaskManagers(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        return client.sendRequest(address, port, JobVertexTaskManagersHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobVertexWatermarks(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        toIllegalArgument(() -> parameters.jobPathParameter.resolveFromString(jobId));
        toIllegalArgument(() -> parameters.jobVertexIdPathParameter.resolveFromString(vertexId));
        return client.sendRequest(address, port, JobVertexWatermarksHeaders.INSTANCE, parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<TriggerResponse> savepointDisposal(SavepointDisposalRequest request) throws IOException {
        return client.sendRequest(address, port, SavepointDisposalTriggerHeaders.getInstance(), EmptyMessageParameters.getInstance(), request);
    }

    @Override
    public CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> savepointDisposalResult(String triggerId) throws IOException {
        SavepointDisposalStatusMessageParameters parameters = new SavepointDisposalStatusMessageParameters();
        toIllegalArgument(() -> parameters.triggerIdPathParameter.resolveFromString(triggerId));
        return client.sendRequest(address, port, SavepointDisposalStatusHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<TaskManagersInfo> taskManagers() throws IOException {
        return client.sendRequest(address, port, TaskManagersHeaders.getInstance());
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> taskManagersMetrics(Optional<String> get, Optional<String> agg, Optional<String> taskmanagers) throws IOException {
        TaskManagerMetricsMessageParameters parameters = new TaskManagerMetricsMessageParameters();
        get.ifPresent(metrics -> toIllegalArgument(() -> parameters.metricsFilterParameter.resolveFromString(metrics)));
        taskmanagers.ifPresent(taskmanager -> toIllegalArgument(() -> parameters.taskManagerIdParameter.resolveFromString(taskmanager)));
        return client.sendRequest(address, port, TaskManagerMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<TaskManagerDetailsInfo> taskManagerDetail(String taskManagerId) throws IOException {
        TaskManagerMessageParameters parameters = new TaskManagerMessageParameters();
        toIllegalArgument(() -> parameters.taskManagerIdParameter.resolveFromString(taskManagerId));
        return client.sendRequest(address, port, TaskManagerDetailsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<LogListInfo> taskManagerLogs(String taskManagerId) throws IOException {
        TaskManagerMessageParameters parameters = new TaskManagerMessageParameters();
        toIllegalArgument(() -> parameters.taskManagerIdParameter.resolveFromString(taskManagerId));
        return client.sendRequest(address, port, TaskManagerLogsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> taskManagerMetrics(String taskManagerId, Optional<String> get) throws IOException {
        TaskManagerMetricsMessageParameters parameters = new TaskManagerMetricsMessageParameters();
        toIllegalArgument(() -> parameters.taskManagerIdParameter.resolveFromString(taskManagerId));
        get.ifPresent(metrics -> toIllegalArgument(() -> parameters.metricsFilterParameter.resolveFromString(metrics)));
        return client.sendRequest(address, port, TaskManagerMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }

    @Override
    public CompletableFuture<ThreadDumpInfo> taskManagerThreadDump(String taskManagerId) throws IOException {
        TaskManagerMessageParameters parameters = new TaskManagerMessageParameters();
        toIllegalArgument(() -> parameters.taskManagerIdParameter.resolveFromString(taskManagerId));
        return client.sendRequest(address, port, TaskManagerThreadDumpHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
    }
}
