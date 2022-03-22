package cn.sliew.flink.gateway.engine.impl;

import cn.sliew.flink.gateway.engine.RestEndpoint;
import cn.sliew.milky.common.util.JacksonUtil;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.flink.calcite.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.runtime.messages.webmonitor.JobIdsWithStatusOverview;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.messages.ClusterConfigurationInfoEntry;
import org.apache.flink.runtime.rest.messages.DashboardConfiguration;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.runtime.rest.messages.LogListInfo;
import org.apache.flink.runtime.rest.messages.dataset.ClusterDataSetListResponseBody;
import org.apache.flink.runtime.rest.messages.job.JobSubmitRequestBody;
import org.apache.flink.runtime.rest.messages.job.JobSubmitResponseBody;
import org.apache.flink.runtime.webmonitor.handlers.*;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class RestEndpointImpl implements RestEndpoint {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(3L))
            .readTimeout(Duration.ofSeconds(3L))
            .writeTimeout(Duration.ofSeconds(3L))
            .callTimeout(Duration.ofSeconds(3L))
            .build();

    private final String webInterfaceURL;

    public RestEndpointImpl(String webInterfaceURL) {
        this.webInterfaceURL = webInterfaceURL;
    }

    @Override
    public boolean cluster() throws IOException {
        okhttp3.Request build = new okhttp3.Request.Builder()
                .get()
                .url(webInterfaceURL + "/cluster")
                .build();
        try (Response response = client.newCall(build).execute()) {
            return response.isSuccessful();
        }
    }

    @Override
    public DashboardConfiguration config() throws IOException {
        Content content = Request.get(webInterfaceURL + "/config")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnContent();
        // 不顶用再说
        return JacksonUtil.parseJsonString(content.asString(StandardCharsets.UTF_8), DashboardConfiguration.class);
    }

    @Override
    public ClusterDataSetListResponseBody datasets() throws IOException {
        Content content = Request.get(webInterfaceURL + "/datasets")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnContent();
        return JacksonUtil.parseJsonString(content.asString(StandardCharsets.UTF_8), ClusterDataSetListResponseBody.class);
    }

    @Override
    public TriggerResponse deleteDataSet(String datasetId) throws IOException {
        Content content = Request.delete(webInterfaceURL + "/datasets/" + datasetId)
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnContent();

        return JacksonUtil.parseJsonString(content.asString(StandardCharsets.UTF_8), TriggerResponse.class);
    }

    @Override
    public AsynchronousOperationResult deleteDataSetStatus(String triggerId) throws IOException {
        Content content = Request.get(webInterfaceURL + "/datasets/delete/" + triggerId)
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnContent();
        return JacksonUtil.parseJsonString(content.asString(StandardCharsets.UTF_8), AsynchronousOperationResult.class);
    }

    @Override
    public JarListInfo jars() throws IOException {
        Content content = Request.get(webInterfaceURL + "/jars")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnContent();
        return JacksonUtil.parseJsonString(content.asString(StandardCharsets.UTF_8), JarListInfo.class);
    }

    @Override
    public JarUploadResponseBody uploadJar(String filePath) throws IOException {
        File jarFile = new File(filePath);
        Content content = Request.post(webInterfaceURL + "/jars/upload")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .body(
                        MultipartEntityBuilder.create()
                                .addBinaryBody("jarfile", jarFile, ContentType.create("application/java-archive"), jarFile.getName())
                                .build()
                ).execute().returnContent();
        return JacksonUtil.parseJsonString(content.asString(StandardCharsets.UTF_8), JarUploadResponseBody.class);
    }

    @Override
    public boolean deleteJar(String jarId) throws IOException {
        HttpResponse response = Request.delete(webInterfaceURL + "/jars/" + jarId)
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnResponse();
        return response.getCode() == HttpStatus.SC_SUCCESS;
    }

    @Override
    public JobPlanInfo jarPlan(String jarId, JarPlanRequestBody requestBody) throws IOException {
        Content content = Request.post(webInterfaceURL + "/jars/" + jarId + "/plan")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .body(new StringEntity(JacksonUtil.toJsonString(requestBody)))
                .execute().returnContent();
        return JacksonUtil.parseJsonString(content.asString(StandardCharsets.UTF_8), JobPlanInfo.class);
    }

    @Override
    public JarRunResponseBody jarRun(String jarId, JarRunRequestBody requestBody) throws IOException {
        Content content = Request.post(webInterfaceURL + "/jars/" + jarId + "/run")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .body(new StringEntity(JacksonUtil.toJsonString(requestBody)))
                .execute().returnContent();
        return JacksonUtil.parseJsonString(content.asString(StandardCharsets.UTF_8), JarRunResponseBody.class);
    }

    @Override
    public List<ClusterConfigurationInfoEntry> jobmanagerConfig() throws IOException {
        Content content = Request.get(webInterfaceURL + "/jobmanager/config")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnContent();
        return JacksonUtil.parseJsonArray(content.asString(StandardCharsets.UTF_8), ClusterConfigurationInfoEntry.class);
    }

    @Override
    public LogListInfo jobmanagerLogs() throws IOException {
        Content content = Request.get(webInterfaceURL + "/jobmanager/logs")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnContent();
        return JacksonUtil.parseJsonString(content.asString(StandardCharsets.UTF_8), LogListInfo.class);
    }

    @Override
    public String jobmanagerMetrics(String metric) throws IOException {
        Content content = Request.get(webInterfaceURL + "/jobmanager/metrics?get=" + metric)
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnContent();
        return content.asString(StandardCharsets.UTF_8);
    }

    @Override
    public JobIdsWithStatusOverview jobs() throws IOException {
        Content content = Request.get(webInterfaceURL + "/jobs")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnContent();
        return JacksonUtil.parseJsonString(content.asString(StandardCharsets.UTF_8), JobIdsWithStatusOverview.class);
    }

    @Override
    public JobSubmitResponseBody jobSubmit(JobSubmitRequestBody requestBody) throws IOException {
        Content content = Request.post(webInterfaceURL + "/jobs")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .body(new StringEntity(JacksonUtil.toJsonString(requestBody)))
                .execute().returnContent();
        return JacksonUtil.parseJsonString(content.asString(StandardCharsets.UTF_8), JobSubmitResponseBody.class);
    }

    @Override
    public String jobsMetric(String get, String agg, String jobs) throws IOException {
        return null;
    }

    @Override
    public MultipleJobsDetails jobsOverview() throws IOException {
        Content content = Request.get(webInterfaceURL + "/jobs/overview")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnContent();
        return JacksonUtil.parseJsonString(content.asString(StandardCharsets.UTF_8), MultipleJobsDetails.class);
    }


}
