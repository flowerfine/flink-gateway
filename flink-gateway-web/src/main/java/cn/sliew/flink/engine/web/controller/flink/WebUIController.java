package cn.sliew.flink.engine.web.controller.flink;

import cn.sliew.flink.gateway.engine.endpoint.RestEndpoint;
import cn.sliew.flink.gateway.engine.endpoint.impl.RestEndpointImpl;
import cn.sliew.flink.gateway.engine.endpoint.impl.RestEndpointImpl2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.rest.messages.DashboardConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/web-ui")
public class WebUIController {

//    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");
    private RestEndpoint endpoint = new RestEndpointImpl2(GlobalConfiguration.loadConfiguration());

    @GetMapping("config")
    public CompletableFuture<DashboardConfiguration> config() throws IOException {
        return endpoint.config();
    }
}
