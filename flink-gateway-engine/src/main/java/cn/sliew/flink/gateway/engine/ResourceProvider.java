package cn.sliew.flink.gateway.engine;

import lombok.Getter;

@Getter
public enum ResourceProvider {

    STANDALONE("Standalone"),
    NATIVE_KUBERNETES("Native Kubernetes"),
    YARN("YARN");

    private String name;

    ResourceProvider(String name) {
        this.name = name;
    }
}
