package cn.sliew.flink.gateway.service.flink.impl;

import cn.sliew.flink.gateway.dao.entity.gateway.flink.FlinkSetting;
import cn.sliew.flink.gateway.dao.mapper.gateway.flink.FlinkSettingMapper;
import cn.sliew.flink.gateway.model.request.FlinkSettingAddRequest;
import cn.sliew.flink.gateway.model.response.FlinkSettingGetResponse;
import cn.sliew.flink.gateway.service.ServiceException;
import cn.sliew.flink.gateway.service.flink.FlinkSettingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class FlinkSettingServiceImpl implements FlinkSettingService {

    @Autowired
    private FlinkSettingMapper flinkSettingMapper;

    @Override
    public FlinkSettingGetResponse get(Long id) {
        FlinkSetting flinkSetting = flinkSettingMapper.selectById(id);
        if (flinkSetting == null) {
            throw new ServiceException("flink setting not exists for " + id);
        }
        return null;
    }

    @Override
    public boolean add(FlinkSettingAddRequest request) {
        int insert = flinkSettingMapper.insertSelective(null);
        return insert == 1;
    }
}
