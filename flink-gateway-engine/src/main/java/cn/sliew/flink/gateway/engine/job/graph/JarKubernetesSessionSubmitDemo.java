package cn.sliew.flink.gateway.engine.job.graph;

import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.configuration.KubernetesDeploymentTarget;
import org.apache.flink.runtime.jobgraph.JobGraph;

public class JarKubernetesSessionSubmitDemo {

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        ClusterClientFactory<String> factory = newClientFactory(config);
        String clusterId = factory.getClusterId(config);
        KubernetesClusterDescriptor clusterDescriptor = (KubernetesClusterDescriptor) factory.createClusterDescriptor(config);

        // 1. 通过 cluster.id 获取 集群
        ClusterClient<String> clusterClient = clusterDescriptor.retrieve(clusterId).getClusterClient();
        // 2. 提交任务
        JobGraph jobGraph = Util.createJobGraph(config);
        JobID jobID = clusterClient.submitJob(jobGraph).get();
        System.out.println(jobID);
    }

    private static ClusterClientFactory<String> newClientFactory(Configuration config) {
        config.setString(KubernetesConfigOptions.CLUSTER_ID, "flink-cluster-7b367a19632fb03f4ff84a580e3d032");
        config.setString(DeploymentOptions.TARGET, KubernetesDeploymentTarget.SESSION.getName());

        DefaultClusterClientServiceLoader serviceLoader = new DefaultClusterClientServiceLoader();
        return serviceLoader.getClusterClientFactory(config);
    }
}
