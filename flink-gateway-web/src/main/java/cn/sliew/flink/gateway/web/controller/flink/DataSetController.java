package cn.sliew.flink.gateway.web.controller.flink;

import cn.sliew.flink.gateway.engine.endpoint.RestEndpoint;
import cn.sliew.flink.gateway.engine.endpoint.impl.RestEndpointImpl2;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationInfo;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.messages.dataset.ClusterDataSetListResponseBody;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/datasets")
public class DataSetController {

//    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");
    private RestEndpoint endpoint = new RestEndpointImpl2(GlobalConfiguration.loadConfiguration());

    @GetMapping("/")
    public CompletableFuture<ClusterDataSetListResponseBody> datasets() throws IOException {
        return endpoint.datasets();
    }

    @DeleteMapping("/{dataSetId}")
    public CompletableFuture<TriggerResponse> delete(@PathVariable("dataSetId") String dataSetId) throws IOException {
        return endpoint.deleteDataSet(dataSetId);
    }

    @DeleteMapping("/delete/{triggerId}")
    public CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> deleteStatus(@PathVariable("triggerId") String triggerId) throws IOException {
        return endpoint.deleteDataSetStatus(triggerId);
    }
}
