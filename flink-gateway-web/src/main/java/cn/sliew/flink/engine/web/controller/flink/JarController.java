package cn.sliew.flink.engine.web.controller.flink;

import cn.sliew.flink.gateway.engine.RestEndpoint;
import cn.sliew.flink.gateway.engine.impl.RestEndpointImpl;
import org.apache.flink.runtime.webmonitor.handlers.JarListInfo;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/flink/jar")
public class JarController {

    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");

    @GetMapping("jars")
    public JarListInfo jars() throws IOException {
        return endpoint.jars();
    }

    @PostMapping("upload")
    public boolean upload(@RequestParam("filePath") String filePath) throws IOException {
        return endpoint.uploadJar(filePath);
    }
}
