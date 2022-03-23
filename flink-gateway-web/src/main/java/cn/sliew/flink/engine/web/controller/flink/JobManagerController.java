package cn.sliew.flink.engine.web.controller.flink;

import cn.sliew.flink.gateway.engine.RestEndpoint;
import cn.sliew.flink.gateway.engine.impl.RestEndpointImpl;
import org.apache.flink.runtime.rest.messages.ClusterConfigurationInfoEntry;
import org.apache.flink.runtime.rest.messages.LogListInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flink/job-manager")
public class JobManagerController {

    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");

    @GetMapping("config")
    public List<ClusterConfigurationInfoEntry> config() throws IOException {
        return endpoint.jobmanagerConfig();
    }

    @GetMapping("logs")
    public LogListInfo logs() throws IOException {
        return endpoint.jobmanagerLogs();
    }

    /**
     * todo 换成 2 接口
     */
    @GetMapping("metrics")
    public List<Map> metrics(@RequestParam(value = "get", required = false) String get) throws IOException {
        return endpoint.jobmanagerMetrics(get);
    }
}
