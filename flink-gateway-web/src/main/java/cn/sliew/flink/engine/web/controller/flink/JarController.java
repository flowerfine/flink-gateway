package cn.sliew.flink.engine.web.controller.flink;

import cn.sliew.flink.gateway.engine.endpoint.RestEndpoint;
import cn.sliew.flink.gateway.engine.endpoint.impl.RestEndpointImpl;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.runtime.webmonitor.handlers.*;
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
    public JarUploadResponseBody upload(@RequestParam("filePath") String filePath) throws IOException {
        return endpoint.uploadJar(filePath);
    }

    @DeleteMapping("{jarId}")
    public boolean delete(@PathVariable("jarId") String jarId) throws IOException {
        return endpoint.deleteJar(jarId);
    }

    @GetMapping("{jarId}/plan")
    public JobPlanInfo jarPlan(@PathVariable("jarId") String jarId, JarPlanRequestBody requestBody) throws IOException {
        return endpoint.jarPlan(jarId, requestBody);
    }

    @PostMapping("{jarId}/run")
    public JarRunResponseBody jarRun(@PathVariable("jarId") String jarId, @RequestBody JarRunRequestBody requestBody) throws IOException {
        return endpoint.jarRun(jarId, requestBody);
    }
}
