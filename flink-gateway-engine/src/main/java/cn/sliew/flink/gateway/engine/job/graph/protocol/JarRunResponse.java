package cn.sliew.flink.gateway.engine.job.graph.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JarRunResponse {

    @JsonProperty("jobid")
    private String jobID;
}
