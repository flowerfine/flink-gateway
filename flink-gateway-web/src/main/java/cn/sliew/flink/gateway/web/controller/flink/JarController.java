package cn.sliew.flink.gateway.web.controller.flink;

import cn.sliew.flink.gateway.engine.base.client.FlinkClient;
import cn.sliew.flink.gateway.engine.http.client.FlinkHttpClient;
import org.apache.flink.runtime.rest.messages.EmptyResponseBody;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.runtime.webmonitor.handlers.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/jars")
public class JarController {

    private FlinkClient client = new FlinkHttpClient("http://localhost:8081");

    @GetMapping("/")
    public CompletableFuture<JarListInfo> jars() throws IOException {
        return client.jar().jars();
    }

    @PostMapping("upload")
    public CompletableFuture<JarUploadResponseBody> upload(@RequestParam("filePath") String filePath) throws IOException {
        return client.jar().uploadJar(filePath);
    }

    @DeleteMapping("{jarId}")
    public CompletableFuture<EmptyResponseBody> delete(@PathVariable("jarId") String jarId) throws IOException {
        return client.jar().deleteJar(jarId);
    }

    @GetMapping("{jarId}/plan")
    public CompletableFuture<JobPlanInfo> jarPlan(@PathVariable("jarId") String jarId, JarPlanRequestBody requestBody) throws IOException {
        return client.jar().jarPlan(jarId, requestBody);
    }

    @PostMapping("{jarId}/run")
    public CompletableFuture<JarRunResponseBody> jarRun(@PathVariable("jarId") String jarId, @RequestBody JarRunRequestBody requestBody) throws IOException {
        return client.jar().jarRun(jarId, requestBody);
    }
}
