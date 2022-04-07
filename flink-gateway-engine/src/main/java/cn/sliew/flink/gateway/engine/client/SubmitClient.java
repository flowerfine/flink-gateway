package cn.sliew.flink.gateway.engine.client;

import cn.sliew.flink.gateway.common.enums.DeploymentTarget;
import cn.sliew.flink.gateway.engine.endpoint.PackageJarJob;
import org.apache.flink.configuration.Configuration;

public interface SubmitClient {

    void submitApplication(DeploymentTarget deploymentTarget, Configuration configuration, PackageJarJob job) throws Exception;

    void submit(DeploymentTarget deploymentTarget, Configuration configuration, PackageJarJob job) throws Exception;

}
