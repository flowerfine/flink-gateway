package cn.sliew.flink.gateway.engine.endpoint.impl;

import cn.sliew.flink.gateway.engine.endpoint.RestEndpoint;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.messages.webmonitor.JobIdsWithStatusOverview;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
import org.apache.flink.runtime.rest.RestClient;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.handler.legacy.messages.ClusterOverviewWithVersion;
import org.apache.flink.runtime.rest.messages.*;
import org.apache.flink.runtime.rest.messages.checkpoints.CheckpointConfigInfo;
import org.apache.flink.runtime.rest.messages.checkpoints.CheckpointStatistics;
import org.apache.flink.runtime.rest.messages.checkpoints.CheckpointingStatistics;
import org.apache.flink.runtime.rest.messages.checkpoints.TaskCheckpointStatisticsWithSubtaskDetails;
import org.apache.flink.runtime.rest.messages.cluster.ShutdownHeaders;
import org.apache.flink.runtime.rest.messages.dataset.*;
import org.apache.flink.runtime.rest.messages.job.*;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointDisposalRequest;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointTriggerHeaders;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointTriggerMessageParameters;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointTriggerRequestBody;
import org.apache.flink.runtime.rest.messages.job.savepoints.stop.StopWithSavepointRequestBody;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagerDetailsInfo;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagersInfo;
import org.apache.flink.runtime.rest.messages.taskmanager.ThreadDumpInfo;
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
        return null;
    }

    @Override
    public JarListInfo jars() throws IOException {
        return null;
    }

    @Override
    public JarUploadResponseBody uploadJar(String filePath) throws IOException {
        return null;
    }

    @Override
    public boolean deleteJar(String jarId) throws IOException {
        return false;
    }

    @Override
    public JobPlanInfo jarPlan(String jarId, JarPlanRequestBody requestBody) throws IOException {
        return null;
    }

    @Override
    public JarRunResponseBody jarRun(String jarId, JarRunRequestBody requestBody) throws IOException {
        return null;
    }

    @Override
    public List<ClusterConfigurationInfoEntry> jobmanagerConfig() throws IOException {
        return null;
    }

    @Override
    public LogListInfo jobmanagerLogs() throws IOException {
        return null;
    }

    @Override
    public List<Map> jobmanagerMetrics(String get) throws IOException {
        return null;
    }

    @Override
    public MultipleJobsDetails jobsOverview() throws IOException {
        return null;
    }

    @Override
    public String jobsMetric(String get, String agg, String jobs) throws IOException {
        return null;
    }

    @Override
    public JobIdsWithStatusOverview jobs() throws IOException {
        return null;
    }

    @Override
    public JobDetailsInfo jobDetail(String jobId) throws IOException {
        return null;
    }

    @Override
    public JobSubmitResponseBody jobSubmit(JobSubmitRequestBody requestBody) throws IOException {
        return null;
    }

    @Override
    public boolean jobTerminate(String jobId, String mode) throws IOException {
        return false;
    }

    @Override
    public JobAccumulatorsInfo jobAccumulators(String jobId, Boolean includeSerializedValue) throws IOException {
        return null;
    }

    @Override
    public CheckpointingStatistics jobCheckpoints(String jobId) throws IOException {
        return null;
    }

    @Override
    public CheckpointConfigInfo jobCheckpointConfig(String jobId) throws IOException {
        return null;
    }

    @Override
    public CheckpointStatistics jobCheckpointDetail(String jobId, Long checkpointId) throws IOException {
        return null;
    }

    @Override
    public TaskCheckpointStatisticsWithSubtaskDetails jobCheckpointSubtaskDetail(String jobId, Long checkpointId, String vertexId) throws IOException {
        return null;
    }

    @Override
    public Map jobConfig(String jobId) throws IOException {
        return null;
    }

    @Override
    public JobExceptionsInfoWithHistory jobException(String jobId, String maxExceptions) throws IOException {
        return null;
    }

    @Override
    public JobExecutionResultResponseBody jobExecutionResult(String jobId) throws IOException {
        return null;
    }

    @Override
    public List<Map> jobMetrics(String jobId, String get) throws IOException {
        return null;
    }

    @Override
    public JobPlanInfo jobPlan(String jobId) throws IOException {
        return null;
    }

    @Override
    public TriggerResponse jobRescale(String jobId, Integer parallelism) throws IOException {
        return null;
    }

    @Override
    public AsynchronousOperationResult jobRescaleResult(String jobId, String triggerId) throws IOException {
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

        return null;
    }

    @Override
    public TriggerResponse jobStop(String jobId, StopWithSavepointRequestBody requestBody) throws IOException {
        return null;
    }

    @Override
    public JobVertexDetailsInfo jobVertexDetail(String jobId, String vertexId) throws IOException {
        return null;
    }

    @Override
    public JobVertexAccumulatorsInfo jobVertexAccumulators(String jobId, String vertexId) throws IOException {
        return null;
    }

    @Override
    public JobVertexBackPressureInfo jobVertexBackPressure(String jobId, String vertexId) throws IOException {
        return null;
    }

    @Override
    public JobVertexFlameGraph jobVertexFlameGraph(String jobId, String vertexId, String type) throws IOException {
        return null;
    }

    @Override
    public String jobVertexMetrics(String jobId, String vertexId, String get) throws IOException {
        return null;
    }

    @Override
    public SubtasksAllAccumulatorsInfo jobVertexSubtaskAccumulators(String jobId, String vertexId) throws IOException {
        return null;
    }

    @Override
    public String jobVertexSubtaskMetrics(String jobId, String vertexId, String get, String agg, String subtasks) throws IOException {
        return null;
    }

    @Override
    public SubtaskExecutionAttemptDetailsInfo jobVertexSubtaskDetail(String jobId, String vertexId, Integer subtaskindex) throws IOException {
        return null;
    }

    @Override
    public SubtaskExecutionAttemptDetailsInfo jobVertexSubtaskAttemptDetail(String jobId, String vertexId, Integer subtaskindex, Integer attempt) throws IOException {
        return null;
    }

    @Override
    public SubtaskExecutionAttemptAccumulatorsInfo jobVertexSubtaskAttemptAccumulators(String jobId, String vertexId, Integer subtaskindex, Integer attempt) throws IOException {
        return null;
    }

    @Override
    public String jobVertexSubtaskMetrics(String jobId, String vertexId, Integer subtaskindex, String get) throws IOException {
        return null;
    }

    @Override
    public SubtasksTimesInfo jobVertexSubtaskTimes(String jobId, String vertexId) throws IOException {
        return null;
    }

    @Override
    public JobVertexTaskManagersInfo jobVertexTaskManagers(String jobId, String vertexId) throws IOException {
        return null;
    }

    @Override
    public String jobVertexWatermarks(String jobId, String vertexId) throws IOException {
        return null;
    }

    @Override
    public TriggerResponse savepointDisposal(SavepointDisposalRequest request) throws IOException {
        return null;
    }

    @Override
    public AsynchronousOperationResult savepointDisposalResult(String triggerId) throws IOException {
        return null;
    }

    @Override
    public TaskManagersInfo taskManagers() throws IOException {
        return null;
    }

    @Override
    public List<Map> taskManagersMetrics(String get, String agg, String taskmanagers) throws IOException {
        return null;
    }

    @Override
    public TaskManagerDetailsInfo taskManagerDetail(String taskManagerId) throws IOException {
        return null;
    }

    @Override
    public LogListInfo taskManagerLogs(String taskManagerId) throws IOException {
        return null;
    }

    @Override
    public List<Map> taskManagerMetrics(String taskManagerId, String get) throws IOException {
        return null;
    }

    @Override
    public ThreadDumpInfo taskManagerThreadDump(String taskManagerId) throws IOException {
        return null;
    }
}
