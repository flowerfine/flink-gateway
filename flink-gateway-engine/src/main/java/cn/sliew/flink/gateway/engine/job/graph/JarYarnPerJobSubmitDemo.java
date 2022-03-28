package cn.sliew.flink.gateway.engine.job.graph;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.*;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.util.HadoopUtils;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.api.records.ApplicationId;

import java.net.MalformedURLException;

@Slf4j
public class JarYarnPerJobSubmitDemo {

    public static void main(String[] args) throws Exception {
        Configuration config = Util.loadConfiguration();
        ClusterClientFactory<ApplicationId> factory = newClientFactory(config);
        YarnClusterDescriptor clusterDescriptor = createClusterDescriptor(factory, config);
        config.setLong(JobManagerOptions.TOTAL_PROCESS_MEMORY.key(), MemorySize.ofMebiBytes(2048).getBytes());
        config.setLong(TaskManagerOptions.TOTAL_PROCESS_MEMORY.key(), MemorySize.ofMebiBytes(2048).getBytes());
        config.setInteger(TaskManagerOptions.NUM_TASK_SLOTS, 2);
        ClusterSpecification clusterSpecification = Util.createClusterSpecification();
        JobGraph jobGraph = Util.createJobGraph(config);
        ClusterClient<ApplicationId> clusterClient = createClusterClient(clusterDescriptor, clusterSpecification, jobGraph);
    }

    /**
     * 需提供 hadoop 的配置文件，以便 flink 获取 hadoop 集群地址。
     * 当使用 flink on yarn 配置时，不需要配置 {@link JobManagerOptions#ADDRESS} 参数，
     * 配置 hadoop 配置文件，可以通过 {@link CoreOptions#FLINK_YARN_CONF_DIR} 和 {@link CoreOptions#FLINK_HADOOP_CONF_DIR}，
     * {@link CoreOptions#FLINK_YARN_CONF_DIR} 拥有更高的优先级，当二者都未配置时，flink 会尝试从 HADOOP_HOME 环境变量
     * 获取 hadoop 配置。
     * {@link CoreOptions#FLINK_YARN_CONF_DIR} 和 {@link CoreOptions#FLINK_HADOOP_CONF_DIR} 只支持环境变量形式设置，
     * 设置两个参数的目的仅仅是为了文档的自动生成.
     *
     * @see HadoopUtils#getHadoopConfiguration(Configuration)
     */
    private static ClusterClientFactory<ApplicationId> newClientFactory(Configuration config) {
//        config.setString(JobManagerOptions.ADDRESS, "localhost");
//        config.setString(CoreOptions.FLINK_YARN_CONF_DIR, HADOOP_CONF_DIR);
//        config.setString(CoreOptions.FLINK_HADOOP_CONF_DIR, HADOOP_CONF_DIR);
        config.setString(DeploymentOptions.TARGET, YarnDeploymentTarget.PER_JOB.getName());

        DefaultClusterClientServiceLoader serviceLoader = new DefaultClusterClientServiceLoader();
        return serviceLoader.getClusterClientFactory(config);
    }

    /**
     * 也可以通过 {@link YarnConfigOptions#FLINK_DIST_JAR} 配置 flink-dist-xxx.jar
     * {@link YarnConfigOptions#SHIP_FILES} 配置 ship jars.
     */
    private static YarnClusterDescriptor createClusterDescriptor(ClusterClientFactory<ApplicationId> factory, Configuration config) throws MalformedURLException {
        YarnClusterDescriptor clusterDescriptor = (YarnClusterDescriptor) factory.createClusterDescriptor(config);
        Util.addJarFiles(clusterDescriptor, config);
        return clusterDescriptor;
    }

    private static ClusterClient<ApplicationId> createClusterClient(YarnClusterDescriptor clusterDescriptor,
                                                                    ClusterSpecification clusterSpecification,
                                                                    JobGraph jobGraph) throws ClusterDeploymentException {

        ClusterClientProvider<ApplicationId> provider = clusterDescriptor.deployJobCluster(clusterSpecification, jobGraph, true);
        ClusterClient<ApplicationId> clusterClient = provider.getClusterClient();
        log.info("deploy per_job with appId: {}", clusterClient.getClusterId());
        return clusterClient;
    }
}
