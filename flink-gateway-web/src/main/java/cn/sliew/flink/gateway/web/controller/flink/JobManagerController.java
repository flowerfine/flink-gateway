package cn.sliew.flink.gateway.web.controller.flink;

import cn.sliew.flinkful.rest.base.RestClient;
import org.apache.flink.runtime.rest.messages.ClusterConfigurationInfo;
import org.apache.flink.runtime.rest.messages.LogListInfo;
import org.apache.flink.runtime.rest.messages.job.metrics.MetricCollectionResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/job-manager")
public class JobManagerController {

    @Autowired
    private RestClient client;

    @GetMapping("config")
    public CompletableFuture<ClusterConfigurationInfo> config() throws IOException {
        return client.jobManager().jobmanagerConfig();
    }

    @GetMapping("logs")
    public CompletableFuture<LogListInfo> logs() throws IOException {
        return client.jobManager().jobmanagerLogs();
    }

    @GetMapping("metrics")
    public CompletableFuture<MetricCollectionResponseBody> metrics(@RequestParam(value = "get", required = false) Optional<String> get) throws IOException {
        return client.jobManager().jobmanagerMetrics(get);
    }
}
