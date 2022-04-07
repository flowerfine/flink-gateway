package cn.sliew.flink.gateway.engine.client;

public interface FlinkClient {

    SubmitClient submit();

    ClusterClient cluster();

    DataSetClient dataSet();

    JarClient jar();

    JobClient job();

    JobManagerClient jobManager();

    void taskManager();

    SavepointClient savepoint();

    DashboardClient dashboard();
}
