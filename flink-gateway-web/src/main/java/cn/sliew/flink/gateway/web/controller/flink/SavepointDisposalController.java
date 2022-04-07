package cn.sliew.flink.gateway.web.controller.flink;

import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationInfo;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointDisposalRequest;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * fixme not tested!
 */
@RestController
@RequestMapping("/flink/savepoint-disposal")
public class SavepointDisposalController {

    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");

    @PostMapping("/")
    public CompletableFuture<TriggerResponse> dispose(@RequestBody SavepointDisposalRequest request) throws IOException {
        return endpoint.savepointDisposal(request);
    }

    @GetMapping("{triggerId}")
    public CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> disposal(@PathVariable("triggerId") String triggerId) throws IOException {
        return endpoint.savepointDisposalResult(triggerId);
    }
}
