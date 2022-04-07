package cn.sliew.flink.gateway.engine.base.endpoint;

import cn.sliew.flink.gateway.common.enums.DeploymentTarget;
import org.apache.flink.configuration.Configuration;

public interface CliEndpoint {

    void submitApplication(DeploymentTarget deploymentTarget, Configuration configuration, PackageJarJob job) throws Exception;

    void submit(DeploymentTarget deploymentTarget, Configuration configuration, PackageJarJob job) throws Exception;

}


