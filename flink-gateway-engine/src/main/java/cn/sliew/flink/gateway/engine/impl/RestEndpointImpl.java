package cn.sliew.flink.gateway.engine.impl;

import cn.sliew.flink.gateway.engine.RestEndpoint;
import cn.sliew.milky.common.check.Ensures;
import cn.sliew.milky.common.util.JacksonUtil;
import cn.sliew.milky.common.util.StringUtils;
import okhttp3.RequestBody;
import okhttp3.*;
import okhttp3.internal.Util;
import org.apache.flink.runtime.messages.webmonitor.JobIdsWithStatusOverview;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
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
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointDisposalRequest;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointTriggerRequestBody;
import org.apache.flink.runtime.rest.messages.job.savepoints.stop.StopWithSavepointRequestBody;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagerDetailsInfo;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagersInfo;
import org.apache.flink.runtime.rest.messages.taskmanager.ThreadDumpInfo;
import org.apache.flink.runtime.webmonitor.handlers.*;
import org.apache.flink.runtime.webmonitor.threadinfo.JobVertexFlameGraph;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class RestEndpointImpl implements RestEndpoint {

    public static final MediaType APPLICATION_JSON = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(3L))
            .readTimeout(Duration.ofSeconds(3L))
            .writeTimeout(Duration.ofSeconds(3L))
            .callTimeout(Duration.ofSeconds(3L))
            .addInterceptor(new LogInterceptor())
            .build();

    private final String webInterfaceURL;

    public RestEndpointImpl(String webInterfaceURL) {
        this.webInterfaceURL = webInterfaceURL;
    }

    @Override
    public ClusterOverviewWithVersion overview() throws IOException {
        String url = webInterfaceURL + "/overview";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkStatus(response);
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), ClusterOverviewWithVersion.class);
        }
    }

    @Override
    public boolean shutdownCluster() throws IOException {
        String url = webInterfaceURL + "/cluster";
        Request request = new Request.Builder()
                .delete()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkStatus(response);
            return response.isSuccessful();
        }
    }

    @Override
    public DashboardConfiguration config() throws IOException {
        String url = webInterfaceURL + "/config";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkStatus(response);
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), DashboardConfiguration.class);
        }
    }

    @Override
    public ClusterDataSetListResponseBody datasets() throws IOException {
        String url = webInterfaceURL + "/datasets";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkStatus(response);
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), ClusterDataSetListResponseBody.class);
        }
    }

    @Override
    public TriggerResponse deleteDataSet(String datasetId) throws IOException {
        String url = webInterfaceURL + "/datasets/" + datasetId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkStatus(response);
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), TriggerResponse.class);
        }
    }

    @Override
    public AsynchronousOperationResult deleteDataSetStatus(String triggerId) throws IOException {
        String url = webInterfaceURL + "/datasets/delete/" + triggerId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkStatus(response);
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), AsynchronousOperationResult.class);
        }
    }

    @Override
    public JarListInfo jars() throws IOException {
        String url = webInterfaceURL + "/jars";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkStatus(response);
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JarListInfo.class);
        }
    }

    @Override
    public JarUploadResponseBody uploadJar(String filePath) throws IOException {
        String url = webInterfaceURL + "/jars/upload";
        File jarFile = new File(filePath);
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("jarfile", jarFile.getName(), RequestBody.create(jarFile, MediaType.get("application/java-archive")))
                .build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkStatus(response);
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JarUploadResponseBody.class);
        }
    }

    @Override
    public boolean deleteJar(String jarId) throws IOException {
        String url = webInterfaceURL + "/jars/" + jarId;
        Request request = new Request.Builder()
                .delete()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkStatus(response);
            return response.isSuccessful();
        }
    }

    @Override
    public JobPlanInfo jarPlan(String jarId, JarPlanRequestBody requestBody) throws IOException {
        String url = webInterfaceURL + "/jars/" + jarId + "/plan";
        RequestBody body = RequestBody.create(FlinkShadedJacksonUtil.toJsonString(requestBody), APPLICATION_JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkStatus(response);
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JobPlanInfo.class);
        }
    }

    @Override
    public JarRunResponseBody jarRun(String jarId, JarRunRequestBody requestBody) throws IOException {
        String url = webInterfaceURL + "/jars/" + jarId + "/run";
        RequestBody body = RequestBody.create(FlinkShadedJacksonUtil.toJsonString(requestBody), APPLICATION_JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkStatus(response);
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JarRunResponseBody.class);
        }
    }

    @Override
    public List<ClusterConfigurationInfoEntry> jobmanagerConfig() throws IOException {
        String url = webInterfaceURL + "/jobmanager/config";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonArray(response.body().string(), ClusterConfigurationInfoEntry.class);
        }
    }

    @Override
    public LogListInfo jobmanagerLogs() throws IOException {
        String url = webInterfaceURL + "/jobmanager/logs";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), LogListInfo.class);
        }
    }

    @Override
    public String jobmanagerMetrics(String get) throws IOException {
        String url = webInterfaceURL + "/jobmanager/metrics";
        if (StringUtils.isNotBlank(get)) {
            url = url + "?get=" + get;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    public JobIdsWithStatusOverview jobs() throws IOException {
        String url = webInterfaceURL + "/jobs";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JobIdsWithStatusOverview.class);
        }
    }

    @Override
    public JobSubmitResponseBody jobSubmit(JobSubmitRequestBody requestBody) throws IOException {
        String url = webInterfaceURL + "/jobs";
        RequestBody body = RequestBody.create(FlinkShadedJacksonUtil.toJsonString(requestBody), APPLICATION_JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JobSubmitResponseBody.class);
        }
    }

    @Override
    public String jobsMetric(String get, String agg, String jobs) throws IOException {
        return null;
    }

    @Override
    public MultipleJobsDetails jobsOverview() throws IOException {
        String url = webInterfaceURL + "/jobs/overview";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), MultipleJobsDetails.class);
        }
    }

    @Override
    public JobDetailsInfo jobDetail(String jobId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JobDetailsInfo.class);
        }
    }

    @Override
    public boolean jobTerminate(String jobId, String mode) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId;
        if (StringUtils.isNotBlank(mode)) {
            url = url + "?mode=" + mode;
        }
        Request request = new Request.Builder()
                .patch(Util.EMPTY_REQUEST)
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    @Override
    public JobAccumulatorsInfo jobAccumulators(String jobId, Boolean includeSerializedValue) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/accumulators";
        if (includeSerializedValue != null) {
            url = url + "?includeSerializedValue=" + includeSerializedValue;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() == false) {
                throw new RuntimeException(response.code() + "=" + response.message());
            }
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JobAccumulatorsInfo.class);
        }
    }

    @Override
    public CheckpointingStatistics jobCheckpoints(String jobId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/checkpoints";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), CheckpointingStatistics.class);
        }
    }

    @Override
    public CheckpointConfigInfo jobCheckpointConfig(String jobId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/checkpoints/config";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), CheckpointConfigInfo.class);
        }
    }

    @Override
    public CheckpointStatistics jobCheckpointDetail(String jobId, String checkpointId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/checkpoints/details/" + checkpointId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), CheckpointStatistics.class);
        }
    }

    @Override
    public TaskCheckpointStatisticsWithSubtaskDetails jobCheckpointSubtaskDetail(String jobId, String checkpointId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/checkpoints/details/" + checkpointId + "/subtasks/" + vertexId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), TaskCheckpointStatisticsWithSubtaskDetails.class);
        }
    }

    @Override
    public String jobConfig(String jobId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/config";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    public JobExceptionsInfoWithHistory jobException(String jobId, String maxExceptions) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/exceptions";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JobExceptionsInfoWithHistory.class);
        }
    }

    @Override
    public JobExecutionResultResponseBody jobExecutionResult(String jobId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/execution-result";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JobExecutionResultResponseBody.class);
        }
    }

    @Override
    public String jobMetrics(String jobId, String get) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/metrics";
        if (StringUtils.isNotBlank(get)) {
            url = url + "?get=" + get;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    public JobPlanInfo jobPlan(String jobId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/plan";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JobPlanInfo.class);
        }
    }

    @Override
    public TriggerResponse jobRescale(String jobId, Integer parallelism) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/rescaling";
        Ensures.checkNotNull(parallelism, () -> "parallelism can't be null");
        Ensures.checkArgument(parallelism > 0, () -> "parallelism must be positive integer");
        Request request = new Request.Builder()
                .patch(Util.EMPTY_REQUEST)
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), TriggerResponse.class);
        }
    }

    @Override
    public AsynchronousOperationResult jobRescaleResult(String jobId, String triggerId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/rescaling/" + triggerId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), AsynchronousOperationResult.class);
        }
    }

    @Override
    public TriggerResponse jobSavepoint(String jobId, SavepointTriggerRequestBody requestBody) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/savepoints";
        RequestBody body = RequestBody.create(FlinkShadedJacksonUtil.toJsonString(requestBody), APPLICATION_JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), TriggerResponse.class);
        }
    }

    @Override
    public AsynchronousOperationResult jobSavepointResult(String jobId, String triggerId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/savepoints/" + triggerId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), AsynchronousOperationResult.class);
        }
    }

    @Override
    public TriggerResponse jobStop(String jobId, StopWithSavepointRequestBody requestBody) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/stop";
        RequestBody body = RequestBody.create(FlinkShadedJacksonUtil.toJsonString(requestBody), APPLICATION_JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), TriggerResponse.class);
        }
    }

    @Override
    public JobVertexDetailsInfo jobVertexDetail(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JobVertexDetailsInfo.class);
        }
    }

    @Override
    public JobVertexAccumulatorsInfo jobVertexAccumulators(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/accumulators";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JobVertexAccumulatorsInfo.class);
        }
    }

    @Override
    public JobVertexBackPressureInfo jobVertexBackPressure(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/backpressure";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JobVertexBackPressureInfo.class);
        }
    }

    @Override
    public JobVertexFlameGraph jobVertexFlameGraph(String jobId, String vertexId, String type) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/flamegraph";
        if (StringUtils.isNotBlank(type)) {
            url = url + "?type=" + type;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JobVertexFlameGraph.class);
        }
    }

    @Override
    public String jobVertexMetrics(String jobId, String vertexId, String get) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/metrics";
        if (StringUtils.isNotBlank(get)) {
            url = url + "?get=" + get;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    public SubtasksAllAccumulatorsInfo jobVertexSubtaskAccumulators(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/subtasks/accumulators";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), SubtasksAllAccumulatorsInfo.class);
        }
    }

    @Override
    public String jobVertexSubtaskMetrics(String jobId, String vertexId, String get, String agg, String subtasks) throws IOException {
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
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    public SubtaskExecutionAttemptDetailsInfo jobVertexSubtaskDetail(String jobId, String vertexId, Integer subtaskindex) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/subtasks/" + subtaskindex;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), SubtaskExecutionAttemptDetailsInfo.class);
        }
    }

    @Override
    public SubtaskExecutionAttemptDetailsInfo jobVertexSubtaskAttemptDetail(String jobId, String vertexId, Integer subtaskindex, Integer attempt) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/subtasks/" + subtaskindex + "/attempts/" + attempt;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), SubtaskExecutionAttemptDetailsInfo.class);
        }
    }

    @Override
    public SubtaskExecutionAttemptAccumulatorsInfo jobVertexSubtaskAttemptAccumulators(String jobId, String vertexId, Integer subtaskindex, Integer attempt) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/subtasks/" + subtaskindex + "/attempts/" + attempt + "/accumulators";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), SubtaskExecutionAttemptAccumulatorsInfo.class);
        }
    }

    @Override
    public String jobVertexSubtaskMetrics(String jobId, String vertexId, Integer subtaskindex, String get) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/subtasks/" + subtaskindex + "/metrics";
        if (StringUtils.isNotBlank(get)) {
            url = url + "?get=" + get;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    public SubtasksTimesInfo jobVertexSubtaskTimes(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/subtasktimes";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), SubtasksTimesInfo.class);
        }
    }

    @Override
    public JobVertexTaskManagersInfo jobVertexTaskManagers(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/taskmanagers";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), JobVertexTaskManagersInfo.class);
        }
    }

    @Override
    public String jobVertexWatermarks(String jobId, String vertexId) throws IOException {
        String url = webInterfaceURL + "/jobs/" + jobId + "/vertices/" + vertexId + "/watermarks";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    public TriggerResponse savepointDisposal(SavepointDisposalRequest requestBody) throws IOException {
        String url = webInterfaceURL + "/savepoint-disposal";
        RequestBody body = RequestBody.create(FlinkShadedJacksonUtil.toJsonString(requestBody), APPLICATION_JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), TriggerResponse.class);
        }
    }

    @Override
    public AsynchronousOperationResult savepointDisposalResult(String triggerId) throws IOException {
        String url = webInterfaceURL + "/savepoint-disposal/" + triggerId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), AsynchronousOperationResult.class);
        }
    }

    @Override
    public TaskManagersInfo taskManagersDetail() throws IOException {
        String url = webInterfaceURL + "/taskmanagers";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), TaskManagersInfo.class);
        }
    }

    @Override
    public String taskManagersMetrics(String get, String agg, String taskmanagers) throws IOException {
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
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    public TaskManagerDetailsInfo taskManagerDetail(String taskManagerId) throws IOException {
        String url = webInterfaceURL + "/taskmanagers/" + taskManagerId;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), TaskManagerDetailsInfo.class);
        }
    }

    @Override
    public LogListInfo taskManagerLogs(String taskManagerId) throws IOException {
        String url = webInterfaceURL + "/taskmanagers/" + taskManagerId + "/logs";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), LogListInfo.class);
        }
    }

    @Override
    public String taskManagerMetrics(String taskManagerId, String get) throws IOException {
        String url = webInterfaceURL + "/taskmanagers/" + taskManagerId + "/metrics";
        if (StringUtils.isNotBlank(get)) {
            url = url + "?get=" + get;
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    public ThreadDumpInfo taskManagerThreadDump(String taskManagerId) throws IOException {
        String url = webInterfaceURL + "/taskmanagers/" + taskManagerId + "/thread-dump";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return FlinkShadedJacksonUtil.parseJsonString(response.body().string(), ThreadDumpInfo.class);
        }
    }

    private void checkStatus(Response response) throws IOException {
        if (response.isSuccessful() == false) {
            String error = String.format("code: %d, message: %s, body: %s", response.code(), response.message(), response.body().string());
            throw new RuntimeException(error);
        }
    }
}
