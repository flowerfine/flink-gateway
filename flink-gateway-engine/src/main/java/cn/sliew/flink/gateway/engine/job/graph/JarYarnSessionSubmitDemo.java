package cn.sliew.flink.gateway.engine.job.graph;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.api.records.ApplicationId;

@Slf4j
public class JarYarnSessionSubmitDemo {

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        ClusterClientFactory<ApplicationId> factory = newClientFactory(config);
        ApplicationId clusterId = factory.getClusterId(config);
        YarnClusterDescriptor clusterDescriptor = (YarnClusterDescriptor) factory.createClusterDescriptor(config);
        // 1. 通过 application.id 获取 集群
        ClusterClient<ApplicationId> clusterClient = clusterDescriptor.retrieve(clusterId).getClusterClient();
        // 2. 提交任务
        JobGraph jobGraph = Util.createJobGraph(config);
        JobID jobID = clusterClient.submitJob(jobGraph).get();
        System.out.println(jobID);
    }

    private static ClusterClientFactory<ApplicationId> newClientFactory(Configuration config) {
        config.setString(YarnConfigOptions.APPLICATION_ID, "application_1646981816129_0003");
        config.setString(DeploymentOptions.TARGET, YarnDeploymentTarget.SESSION.getName());

        DefaultClusterClientServiceLoader serviceLoader = new DefaultClusterClientServiceLoader();
        return serviceLoader.getClusterClientFactory(config);
    }
}
