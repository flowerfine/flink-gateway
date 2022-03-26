package cn.sliew.flink.gateway.engine;

public interface CliEndpoint {

    void standalone() throws Exception;



    void submitApplication() throws Exception;

    void submitSessionCluster() throws Exception;

    void submitPerjobCluster() throws Exception;
}


