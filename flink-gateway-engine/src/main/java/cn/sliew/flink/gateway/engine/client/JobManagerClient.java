package cn.sliew.flink.gateway.engine.client;

import org.apache.flink.runtime.rest.messages.ClusterConfigurationInfo;
import org.apache.flink.runtime.rest.messages.LogListInfo;
import org.apache.flink.runtime.rest.messages.job.metrics.MetricCollectionResponseBody;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface JobManagerClient {

    /**
     * Returns the cluster configuration.
     */
    CompletableFuture<ClusterConfigurationInfo> jobmanagerConfig() throws IOException;

    /**
     * Returns the list of log files on the JobManager.
     */
    CompletableFuture<LogListInfo> jobmanagerLogs() throws IOException;

    /**
     * Provides access to job manager metrics.
     *
     * @param get(optional) Comma-separated list of string values to select specific metrics.
     */
    CompletableFuture<MetricCollectionResponseBody> jobmanagerMetrics(Optional<String> get) throws IOException;

}
