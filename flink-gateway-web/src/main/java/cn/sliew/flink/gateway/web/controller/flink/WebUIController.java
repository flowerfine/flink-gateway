package cn.sliew.flink.gateway.web.controller.flink;

import cn.sliew.flinkful.rest.base.RestClient;
import org.apache.flink.runtime.rest.messages.DashboardConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/web-ui")
public class WebUIController {

    @Autowired
    private RestClient client;

    @GetMapping("config")
    public CompletableFuture<DashboardConfiguration> config() throws IOException {
        return client.dashboard().config();
    }
}
