package cn.sliew.flink.gateway.web.controller.flink;

import org.apache.flink.runtime.rest.messages.EmptyResponseBody;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/jars")
public class JarController {

    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");

    @GetMapping("/")
    public CompletableFuture<JarListInfo> jars() throws IOException {
        return endpoint.jars();
    }

    @PostMapping("upload")
    public CompletableFuture<JarUploadResponseBody> upload(@RequestParam("filePath") String filePath) throws IOException {
        return endpoint.uploadJar(filePath);
    }

    @DeleteMapping("{jarId}")
    public CompletableFuture<EmptyResponseBody> delete(@PathVariable("jarId") String jarId) throws IOException {
        return endpoint.deleteJar(jarId);
    }

    @GetMapping("{jarId}/plan")
    public CompletableFuture<JobPlanInfo> jarPlan(@PathVariable("jarId") String jarId, JarPlanRequestBody requestBody) throws IOException {
        return endpoint.jarPlan(jarId, requestBody);
    }

    @PostMapping("{jarId}/run")
    public CompletableFuture<JarRunResponseBody> jarRun(@PathVariable("jarId") String jarId, @RequestBody JarRunRequestBody requestBody) throws IOException {
        return endpoint.jarRun(jarId, requestBody);
    }
}
