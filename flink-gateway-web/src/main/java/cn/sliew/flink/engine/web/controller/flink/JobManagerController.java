package cn.sliew.flink.engine.web.controller.flink;

import cn.sliew.flink.gateway.engine.endpoint.RestEndpoint;
import cn.sliew.flink.gateway.engine.endpoint.impl.RestEndpointImpl;
import org.apache.flink.runtime.rest.messages.ClusterConfigurationInfo;
import org.apache.flink.runtime.rest.messages.LogListInfo;
import org.apache.flink.runtime.rest.messages.job.metrics.MetricCollectionResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/job-manager")
public class JobManagerController {

    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");

    @GetMapping("config")
    public CompletableFuture<ClusterConfigurationInfo> config() throws IOException {
        return endpoint.jobmanagerConfig();
    }

    @GetMapping("logs")
    public CompletableFuture<LogListInfo> logs() throws IOException {
        return endpoint.jobmanagerLogs();
    }

    /**
     * todo 换成 2 接口
     */
    @GetMapping("metrics")
    public CompletableFuture<MetricCollectionResponseBody> metrics(@RequestParam(value = "get", required = false) String get) throws IOException {
        return endpoint.jobmanagerMetrics(get);
    }
}
