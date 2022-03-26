package cn.sliew.flink.gateway.engine.enums;

import lombok.Getter;
import org.apache.flink.client.deployment.executors.RemoteExecutor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.kubernetes.configuration.KubernetesDeploymentTarget;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;

@Getter
public enum DeploymentTarget {

    STANDALONE_APPLICATION(ResourceProvider.STANDALONE, DeploymentMode.APPLICATION, RemoteExecutor.NAME),
    STANDALONE_SESSION(ResourceProvider.STANDALONE, DeploymentMode.SESSION, RemoteExecutor.NAME),

    NATIVE_KUBERNETES_APPLICATION(ResourceProvider.NATIVE_KUBERNETES, DeploymentMode.APPLICATION, KubernetesDeploymentTarget.APPLICATION.getName()),
    NATIVE_KUBERNETES_SESSION(ResourceProvider.NATIVE_KUBERNETES, DeploymentMode.SESSION, KubernetesDeploymentTarget.SESSION.getName()),

    YARN_APPLICATION(ResourceProvider.YARN, DeploymentMode.APPLICATION, YarnDeploymentTarget.APPLICATION.getName()),
    YARN_PER_JOB(ResourceProvider.YARN, DeploymentMode.PER_JOB, YarnDeploymentTarget.PER_JOB.getName()),
    YARN_SESSION(ResourceProvider.YARN, DeploymentMode.SESSION, YarnDeploymentTarget.SESSION.getName()),
    ;

    private ResourceProvider resourceProvider;
    private DeploymentMode deploymentMode;
    private String name;

    DeploymentTarget(ResourceProvider resourceProvider, DeploymentMode deploymentMode, String name) {
        this.resourceProvider = resourceProvider;
        this.deploymentMode = deploymentMode;
        this.name = name;
    }

    public Configuration toConfiguration(Configuration configuration) {
        configuration.set(DeploymentOptions.TARGET, getName());
        return configuration;
    }
}
