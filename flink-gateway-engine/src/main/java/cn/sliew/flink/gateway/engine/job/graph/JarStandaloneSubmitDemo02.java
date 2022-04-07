package cn.sliew.flink.gateway.engine.job.graph;

import cn.sliew.flink.gateway.engine.job.graph.protocol.JarRunRequest;
import cn.sliew.flink.gateway.engine.job.graph.protocol.JarRunResponse;
import cn.sliew.flink.gateway.engine.job.graph.protocol.JarUploadResponse;
import cn.sliew.milky.common.util.JacksonUtil;
import okhttp3.*;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.deployment.StandaloneClusterDescriptor;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.deployment.executors.RemoteExecutor;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.rest.util.RestConstants;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static cn.sliew.flink.gateway.engine.endpoint.impl.RestEndpointImpl.APPLICATION_JSON;

public class JarStandaloneSubmitDemo02 {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(3L))
            .readTimeout(Duration.ofSeconds(3L))
            .writeTimeout(Duration.ofSeconds(3L))
            .callTimeout(Duration.ofSeconds(3L))
            .addInterceptor(new LogInterceptor())
            .build();

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        ClusterClientFactory<StandaloneClusterId> factory = newClientFactory(config);
        StandaloneClusterId clusterId = factory.getClusterId(config);
        StandaloneClusterDescriptor clusterDescriptor = (StandaloneClusterDescriptor) factory.createClusterDescriptor(config);

        ClusterClient<StandaloneClusterId> client = clusterDescriptor.retrieve(clusterId).getClusterClient();
        String webInterfaceURL = client.getWebInterfaceURL();

        String jarFilePath = "/Users/wangqi/Documents/software/flink/flink-1.13.6/examples/streaming/SocketWindowWordCount.jar";
        JarUploadResponse jarUploadResponse = uploadJar(webInterfaceURL, new File(jarFilePath));
        String jarId = jarUploadResponse.getFilename().substring(jarUploadResponse.getFilename().lastIndexOf("/") + 1);
        JobID jobID = run(webInterfaceURL, jarId, "org.apache.flink.streaming.examples.socket.SocketWindowWordCount");
        System.out.println(jobID);
    }

    private static JarUploadResponse uploadJar(String webInterfaceURL, File jarFile) throws IOException {
        String url = webInterfaceURL + "/jars/upload";
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("jarfile", jarFile.getName(), RequestBody.create(jarFile, MediaType.get(RestConstants.CONTENT_TYPE_JAR)))
                .build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return JacksonUtil.parseJsonString(response.body().string(), JarUploadResponse.class);
        }
    }

    private static JobID run(String webInterfaceURL, String jarId, String entryClass) throws IOException {
        JarRunRequest jarRunRequest = new JarRunRequest();
        jarRunRequest.setEntryClass(entryClass);
        jarRunRequest.setProgramArgs("--port 9000");

        String url = webInterfaceURL + "/jars/" + jarId + "/run";
        RequestBody body = RequestBody.create(JacksonUtil.toJsonString(jarRunRequest), APPLICATION_JSON);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            JarRunResponse jarRunResponse = JacksonUtil.parseJsonString(response.body().string(), JarRunResponse.class);
            return JobID.fromHexString(jarRunResponse.getJobID());
        }
    }


    private static ClusterClientFactory<StandaloneClusterId> newClientFactory(Configuration config) {
        config.setString(JobManagerOptions.ADDRESS, "localhost");
        config.setInteger(JobManagerOptions.PORT, 8081);

        // stanalone 模式下的 webInterfaceUrl
        config.setString(RestOptions.ADDRESS, "localhost");
        config.setInteger(RestOptions.PORT, 8081);
        config.setString(DeploymentOptions.TARGET, RemoteExecutor.NAME);

        DefaultClusterClientServiceLoader serviceLoader = new DefaultClusterClientServiceLoader();
        return serviceLoader.getClusterClientFactory(config);
    }
}
