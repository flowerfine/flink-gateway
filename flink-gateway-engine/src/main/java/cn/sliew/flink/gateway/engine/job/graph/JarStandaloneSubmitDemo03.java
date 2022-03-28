package cn.sliew.flink.gateway.engine.job.graph;

import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.deployment.StandaloneClusterDescriptor;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.deployment.executors.RemoteExecutor;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;

import java.io.File;

public class JarStandaloneSubmitDemo03 {

    public static void main(String[] args) throws Exception {
        String jarFilePath = "/Users/wangqi/Documents/software/flink/flink-1.13.6/examples/streaming/SocketWindowWordCount.jar";
        PackagedProgram program = PackagedProgram.newBuilder()
                .setJarFile(new File(jarFilePath))
                .setArguments("--port", "9000")
                .setEntryPointClassName("org.apache.flink.streaming.examples.socket.SocketWindowWordCount")
                .build();

        Configuration config = new Configuration();
        ClusterClientFactory<StandaloneClusterId> factory = newClientFactory(config);
        StandaloneClusterId clusterId = factory.getClusterId(config);
        StandaloneClusterDescriptor clusterDescriptor = (StandaloneClusterDescriptor) factory.createClusterDescriptor(config);
        ClusterClient<StandaloneClusterId> client = clusterDescriptor.retrieve(clusterId).getClusterClient();

        JobGraph jobGraph = PackagedProgramUtils.createJobGraph(program, config, 1, false);
        JobID jobId = client.submitJob(jobGraph).get();
        System.out.println(jobId);
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
