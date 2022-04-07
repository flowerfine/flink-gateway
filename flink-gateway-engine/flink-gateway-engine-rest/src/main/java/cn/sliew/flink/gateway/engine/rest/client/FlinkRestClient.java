package cn.sliew.flink.gateway.engine.rest.client;

import cn.sliew.flink.gateway.engine.base.client.*;
import cn.sliew.milky.common.exception.Rethrower;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.rest.RestClient;
import org.apache.flink.util.ConfigurationException;
import org.apache.flink.util.concurrent.ExecutorThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FlinkRestClient implements FlinkClient {

    private final String address;
    private final int port;
    private final RestClient client;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4, new ExecutorThreadFactory("Flink-RestClusterClient-IO"));

    public FlinkRestClient(String address, int port, Configuration configuration) {
        this.address = address;
        this.port = port;

        RestClient restClient = null;
        try {
            restClient = new RestClient(configuration, executorService);
        } catch (ConfigurationException e) {
            Rethrower.throwAs(e);
        }
        this.client = restClient;
    }

    @Override
    public SubmitClient submit() {
        return null;
    }

    @Override
    public ClusterClient cluster() {
        return new ClusterRestClient(address, port, client);
    }

    @Override
    public DataSetClient dataSet() {
        return new DataSetRestClient(address, port, client);
    }

    @Override
    public JarClient jar() {
        return new JarRestClient(address, port, client);
    }

    @Override
    public JobClient job() {
        return new JobRestClient(address, port, client);
    }

    @Override
    public JobManagerClient jobManager() {
        return new JobManagerRestClient(address, port, client);
    }

    @Override
    public TaskManagerClient taskManager() {
        return new TaskManagerRestClient(address, port, client);
    }

    @Override
    public SavepointClient savepoint() {
        return new SavepointRestClient(address, port, client);
    }

    @Override
    public DashboardClient dashboard() {
        return new DashboardRestClient(address, port, client);
    }
}
