package cn.sliew.flink.gateway.web.controller.flink;

import cn.sliew.flink.gateway.engine.base.client.FlinkClient;
import cn.sliew.flink.gateway.engine.http.client.FlinkHttpClient;
import org.apache.flink.runtime.rest.messages.DashboardConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/web-ui")
public class WebUIController {

    private FlinkClient client = new FlinkHttpClient("http://localhost:8081");

    @GetMapping("config")
    public CompletableFuture<DashboardConfiguration> config() throws IOException {
        return client.dashboard().config();
    }
}
