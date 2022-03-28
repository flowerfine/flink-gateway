package cn.sliew.flink.gateway.engine.endpoint.impl;

import cn.sliew.flink.gateway.engine.endpoint.CliEndpoint;
import cn.sliew.flink.gateway.engine.enums.DeploymentTarget;
import org.apache.flink.client.cli.ApplicationDeployer;
import org.apache.flink.client.cli.ProgramOptions;
import org.apache.flink.client.deployment.ClusterClientServiceLoader;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.deployment.application.cli.ApplicationClusterDeployer;
import org.apache.flink.configuration.Configuration;

public class CliEndpointImpl implements CliEndpoint {

    @Override
    public void submit(DeploymentTarget deploymentTarget, Configuration configuration, ProgramOptions options) throws Exception {

    }

    @Override
    public void submitApplication(DeploymentTarget deploymentTarget, Configuration configuration) throws Exception {
        ClusterClientServiceLoader clusterClientServiceLoader = new DefaultClusterClientServiceLoader();
        ApplicationDeployer deployer = new ApplicationClusterDeployer(clusterClientServiceLoader);
        

    }
}
