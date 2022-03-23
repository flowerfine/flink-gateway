package cn.sliew.flink.engine.web.controller.flink;

import cn.sliew.flink.gateway.engine.RestEndpoint;
import cn.sliew.flink.gateway.engine.impl.RestEndpointImpl;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointDisposalRequest;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * fixme not tested!
 */
@RestController
@RequestMapping("/flink/savepoint-disposal")
public class SavepointDisposalController {

    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");

    @PostMapping("/")
    public TriggerResponse dispose(@RequestBody SavepointDisposalRequest request) throws IOException {
        return endpoint.savepointDisposal(request);
    }

    @GetMapping("{triggerId}")
    public AsynchronousOperationResult disposal(@PathVariable("triggerId") String triggerId) throws IOException {
        return endpoint.savepointDisposalResult(triggerId);
    }
}
