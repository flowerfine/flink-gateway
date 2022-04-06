package cn.sliew.flink.gateway.model.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class FlinkSettingGetResponse implements Response {

    @Serial
    private static final long serialVersionUID = -2229834978406030711L;

    private FlinkSettingDTO data;
}
