package cn.sliew.flink.gateway.engine.job.graph;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.*;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.api.records.ApplicationId;

@Slf4j
public class YarnSessionClusterCreateDemo {

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        ClusterClientFactory<ApplicationId> factory = newClientFactory(config);
        YarnClusterDescriptor clusterDescriptor = (YarnClusterDescriptor) factory.createClusterDescriptor(config);
        Util.addJarFiles(clusterDescriptor, config);

        config.setLong(JobManagerOptions.TOTAL_PROCESS_MEMORY.key(), MemorySize.ofMebiBytes(2048).getBytes());
        config.setLong(TaskManagerOptions.TOTAL_PROCESS_MEMORY.key(), MemorySize.ofMebiBytes(2048).getBytes());

        // 1. 创建 session 集群
        ClusterSpecification clusterSpecification = Util.createClusterSpecification();
        ClusterClient<ApplicationId> clusterClient = createClusterClient(clusterDescriptor, clusterSpecification);
        // 2. 提交任务
        JobGraph jobGraph = Util.createJobGraph(config);
        JobID jobID = clusterClient.submitJob(jobGraph).get();
        System.out.println(jobID);
    }

    private static ClusterClientFactory<ApplicationId> newClientFactory(Configuration config) {
        config.setString(DeploymentOptions.TARGET, YarnDeploymentTarget.SESSION.getName());

        DefaultClusterClientServiceLoader serviceLoader = new DefaultClusterClientServiceLoader();
        return serviceLoader.getClusterClientFactory(config);
    }

    private static ClusterClient<ApplicationId> createClusterClient(YarnClusterDescriptor clusterDescriptor,
                                                                    ClusterSpecification clusterSpecification) throws ClusterDeploymentException {

        ClusterClientProvider<ApplicationId> provider = clusterDescriptor.deploySessionCluster(clusterSpecification);
        ClusterClient<ApplicationId> clusterClient = provider.getClusterClient();

        log.info("deploy session with appId: {}", clusterClient.getClusterId());
        return clusterClient;
    }
}
