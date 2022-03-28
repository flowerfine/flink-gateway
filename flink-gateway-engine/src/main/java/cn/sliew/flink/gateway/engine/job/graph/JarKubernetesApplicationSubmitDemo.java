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
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.configuration.KubernetesDeploymentTarget;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collections;

/**
 * 首先通过命令 docker build -f Dockerfile -t flink-example:1 . 创建镜像
 */
@Slf4j
public class JarKubernetesApplicationSubmitDemo {

    public static void main(String[] args) throws Exception {
        String imageName = buildImage();

        Configuration config = Util.loadConfiguration();
        ClusterClientFactory<String> factory = newClientFactory(config);
        KubernetesClusterDescriptor clusterDescriptor = createClusterDescriptor(factory, config);

        ClusterSpecification clusterSpecification = Util.createClusterSpecification();
        config.setLong(JobManagerOptions.TOTAL_PROCESS_MEMORY.key(), MemorySize.ofMebiBytes(1024).getBytes());
        config.setLong(TaskManagerOptions.TOTAL_PROCESS_MEMORY.key(), MemorySize.ofMebiBytes(1024).getBytes());

        ConfigUtils.encodeCollectionToConfig(config, PipelineOptions.JARS, Collections.singletonList(new File(Util.LOCAL_JAR_FILE_PATH)), Object::toString);
        config.setString(KubernetesConfigOptions.CONTAINER_IMAGE, imageName);
        ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(new String[]{}, Util.ENTRY_POINT_CLASS_NAME);
        ClusterClient<String> clusterClient = createClusterClient(clusterDescriptor, clusterSpecification, applicationConfiguration);
    }

    private static String buildImage() {
        return "flink-example:1";
    }

    private static ClusterClientFactory<String> newClientFactory(Configuration config) {
        config.setString(DeploymentOptions.TARGET, KubernetesDeploymentTarget.APPLICATION.getName());

        DefaultClusterClientServiceLoader serviceLoader = new DefaultClusterClientServiceLoader();
        return serviceLoader.getClusterClientFactory(config);
    }

    private static KubernetesClusterDescriptor createClusterDescriptor(ClusterClientFactory<String> factory, Configuration config) throws MalformedURLException {
        KubernetesClusterDescriptor clusterDescriptor = (KubernetesClusterDescriptor) factory.createClusterDescriptor(config);
        return clusterDescriptor;
    }

    private static ClusterClient<String> createClusterClient(KubernetesClusterDescriptor clusterDescriptor,
                                                             ClusterSpecification clusterSpecification,
                                                             ApplicationConfiguration applicationConfiguration) throws ClusterDeploymentException {

        ClusterClientProvider<String> provider = clusterDescriptor.deployApplicationCluster(clusterSpecification, applicationConfiguration);
        ClusterClient<String> clusterClient = provider.getClusterClient();
        log.info("deploy application with clusterId: {}", clusterClient.getClusterId());
        return clusterClient;
    }
}
