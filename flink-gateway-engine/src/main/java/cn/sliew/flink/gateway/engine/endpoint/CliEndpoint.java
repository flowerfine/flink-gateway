package cn.sliew.flink.gateway.engine.endpoint;

import cn.sliew.flink.gateway.engine.enums.DeploymentTarget;
import org.apache.flink.configuration.Configuration;

public interface CliEndpoint {

    void submitApplication(DeploymentTarget deploymentTarget, Configuration configuration, PackageJarJob job) throws Exception;

    void submit(DeploymentTarget deploymentTarget, Configuration configuration, PackageJarJob job) throws Exception;

}


