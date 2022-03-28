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
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesDeploymentTarget;
import org.apache.flink.runtime.jobgraph.JobGraph;

/**
 * Native Kubernetes 部署需要利用 ${user.home}/.kube/config 信息获取 Kubernetes 信息
 */
@Slf4j
public class KubernetesSessionClusterCreateDemo {

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        ClusterClientFactory<String> factory = newClientFactory(config);
        KubernetesClusterDescriptor clusterDescriptor = (KubernetesClusterDescriptor) factory.createClusterDescriptor(config);

        config.setLong(JobManagerOptions.TOTAL_PROCESS_MEMORY.key(), MemorySize.ofMebiBytes(2048).getBytes());
        config.setLong(TaskManagerOptions.TOTAL_PROCESS_MEMORY.key(), MemorySize.ofMebiBytes(2048).getBytes());

        // 1. 创建 session 集群
        ClusterSpecification clusterSpecification = Util.createClusterSpecification();
        ClusterClient<String> clusterClient = createClusterClient(clusterDescriptor, clusterSpecification);
        // 2. 提交任务
        JobGraph jobGraph = Util.createJobGraph(config);
        JobID jobID = clusterClient.submitJob(jobGraph).get();
        System.out.println(jobID);
    }

    private static ClusterClientFactory<String> newClientFactory(Configuration config) {
        config.setString(DeploymentOptions.TARGET, KubernetesDeploymentTarget.SESSION.getName());

        DefaultClusterClientServiceLoader serviceLoader = new DefaultClusterClientServiceLoader();
        return serviceLoader.getClusterClientFactory(config);
    }

    private static ClusterClient<String> createClusterClient(KubernetesClusterDescriptor clusterDescriptor,
                                                             ClusterSpecification clusterSpecification) throws ClusterDeploymentException {

        ClusterClientProvider<String> provider = clusterDescriptor.deploySessionCluster(clusterSpecification);
        ClusterClient<String> clusterClient = provider.getClusterClient();

        log.info("deploy session with clusterId: {}", clusterClient.getClusterId());
        return clusterClient;
    }
}
