package cn.sliew.flink.gateway.engine.endpoint.impl;

import cn.sliew.flink.gateway.engine.endpoint.RestEndpoint;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.messages.webmonitor.JobIdsWithStatusOverview;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
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
import org.apache.flink.runtime.webmonitor.handlers.*;
import org.apache.flink.runtime.webmonitor.threadinfo.JobVertexFlameGraph;
import org.apache.flink.util.ConfigurationException;
import org.apache.flink.util.concurrent.ExecutorThreadFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestEndpointImpl2 implements RestEndpoint {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4, new ExecutorThreadFactory("Flink-RestClusterClient-IO"));
    private final RestClient client;

    private final String address = "localhost";
    private final int port = 8081;

    public RestEndpointImpl2(Configuration configuration) {
        try {
            this.client = new RestClient(configuration, executorService);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @Override
    public DashboardConfiguration config() throws IOException {
        CompletableFuture<DashboardConfiguration> future = client.sendRequest(address, port, DashboardConfigurationHeaders.getInstance());
        try {
            DashboardConfiguration dashboardConfiguration = future.get();

            return dashboardConfiguration;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ClusterOverviewWithVersion overview() throws IOException {
        CompletableFuture<ClusterOverviewWithVersion> future = client.sendRequest(address, port, ClusterOverviewHeaders.getInstance());
        return null;
    }

    @Override
    public boolean shutdownCluster() throws IOException {
        final CompletableFuture<EmptyResponseBody> future = client.sendRequest(address, port, ShutdownHeaders.getInstance());
        return false;
    }

    @Override
    public ClusterDataSetListResponseBody datasets() throws IOException {
        final CompletableFuture<ClusterDataSetListResponseBody> future = client.sendRequest(address, port, ClusterDataSetListHeaders.INSTANCE);
        return null;
    }

    @Override
    public TriggerResponse deleteDataSet(String datasetId) throws IOException {
        ClusterDataSetDeleteTriggerMessageParameters parameters = new ClusterDataSetDeleteTriggerMessageParameters();
        parameters.clusterDataSetIdPathParameter.resolveFromString(datasetId);
        CompletableFuture<TriggerResponse> future = client.sendRequest(address, port, ClusterDataSetDeleteTriggerHeaders.INSTANCE, parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public AsynchronousOperationResult deleteDataSetStatus(String triggerId) throws IOException {
        ClusterDataSetDeleteStatusMessageParameters parameters = new ClusterDataSetDeleteStatusMessageParameters();
        parameters.triggerIdPathParameter.resolveFromString(triggerId);
        CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> future = client.sendRequest(address, port, ClusterDataSetDeleteStatusHeaders.INSTANCE, parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public JarListInfo jars() throws IOException {
        final CompletableFuture<JarListInfo> future = client.sendRequest(address, port, JarListHeaders.getInstance());
        return null;
    }

    @Override
    public JarUploadResponseBody uploadJar(String filePath) throws IOException {

        return null;
    }

    @Override
    public boolean deleteJar(String jarId) throws IOException {
        JarDeleteMessageParameters parameters = new JarDeleteMessageParameters();
        parameters.jarIdPathParameter.resolveFromString(jarId);
        CompletableFuture<EmptyResponseBody> future = client.sendRequest(address, port, JarDeleteHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return false;
    }

    @Override
    public JobPlanInfo jarPlan(String jarId, JarPlanRequestBody requestBody) throws IOException {
        JarPlanMessageParameters parameters = new JarPlanMessageParameters();
        parameters.jarIdPathParameter.resolveFromString(jarId);
        CompletableFuture<JobPlanInfo> future = client.sendRequest(address, port, JarPlanGetHeaders.getInstance(), parameters, requestBody);
        return null;
    }

    @Override
    public JarRunResponseBody jarRun(String jarId, JarRunRequestBody requestBody) throws IOException {
        JarRunMessageParameters parameters = new JarRunMessageParameters();
        parameters.jarIdPathParameter.resolveFromString(jarId);
        CompletableFuture<JarRunResponseBody> future = client.sendRequest(address, port, JarRunHeaders.getInstance(), parameters, requestBody);
        return null;
    }

    @Override
    public List<ClusterConfigurationInfoEntry> jobmanagerConfig() throws IOException {
        CompletableFuture<ClusterConfigurationInfo> future = client.sendRequest(address, port, ClusterConfigurationInfoHeaders.getInstance());
        return null;
    }

    @Override
    public LogListInfo jobmanagerLogs() throws IOException {
        CompletableFuture<LogListInfo> future = client.sendRequest(address, port, JobManagerLogListHeaders.getInstance());
        return null;
    }

    @Override
    public List<Map> jobmanagerMetrics(String get) throws IOException {
        JobManagerMetricsMessageParameters parameters = new JobManagerMetricsMessageParameters();
        parameters.metricsFilterParameter.resolveFromString(get);
        CompletableFuture<MetricCollectionResponseBody> future = client.sendRequest(address, port, JobManagerMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public MultipleJobsDetails jobsOverview() throws IOException {
        CompletableFuture<MultipleJobsDetails> future = client.sendRequest(address, port, JobsOverviewHeaders.getInstance());
        return null;
    }

    @Override
    public String jobsMetric(String get, String agg, String jobs) throws IOException {
        JobMetricsMessageParameters parameters = new JobMetricsMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobs);
        parameters.metricsFilterParameter.resolveFromString(get);
        CompletableFuture<MetricCollectionResponseBody> future = client.sendRequest(address, port, JobMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public JobIdsWithStatusOverview jobs() throws IOException {
        CompletableFuture<JobIdsWithStatusOverview> future = client.sendRequest(address, port, JobIdsWithStatusesOverviewHeaders.getInstance());
        return null;
    }

    @Override
    public JobDetailsInfo jobDetail(String jobId) throws IOException {
        JobMessageParameters parameters = new JobMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        CompletableFuture<JobDetailsInfo> future = client.sendRequest(address, port, JobDetailsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public JobSubmitResponseBody jobSubmit(JobSubmitRequestBody requestBody) throws IOException {
        final CompletableFuture<JobSubmitResponseBody> future = client.sendRequest(address, port, JobSubmitHeaders.getInstance(), EmptyMessageParameters.getInstance(), requestBody);
        return null;
    }

    @Override
    public boolean jobTerminate(String jobId, String mode) throws IOException {
        JobCancellationMessageParameters parameters = new JobCancellationMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.terminationModeQueryParameter.resolveFromString(mode);
        CompletableFuture<EmptyResponseBody> future = client.sendRequest(address, port, JobCancellationHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return false;
    }

    @Override
    public JobAccumulatorsInfo jobAccumulators(String jobId, Boolean includeSerializedValue) throws IOException {
        JobAccumulatorsMessageParameters parameters = new JobAccumulatorsMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.includeSerializedAccumulatorsParameter.resolveFromString(includeSerializedValue.toString());
        CompletableFuture<JobAccumulatorsInfo> future = client.sendRequest(address, port, JobAccumulatorsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public CheckpointingStatistics jobCheckpoints(String jobId) throws IOException {
        JobMessageParameters parameters = new JobMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        CompletableFuture<CheckpointingStatistics> future = client.sendRequest(address, port, CheckpointingStatisticsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public CheckpointConfigInfo jobCheckpointConfig(String jobId) throws IOException {
        JobMessageParameters parameters = new JobMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        CompletableFuture<CheckpointConfigInfo> future = client.sendRequest(address, port, CheckpointConfigHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public CheckpointStatistics jobCheckpointDetail(String jobId, Long checkpointId) throws IOException {
        CheckpointMessageParameters parameters = new CheckpointMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.checkpointIdPathParameter.resolveFromString(checkpointId.toString());
        CompletableFuture<CheckpointStatistics> future = client.sendRequest(address, port, CheckpointStatisticDetailsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public TaskCheckpointStatisticsWithSubtaskDetails jobCheckpointSubtaskDetail(String jobId, Long checkpointId, String vertexId) throws IOException {
        TaskCheckpointMessageParameters parameters = new TaskCheckpointMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.checkpointIdPathParameter.resolveFromString(checkpointId.toString());
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        CompletableFuture<TaskCheckpointStatisticsWithSubtaskDetails> future = client.sendRequest(address, port, TaskCheckpointStatisticsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public Map jobConfig(String jobId) throws IOException {
        JobMessageParameters parameters = new JobMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        CompletableFuture<JobConfigInfo> future = client.sendRequest(address, port, JobConfigHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public JobExceptionsInfoWithHistory jobException(String jobId, String maxExceptions) throws IOException {
        JobExceptionsMessageParameters parameters = new JobExceptionsMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.upperLimitExceptionParameter.resolveFromString(maxExceptions);
        CompletableFuture<JobExceptionsInfoWithHistory> future = client.sendRequest(address, port, JobExceptionsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public JobExecutionResultResponseBody jobExecutionResult(String jobId) throws IOException {
        JobMessageParameters parameters = new JobMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        CompletableFuture<JobExecutionResultResponseBody> future = client.sendRequest(address, port, JobExecutionResultHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public List<Map> jobMetrics(String jobId, String get) throws IOException {
        JobMetricsMessageParameters parameters = new JobMetricsMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.metricsFilterParameter.resolveFromString(get);
        CompletableFuture<MetricCollectionResponseBody> future = client.sendRequest(address, port, JobMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public JobPlanInfo jobPlan(String jobId) throws IOException {
        JobMessageParameters parameters = new JobMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        CompletableFuture<JobPlanInfo> future = client.sendRequest(address, port, JobPlanHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public TriggerResponse jobRescale(String jobId, Integer parallelism) throws IOException {
        RescalingTriggerMessageParameters parameters = new RescalingTriggerMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.rescalingParallelismQueryParameter.resolveFromString(parallelism.toString());
        CompletableFuture<TriggerResponse> future = client.sendRequest(address, port, RescalingTriggerHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public AsynchronousOperationResult jobRescaleResult(String jobId, String triggerId) throws IOException {
        RescalingStatusMessageParameters parameters = new RescalingStatusMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.triggerIdPathParameter.resolveFromString(triggerId);
        CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> future = client.sendRequest(address, port, RescalingStatusHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public TriggerResponse jobSavepoint(String jobId, SavepointTriggerRequestBody requestBody) throws IOException {
        SavepointTriggerMessageParameters parameters = new SavepointTriggerMessageParameters();
        parameters.jobID.resolveFromString(jobId);
        final CompletableFuture<TriggerResponse> future = client.sendRequest(address, port, SavepointTriggerHeaders.getInstance(), parameters, requestBody);
        return null;
    }

    @Override
    public AsynchronousOperationResult jobSavepointResult(String jobId, String triggerId) throws IOException {
        SavepointStatusMessageParameters parameters = new SavepointStatusMessageParameters();
        parameters.jobIdPathParameter.resolveFromString(jobId);
        parameters.triggerIdPathParameter.resolveFromString(triggerId);
        CompletableFuture<AsynchronousOperationResult<SavepointInfo>> future = client.sendRequest(address, port, SavepointStatusHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public TriggerResponse jobStop(String jobId, StopWithSavepointRequestBody requestBody) throws IOException {
        SavepointTriggerMessageParameters parameters = new SavepointTriggerMessageParameters();
        parameters.jobID.resolveFromString(jobId);
        CompletableFuture<TriggerResponse> future = client.sendRequest(address, port, StopWithSavepointTriggerHeaders.getInstance(), parameters, requestBody);
        return null;
    }

    @Override
    public JobVertexDetailsInfo jobVertexDetail(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        CompletableFuture<JobVertexDetailsInfo> future = client.sendRequest(address, port, JobVertexDetailsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public JobVertexAccumulatorsInfo jobVertexAccumulators(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        CompletableFuture<JobVertexAccumulatorsInfo> future = client.sendRequest(address, port, JobVertexAccumulatorsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public JobVertexBackPressureInfo jobVertexBackPressure(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        CompletableFuture<JobVertexBackPressureInfo> future = client.sendRequest(address, port, JobVertexBackPressureHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public JobVertexFlameGraph jobVertexFlameGraph(String jobId, String vertexId, String type) throws IOException {
        JobVertexFlameGraphParameters parameters = new JobVertexFlameGraphParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        parameters.flameGraphTypeQueryParameter.resolveFromString(type);
        CompletableFuture<JobVertexFlameGraph> future = client.sendRequest(address, port, JobVertexFlameGraphHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public String jobVertexMetrics(String jobId, String vertexId, String get) throws IOException {
        JobVertexMetricsMessageParameters parameters = new JobVertexMetricsMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        parameters.metricsFilterParameter.resolveFromString(get);
        CompletableFuture<MetricCollectionResponseBody> future = client.sendRequest(address, port, JobVertexMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public SubtasksAllAccumulatorsInfo jobVertexSubtaskAccumulators(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        CompletableFuture<SubtasksAllAccumulatorsInfo> future = client.sendRequest(address, port, SubtasksAllAccumulatorsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public String jobVertexSubtaskMetrics(String jobId, String vertexId, String get, String agg, String subtasks) throws IOException {
        SubtaskMetricsMessageParameters parameters = new SubtaskMetricsMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        parameters.metricsFilterParameter.resolveFromString(get);
        parameters.subtaskIndexPathParameter.resolveFromString(subtasks);
        CompletableFuture<MetricCollectionResponseBody> future = client.sendRequest(address, port, SubtaskMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public SubtaskExecutionAttemptDetailsInfo jobVertexSubtaskDetail(String jobId, String vertexId, Integer subtaskindex) throws IOException {
        SubtaskMessageParameters parameters = new SubtaskMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        parameters.subtaskIndexPathParameter.resolveFromString(subtaskindex.toString());
        CompletableFuture<SubtaskExecutionAttemptDetailsInfo> future = client.sendRequest(address, port, SubtaskCurrentAttemptDetailsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public SubtaskExecutionAttemptDetailsInfo jobVertexSubtaskAttemptDetail(String jobId, String vertexId, Integer subtaskindex, Integer attempt) throws IOException {
        SubtaskAttemptMessageParameters parameters = new SubtaskAttemptMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        parameters.subtaskIndexPathParameter.resolveFromString(subtaskindex.toString());
        parameters.subtaskAttemptPathParameter.resolveFromString(attempt.toString());
        CompletableFuture<SubtaskExecutionAttemptDetailsInfo> future = client.sendRequest(address, port, SubtaskExecutionAttemptDetailsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public SubtaskExecutionAttemptAccumulatorsInfo jobVertexSubtaskAttemptAccumulators(String jobId, String vertexId, Integer subtaskindex, Integer attempt) throws IOException {
        SubtaskAttemptMessageParameters parameters = new SubtaskAttemptMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        parameters.subtaskIndexPathParameter.resolveFromString(subtaskindex.toString());
        parameters.subtaskAttemptPathParameter.resolveFromString(attempt.toString());
        CompletableFuture<SubtaskExecutionAttemptAccumulatorsInfo> future = client.sendRequest(address, port, SubtaskExecutionAttemptAccumulatorsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public String jobVertexSubtaskMetrics(String jobId, String vertexId, Integer subtaskindex, String get) throws IOException {
        SubtaskMetricsMessageParameters parameters = new SubtaskMetricsMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        parameters.subtaskIndexPathParameter.resolveFromString(subtaskindex);
        parameters.metricsFilterParameter.resolveFromString(get);
        CompletableFuture<MetricCollectionResponseBody> future = client.sendRequest(address, port, SubtaskMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public SubtasksTimesInfo jobVertexSubtaskTimes(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        CompletableFuture<SubtasksTimesInfo> future = client.sendRequest(address, port, SubtasksTimesHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public JobVertexTaskManagersInfo jobVertexTaskManagers(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        CompletableFuture<JobVertexTaskManagersInfo> future = client.sendRequest(address, port, JobVertexTaskManagersHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public String jobVertexWatermarks(String jobId, String vertexId) throws IOException {
        JobVertexMessageParameters parameters = new JobVertexMessageParameters();
        parameters.jobPathParameter.resolveFromString(jobId);
        parameters.jobVertexIdPathParameter.resolveFromString(vertexId);
        CompletableFuture<MetricCollectionResponseBody> future = client.sendRequest(address, port, JobVertexWatermarksHeaders.INSTANCE, parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public TriggerResponse savepointDisposal(SavepointDisposalRequest request) throws IOException {
        CompletableFuture<TriggerResponse> future = client.sendRequest(address, port, SavepointDisposalTriggerHeaders.getInstance(), EmptyMessageParameters.getInstance(), request);
        return null;
    }

    @Override
    public AsynchronousOperationResult savepointDisposalResult(String triggerId) throws IOException {
        SavepointDisposalStatusMessageParameters parameters = new SavepointDisposalStatusMessageParameters();
        parameters.triggerIdPathParameter.resolveFromString(triggerId);
        CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> future = client.sendRequest(address, port, SavepointDisposalStatusHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public TaskManagersInfo taskManagers() throws IOException {
        CompletableFuture<TaskManagersInfo> future = client.sendRequest(address, port, TaskManagersHeaders.getInstance());
        return null;
    }

    @Override
    public List<Map> taskManagersMetrics(String get, String agg, String taskmanagers) throws IOException {
        TaskManagerMetricsMessageParameters parameters = new TaskManagerMetricsMessageParameters();
        parameters.metricsFilterParameter.resolveFromString(get);
        parameters.taskManagerIdParameter.resolveFromString(taskmanagers);
        CompletableFuture<MetricCollectionResponseBody> future = client.sendRequest(address, port, TaskManagerMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public TaskManagerDetailsInfo taskManagerDetail(String taskManagerId) throws IOException {
        TaskManagerMessageParameters parameters = new TaskManagerMessageParameters();
        parameters.taskManagerIdParameter.resolveFromString(taskManagerId);
        CompletableFuture<TaskManagerDetailsInfo> future = client.sendRequest(address, port, TaskManagerDetailsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public LogListInfo taskManagerLogs(String taskManagerId) throws IOException {
        TaskManagerMessageParameters parameters = new TaskManagerMessageParameters();
        parameters.taskManagerIdParameter.resolveFromString(taskManagerId);
        CompletableFuture<LogListInfo> future = client.sendRequest(address, port, TaskManagerLogsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public List<Map> taskManagerMetrics(String taskManagerId, String get) throws IOException {
        TaskManagerMetricsMessageParameters parameters = new TaskManagerMetricsMessageParameters();
        parameters.taskManagerIdParameter.resolveFromString(taskManagerId);
        parameters.metricsFilterParameter.resolveFromString(get);
        CompletableFuture<MetricCollectionResponseBody> future = client.sendRequest(address, port, TaskManagerMetricsHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }

    @Override
    public ThreadDumpInfo taskManagerThreadDump(String taskManagerId) throws IOException {
        TaskManagerMessageParameters parameters = new TaskManagerMessageParameters();
        parameters.taskManagerIdParameter.resolveFromString(taskManagerId);
        CompletableFuture<ThreadDumpInfo> future = client.sendRequest(address, port, TaskManagerThreadDumpHeaders.getInstance(), parameters, EmptyRequestBody.getInstance());
        return null;
    }
}
