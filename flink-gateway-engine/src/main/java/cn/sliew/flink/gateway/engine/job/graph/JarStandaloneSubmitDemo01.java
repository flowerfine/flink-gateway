package cn.sliew.flink.gateway.engine.job.graph;

import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;

import java.io.File;

/**
 * --add-opens java.base/java.lang=ALL-UNNAMED
 * 1. start nc server: nc -l 9000
 * 2. bootstrap demo. for jdk 17, must add vm options: --add-opens java.base/java.lang=ALL-UNNAMED
 * 3. type some words to nc, and response on taskmanager console
 * not working for jdk 17
 */
public class JarStandaloneSubmitDemo01 {

    public static void main(String[] args) throws Exception {
        String jarFilePath = "/Users/wangqi/Documents/software/flink/flink-1.13.6/examples/streaming/SocketWindowWordCount.jar";
        PackagedProgram program = PackagedProgram.newBuilder()
                .setJarFile(new File(jarFilePath))
                .setArguments("--port", "9000")
                .setEntryPointClassName("org.apache.flink.streaming.examples.socket.SocketWindowWordCount")
                .build();

        Configuration config = new Configuration();
        config.setString(JobManagerOptions.ADDRESS, "127.0.0.1");
        config.setInteger(RestOptions.PORT, 8081);
        config.setInteger(RestOptions.RETRY_MAX_ATTEMPTS, 3);
        RestClusterClient<StandaloneClusterId> client = new RestClusterClient<>(config, StandaloneClusterId.getInstance());

        JobGraph jobGraph = PackagedProgramUtils.createJobGraph(program, config, 1, false);

        JobID jobId = client.submitJob(jobGraph).get();
        System.out.println(jobId);
    }
}
