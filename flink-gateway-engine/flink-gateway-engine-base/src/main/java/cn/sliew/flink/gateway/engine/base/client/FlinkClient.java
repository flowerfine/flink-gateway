package cn.sliew.flink.gateway.engine.base.client;

public interface FlinkClient {

    SubmitClient submit();

    ClusterClient cluster();

    DataSetClient dataSet();

    JarClient jar();

    JobClient job();

    JobManagerClient jobManager();

    TaskManagerClient taskManager();

    SavepointClient savepoint();

    DashboardClient dashboard();
}
