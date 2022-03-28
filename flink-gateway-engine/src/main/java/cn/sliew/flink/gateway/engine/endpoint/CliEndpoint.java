package cn.sliew.flink.gateway.engine.endpoint;

import cn.sliew.flink.gateway.engine.enums.DeploymentTarget;
import org.apache.flink.client.cli.ProgramOptions;
import org.apache.flink.configuration.Configuration;

public interface CliEndpoint {

    void submit(DeploymentTarget deploymentTarget, Configuration configuration, ProgramOptions options) throws Exception;

    void submitApplication(DeploymentTarget deploymentTarget, Configuration configuration) throws Exception;
}


