package cn.sliew.flink.gateway.engine.endpoint.impl;

import cn.sliew.flink.gateway.engine.endpoint.RestEndpoint;
import cn.sliew.flink.gateway.engine.util.FlinkShadedJacksonUtil;
import cn.sliew.milky.common.check.Ensures;
import cn.sliew.milky.common.exception.Rethrower;
import cn.sliew.milky.common.util.StringUtils;
import okhttp3.RequestBody;
import okhttp3.*;
import okhttp3.internal.Util;
import org.apache.flink.runtime.messages.webmonitor.JobIdsWithStatusOverview;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationInfo;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.handler.legacy.messages.ClusterOverviewWithVersion;
import org.apache.flink.runtime.rest.messages.*;
import org.apache.flink.runtime.rest.messages.checkpoints.CheckpointConfigInfo;
import org.apache.flink.runtime.rest.messages.checkpoints.CheckpointStatistics;
import org.apache.flink.runtime.rest.messages.checkpoints.CheckpointingStatistics;
import org.apache.flink.runtime.rest.messages.checkpoints.TaskCheckpointStatisticsWithSubtaskDetails;
import org.apache.flink.runtime.rest.messages.dataset.ClusterDataSetListResponseBody;
import org.apache.flink.runtime.rest.messages.job.*;
import org.apache.flink.runtime.rest.messages.job.metrics.MetricCollectionResponseBody;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointDisposalRequest;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointInfo;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointTriggerRequestBody;
import org.apache.flink.runtime.rest.messages.job.savepoints.stop.StopWithSavepointRequestBody;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagerDetailsInfo;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagersInfo;
import org.apache.flink.runtime.rest.messages.taskmanager.ThreadDumpInfo;
import org.apache.flink.runtime.rest.util.RestConstants;
import org.apache.flink.runtime.rest.versioning.RestAPIVersion;
import org.apache.flink.runtime.webmonitor.handlers.*;
import org.apache.flink.runtime.webmonitor.threadinfo.JobVertexFlameGraph;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RestEndpointImpl implements RestEndpoint {

    public static final MediaType APPLICATION_JSON = MediaType.get(RestConstants.REST_CONTENT_TYPE);

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(3L))
            .readTimeout(Duration.ofSeconds(3L))
            .writeTimeout(Duration.ofSeconds(3L))
            .callTimeout(Duration.ofSeconds(3L))
            .addInterceptor(new LogInterceptor())
            .build();


    private final String webInterfaceURL;

    public RestEndpointImpl(String webInterfaceURL) {
        this.webInterfaceURL = webInterfaceURL + "/" + RestAPIVersion.V1.getURLVersionPrefix();
    }

    private CompletableFuture<EmptyResponseBody> remoteCall(Request request) throws IOException {
        FutureResponse future = new FutureResponse();
        client.newCall(request).enqueue(future);
        return map(future, json -> EmptyResponseBody.getInstance());
    }

    private <Out, In> CompletableFuture<Out> remoteCall(Request request, Class<Out> outClass, Class<In> parameterClasses) throws IOException {
        FutureResponse future = new FutureResponse();
        client.newCall(request).enqueue(future);
        return map(future, json -> FlinkShadedJacksonUtil.parseJsonString(json, outClass, parameterClasses));
    }

    private <T> CompletableFuture<T> remoteCall(Request request, Class<T> responseClass) throws IOException {
        FutureResponse future = new FutureResponse();
        client.newCall(request).enqueue(future);
        return map(future, json -> FlinkShadedJacksonUtil.parseJsonString(json, responseClass));
    }

    private <T> CompletableFuture<T> map(FutureResponse future, Function<String, T> parser) {
        return future.future.thenApply(response -> {
            try {
                checkStatus(response);
                return parser.apply(response.body().string());
            } catch (IOException e) {
                Rethrower.throwAs(e);
                return null;
            } finally {
                response.close();
            }
        });
    }

    @Override
    public CompletableFuture<DashboardConfiguration> config() throws IOException {
        String url = webInterfaceURL + "/config";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, DashboardConfiguration.class);
    }

    @Override
    public CompletableFuture<ClusterOverviewWithVersion> overview() throws IOException {
        String url = webInterfaceURL + "/overview";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, ClusterOverviewWithVersion.class);
    }

    @Override
    public CompletableFuture<EmptyResponseBody> shutdownCluster() throws IOException {
        String url = webInterfaceURL + "/cluster";
        Request request = new Request.Builder()
                .delete()
                .url(url)
                .build();
        return remoteCall(request);
    }

    @Override
    public CompletableFuture<ClusterDataSetListResponseBody> datasets() throws IOException {
        String url = webInterfaceURL + "/datasets";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, ClusterDataSetListResponseBody.class);
    }

    @Override
    public CompletableFuture<TriggerResponse> deleteDataSet(String datasetId) throws IOException {
        String url = webInterfaceURL + "/datasets/" + datasetId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, TriggerResponse.class);
    }

    @Override
    public CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> deleteDataSetStatus(String triggerId) throws IOException {
        String url = webInterfaceURL + "/datasets/delete/" + triggerId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, AsynchronousOperationResult.class, AsynchronousOperationInfo.class).thenApply(result -> {
            AsynchronousOperationResult<AsynchronousOperationInfo> type = result;
            return type;
        });
    }

    @Override
    public CompletableFuture<JarListInfo> jars() throws IOException {
        String url = webInterfaceURL + "/jars";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, JarListInfo.class);
    }

    @Override
    public CompletableFuture<JarUploadResponseBody> uploadJar(String filePath) throws IOException {
        String url = webInterfaceURL + "/jars/upload";
        File jarFile = new File(filePath);
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("jarfile", jarFile.getName(), RequestBody.create(jarFile, MediaType.get(RestConstants.CONTENT_TYPE_JAR)))
                .build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        return remoteCall(request, JarUploadResponseBody.class);
    }

    @Override
    public CompletableFuture<EmptyResponseBody> deleteJar(String jarId) throws IOException {
        String url = webInterfaceURL + "/jars/" + jarId;
        Request request = new Request.Builder()
                .delete()
                .url(url)
                .build();
        return remoteCall(request);
    }

    @Override
    public CompletableFuture<JobPlanInfo> jarPlan(String jarId, JarPlanRequestBody requestBody) throws IOException {
        String url = webInterfaceURL + "/jars/" + jarId + "/plan";
        RequestBody body = RequestBody.create(FlinkShadedJacksonUtil.toJsonString(requestBody), APPLICATION_JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        return remoteCall(request, JobPlanInfo.class);
    }

    @Override
    public CompletableFuture<JarRunResponseBody> jarRun(String jarId, JarRunRequestBody requestBody) throws IOException {
        String url = webInterfaceURL + "/jars/" + jarId + "/run";
        RequestBody body = RequestBody.create(FlinkShadedJacksonUtil.toJsonString(requestBody), APPLICATION_JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        return remoteCall(request, JarRunResponseBody.class);
    }

    @Override
    public CompletableFuture<ClusterConfigurationInfo> jobmanagerConfig() throws IOException {
        String url = webInterfaceURL + "/jobmanager/config";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, ClusterConfigurationInfo.class);
    }

    @Override
    public CompletableFuture<LogListInfo> jobmanagerLogs() throws IOException {
        String url = webInterfaceURL + "/jobmanager/logs";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, LogListInfo.class);
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobmanagerMetrics(Optional<String> get) throws IOException {
        String url = webInterfaceURL + "/jobmanager/metrics";
        if (get.isPresent()) {
            url = url + "?get=" + get.get();
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, MetricCollectionResponseBody.class);
    }

    @Override
    public CompletableFuture<MultipleJobsDetails> jobsOverview() throws IOException {
        String url = webInterfaceURL + "/jobs/overview";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, MultipleJobsDetails.class);
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobsMetric(String get, String agg, String jobs) throws IOException {
        return null;
    }

    @Override
    public CompletableFuture<JobIdsWithStatusOverview> jobs() throws IOException {
        String url = webInterfaceURL + "/jobs";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, JobIdsWithStatusOverview.class);
    }

    @Override
    public CompletableFuture<JobDetailsInfo> jobDetail(String jobId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, JobDetailsInfo.class);
    }

    @Override
    public CompletableFuture<JobSubmitResponseBody> jobSubmit(JobSubmitRequestBody requestBody) throws IOException {
        String url = webInterfaceURL + "/jobs";
        RequestBody body = RequestBody.create(FlinkShadedJacksonUtil.toJsonString(requestBody), APPLICATION_JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        return remoteCall(request, JobSubmitResponseBody.class);
    }

    @Override
    public CompletableFuture<EmptyResponseBody> jobTerminate(String jobId, String mode) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId;
        if (StringUtils.isNotBlank(mode)) {
            url = url + "?mode=" + mode;
        }
        Request request = new Request.Builder()
                .patch(Util.EMPTY_REQUEST)
                .url(url)
                .build();
        return remoteCall(request);
    }

    @Override
    public CompletableFuture<JobAccumulatorsInfo> jobAccumulators(String jobId, Boolean includeSerializedValue) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/accumulators";
        if (includeSerializedValue != null) {
            url = url + "?includeSerializedValue=" + includeSerializedValue;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, JobAccumulatorsInfo.class);
    }

    @Override
    public CompletableFuture<CheckpointingStatistics> jobCheckpoints(String jobId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/checkpoints";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, CheckpointingStatistics.class);
    }

    @Override
    public CompletableFuture<CheckpointConfigInfo> jobCheckpointConfig(String jobId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/checkpoints/config";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, CheckpointConfigInfo.class);
    }

    @Override
    public CompletableFuture<CheckpointStatistics> jobCheckpointDetail(String jobId, Long checkpointId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/checkpoints/details/" + checkpointId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, CheckpointStatistics.class);
    }

    @Override
    public CompletableFuture<TaskCheckpointStatisticsWithSubtaskDetails> jobCheckpointSubtaskDetail(String jobId, Long checkpointId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/checkpoints/details/" + checkpointId + "/subtasks/" + vertexId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, TaskCheckpointStatisticsWithSubtaskDetails.class);
    }

    @Override
    public CompletableFuture<JobConfigInfo> jobConfig(String jobId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/config";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, JobConfigInfo.class);
    }

    @Override
    public CompletableFuture<JobExceptionsInfoWithHistory> jobException(String jobId, String maxExceptions) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/exceptions";
        if (StringUtils.isNotBlank(maxExceptions)) {
            url = url + "?maxExceptions=" + maxExceptions;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, JobExceptionsInfoWithHistory.class);
    }

    @Override
    public CompletableFuture<JobExecutionResultResponseBody> jobExecutionResult(String jobId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/execution-result";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, JobExecutionResultResponseBody.class);
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobMetrics(String jobId, String get) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/metrics";
        if (StringUtils.isNotBlank(get)) {
            url = url + "?get=" + get;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, MetricCollectionResponseBody.class);
    }

    @Override
    public CompletableFuture<JobPlanInfo> jobPlan(String jobId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/plan";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, JobPlanInfo.class);
    }

    @Override
    public CompletableFuture<TriggerResponse> jobRescale(String jobId, Integer parallelism) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/rescaling";
        Ensures.checkNotNull(parallelism, () -> "parallelism can't be null");
        Ensures.checkArgument(parallelism > 0, () -> "parallelism must be positive integer");
        Request request = new Request.Builder()
                .patch(Util.EMPTY_REQUEST)
                .url(url)
                .build();
        return remoteCall(request, TriggerResponse.class);
    }

    @Override
    public CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> jobRescaleResult(String jobId, String triggerId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/rescaling/" + triggerId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, AsynchronousOperationResult.class, AsynchronousOperationInfo.class).thenApply(result -> {
            AsynchronousOperationResult<AsynchronousOperationInfo> type = result;
            return type;
        });
    }

    @Override
    public CompletableFuture<TriggerResponse> jobSavepoint(String jobId, SavepointTriggerRequestBody requestBody) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/savepoints";
        RequestBody body = RequestBody.create(FlinkShadedJacksonUtil.toJsonString(requestBody), APPLICATION_JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        return remoteCall(request, TriggerResponse.class);
    }

    @Override
    public CompletableFuture<AsynchronousOperationResult<SavepointInfo>> jobSavepointResult(String jobId, String triggerId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/savepoints/" + triggerId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, AsynchronousOperationResult.class, SavepointInfo.class).thenApply(result -> {
            AsynchronousOperationResult<SavepointInfo> type = result;
            return type;
        });
    }

    @Override
    public CompletableFuture<TriggerResponse> jobStop(String jobId, StopWithSavepointRequestBody requestBody) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/stop";
        RequestBody body = RequestBody.create(FlinkShadedJacksonUtil.toJsonString(requestBody), APPLICATION_JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        return remoteCall(request, TriggerResponse.class);
    }

    @Override
    public CompletableFuture<JobVertexDetailsInfo> jobVertexDetail(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, JobVertexDetailsInfo.class);
    }

    @Override
    public CompletableFuture<JobVertexAccumulatorsInfo> jobVertexAccumulators(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/accumulators";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, JobVertexAccumulatorsInfo.class);
    }

    @Override
    public CompletableFuture<JobVertexBackPressureInfo> jobVertexBackPressure(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/backpressure";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, JobVertexBackPressureInfo.class);
    }

    @Override
    public CompletableFuture<JobVertexFlameGraph> jobVertexFlameGraph(String jobId, String vertexId, String type) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/flamegraph";
        if (StringUtils.isNotBlank(type)) {
            url = url + "?type=" + type;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, JobVertexFlameGraph.class);
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobVertexMetrics(String jobId, String vertexId, String get) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/metrics";
        if (StringUtils.isNotBlank(get)) {
            url = url + "?get=" + get;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, MetricCollectionResponseBody.class);
    }

    @Override
    public CompletableFuture<SubtasksAllAccumulatorsInfo> jobVertexSubtaskAccumulators(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/subtasks/accumulators";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, SubtasksAllAccumulatorsInfo.class);
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobVertexSubtaskMetrics(String jobId, String vertexId, String get, String agg, String subtasks) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/subtasks/metrics";
        List<String> queryParams = new LinkedList<>();
        if (StringUtils.isNotBlank(get)) {
            queryParams.add("get=" + get);
        }
        if (StringUtils.isNotBlank(agg)) {
            queryParams.add("agg=" + agg);
        }
        if (StringUtils.isNotBlank(subtasks)) {
            queryParams.add("subtasks=" + subtasks);
        }
        if (queryParams.isEmpty() == false) {
            String params = queryParams.stream().collect(Collectors.joining("&"));
            url = url + "?" + params;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, MetricCollectionResponseBody.class);
    }

    @Override
    public CompletableFuture<SubtaskExecutionAttemptDetailsInfo> jobVertexSubtaskDetail(String jobId, String vertexId, Integer subtaskindex) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/subtasks/" + subtaskindex;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, SubtaskExecutionAttemptDetailsInfo.class);
    }

    @Override
    public CompletableFuture<SubtaskExecutionAttemptDetailsInfo> jobVertexSubtaskAttemptDetail(String jobId, String vertexId, Integer subtaskindex, Integer attempt) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/subtasks/" + subtaskindex + "/attempts/" + attempt;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, SubtaskExecutionAttemptDetailsInfo.class);
    }

    @Override
    public CompletableFuture<SubtaskExecutionAttemptAccumulatorsInfo> jobVertexSubtaskAttemptAccumulators(String jobId, String vertexId, Integer subtaskindex, Integer attempt) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/subtasks/" + subtaskindex + "/attempts/" + attempt + "/accumulators";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, SubtaskExecutionAttemptAccumulatorsInfo.class);
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobVertexSubtaskMetrics(String jobId, String vertexId, Integer subtaskindex, String get) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/subtasks/" + subtaskindex + "/metrics";
        if (StringUtils.isNotBlank(get)) {
            url = url + "?get=" + get;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, MetricCollectionResponseBody.class);
    }

    @Override
    public CompletableFuture<SubtasksTimesInfo> jobVertexSubtaskTimes(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/subtasktimes";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, SubtasksTimesInfo.class);
    }

    @Override
    public CompletableFuture<JobVertexTaskManagersInfo> jobVertexTaskManagers(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/taskmanagers";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, JobVertexTaskManagersInfo.class);
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> jobVertexWatermarks(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/watermarks";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, MetricCollectionResponseBody.class);
    }

    @Override
    public CompletableFuture<TriggerResponse> savepointDisposal(SavepointDisposalRequest requestBody) throws IOException {
        String url = webInterfaceURL + "/savepoint-disposal";
        RequestBody body = RequestBody.create(FlinkShadedJacksonUtil.toJsonString(requestBody), APPLICATION_JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        return remoteCall(request, TriggerResponse.class);
    }

    @Override
    public CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> savepointDisposalResult(String triggerId) throws IOException {
        String url = webInterfaceURL + "/savepoint-disposal/" + triggerId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, AsynchronousOperationResult.class, AsynchronousOperationInfo.class).thenApply(result -> {
            AsynchronousOperationResult<AsynchronousOperationInfo> type = result;
            return type;
        });
    }

    @Override
    public CompletableFuture<TaskManagersInfo> taskManagers() throws IOException {
        String url = webInterfaceURL + "/taskmanagers";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, TaskManagersInfo.class);
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> taskManagersMetrics(String get, String agg, String taskmanagers) throws IOException {
        String url = webInterfaceURL + "/taskmanagers/metrics";
        List<String> queryParams = new LinkedList<>();
        if (StringUtils.isNotBlank(get)) {
            queryParams.add("get=" + get);
        }
        if (StringUtils.isNotBlank(agg)) {
            queryParams.add("agg=" + agg);
        }
        if (StringUtils.isNotBlank(taskmanagers)) {
            queryParams.add("taskmanagers=" + taskmanagers);
        }
        if (queryParams.isEmpty() == false) {
            String params = queryParams.stream().collect(Collectors.joining("&"));
            url = url + "?" + params;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, MetricCollectionResponseBody.class);
    }

    @Override
    public CompletableFuture<TaskManagerDetailsInfo> taskManagerDetail(String taskManagerId) throws IOException {
        String url = webInterfaceURL + "/taskmanagers/" + taskManagerId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, TaskManagerDetailsInfo.class);
    }

    @Override
    public CompletableFuture<LogListInfo> taskManagerLogs(String taskManagerId) throws IOException {
        String url = webInterfaceURL + "/taskmanagers/" + taskManagerId + "/logs";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, LogListInfo.class);
    }

    @Override
    public CompletableFuture<MetricCollectionResponseBody> taskManagerMetrics(String taskManagerId, String get) throws IOException {
        String url = webInterfaceURL + "/taskmanagers/" + taskManagerId + "/metrics";
        if (StringUtils.isNotBlank(get)) {
            url = url + "?get=" + get;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, MetricCollectionResponseBody.class);
    }

    @Override
    public CompletableFuture<ThreadDumpInfo> taskManagerThreadDump(String taskManagerId) throws IOException {
        String url = webInterfaceURL + "/taskmanagers/" + taskManagerId + "/thread-dump";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        return remoteCall(request, ThreadDumpInfo.class);
    }

    private void checkStatus(Response response) throws IOException {
        if (response.isSuccessful() == false) {
            String error = String.format("code: %d, message: %s, body: %s", response.code(), response.message(), response.body().string());
            throw new RuntimeException(error);
        }
    }
}
