package cn.sliew.flink.gateway.web.controller.flink;

import cn.sliew.flink.gateway.engine.endpoint.RestEndpoint;
import cn.sliew.flink.gateway.engine.endpoint.impl.RestEndpointImpl;
import org.apache.flink.runtime.rest.handler.legacy.messages.ClusterOverviewWithVersion;
import org.apache.flink.runtime.rest.messages.EmptyResponseBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/cluster")
public class ClusterController {

    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");

    @GetMapping("overview")
    public CompletableFuture<ClusterOverviewWithVersion> overview() throws IOException {
        return endpoint.overview();
    }

    @DeleteMapping("shutdown")
    public CompletableFuture<EmptyResponseBody> shutdown() throws IOException {
        return endpoint.shutdownCluster();
    }
}
