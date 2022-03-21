package cn.sliew.flink.gateway.engine;

import org.apache.flink.runtime.rest.messages.DashboardConfiguration;
import org.apache.flink.runtime.rest.messages.dataset.ClusterDataSetListResponseBody;

import java.io.IOException;

public interface RestEndpoint {

    /**
     * Shuts down the cluster
     */
    boolean cluster() throws IOException;

    /**
     * Returns the configuration of the WebUI.
     */
    DashboardConfiguration config() throws IOException;

    /**
     * Returns all cluster data sets.
     */
    ClusterDataSetListResponseBody datasets() throws IOException;


}
