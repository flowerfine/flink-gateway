package cn.sliew.flink.gateway.engine.client;

import org.apache.flink.client.cli.ApplicationDeployer;
import org.apache.flink.client.deployment.ClusterClientServiceLoader;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.deployment.application.cli.ApplicationClusterDeployer;

public class FlinkApplicationService {

    private final ClusterClientServiceLoader clusterClientServiceLoader = new DefaultClusterClientServiceLoader();

    public void submit() throws Exception {
        final ClusterClientServiceLoader clusterClientServiceLoader =
                new DefaultClusterClientServiceLoader();
        final ApplicationDeployer deployer =
                new ApplicationClusterDeployer(clusterClientServiceLoader);
    }


}
