package cn.sliew.flink.gateway.engine.job.graph;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.*;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.api.records.ApplicationId;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
public class JarYarnApplicationSubmitDemo {

    public static void main(String[] args) throws Exception {
        Configuration config = Util.loadConfiguration();
        ClusterClientFactory<ApplicationId> factory = newClientFactory(config);
        YarnClusterDescriptor clusterDescriptor = createClusterDescriptor(factory, config);
        ClusterSpecification clusterSpecification = Util.createClusterSpecification();

        config.setLong(JobManagerOptions.TOTAL_PROCESS_MEMORY.key(), MemorySize.ofMebiBytes(4096).getBytes());
        config.setLong(TaskManagerOptions.TOTAL_PROCESS_MEMORY.key(), MemorySize.ofMebiBytes(4096).getBytes());
        config.setInteger(TaskManagerOptions.NUM_TASK_SLOTS, 2);
        config.set(YarnConfigOptions.PROVIDED_LIB_DIRS, Arrays.asList(new String[]{"hdfs://hadoop:9000/flink/1.13.6"}));
        config.set(YarnConfigOptions.FLINK_DIST_JAR, "hdfs://hadoop:9000/flink/1.13.6/flink-dist_2.11-1.13.6.jar");
//        Util.addJarFiles(clusterDescriptor, config);

        ConfigUtils.encodeCollectionToConfig(config, PipelineOptions.JARS, Collections.singletonList(new File(Util.JAR_FILE_PATH)), Object::toString);
        ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(new String[]{}, Util.ENTRY_POINT_CLASS_NAME);
        ClusterClient<ApplicationId> clusterClient = createClusterClient(clusterDescriptor, clusterSpecification, applicationConfiguration);
    }

    private static ClusterClientFactory<ApplicationId> newClientFactory(Configuration config) {
        config.setString(DeploymentOptions.TARGET, YarnDeploymentTarget.APPLICATION.getName());

        DefaultClusterClientServiceLoader serviceLoader = new DefaultClusterClientServiceLoader();
        return serviceLoader.getClusterClientFactory(config);
    }

    private static YarnClusterDescriptor createClusterDescriptor(ClusterClientFactory<ApplicationId> factory, Configuration config) throws MalformedURLException {
        YarnClusterDescriptor clusterDescriptor = (YarnClusterDescriptor) factory.createClusterDescriptor(config);
        return clusterDescriptor;
    }

    private static ClusterClient<ApplicationId> createClusterClient(YarnClusterDescriptor clusterDescriptor,
                                                                    ClusterSpecification clusterSpecification,
                                                                    ApplicationConfiguration applicationConfiguration) throws ClusterDeploymentException {

        ClusterClientProvider<ApplicationId> provider = clusterDescriptor.deployApplicationCluster(clusterSpecification, applicationConfiguration);
        ClusterClient<ApplicationId> clusterClient = provider.getClusterClient();
        log.info("deploy application with appId: {}", clusterClient.getClusterId());
        return clusterClient;
    }
}
