package cn.sliew.flink.gateway.service.flink;

import cn.sliew.flink.gateway.model.request.FlinkSettingAddRequest;
import cn.sliew.flink.gateway.model.response.FlinkSettingGetResponse;

public interface FlinkSettingService {

    FlinkSettingGetResponse get(Long id);

    boolean add(FlinkSettingAddRequest request);

}
