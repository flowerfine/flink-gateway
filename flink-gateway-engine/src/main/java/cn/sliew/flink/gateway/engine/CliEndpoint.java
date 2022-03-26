package cn.sliew.flink.gateway.engine;

public interface CliEndpoint {

    void submit() throws Exception;

    void submitApplication() throws Exception;
}


