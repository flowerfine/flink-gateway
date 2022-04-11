package cn.sliew.flink.gateway.dao.mapper.gateway.flink.deploy;

import cn.sliew.flink.gateway.dao.entity.gateway.flink.deploy.FlinkDeployLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface FlinkDeployLogMapper extends BaseMapper<FlinkDeployLog> {

    int insertSelective(FlinkDeployLog record);

    int updateByPrimaryKeySelective(FlinkDeployLog record);
}
