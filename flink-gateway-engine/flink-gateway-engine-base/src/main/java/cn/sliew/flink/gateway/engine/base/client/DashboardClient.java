package cn.sliew.flink.gateway.engine.base.client;

import org.apache.flink.runtime.rest.messages.DashboardConfiguration;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface DashboardClient {

    /**
     * Returns the configuration of the WebUI.
     */
    CompletableFuture<DashboardConfiguration> config() throws IOException;
}
