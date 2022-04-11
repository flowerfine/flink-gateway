package cn.sliew.flink.gateway.dao.mapper.gateway.flink.deploy;

import cn.sliew.flink.gateway.dao.entity.gateway.flink.deploy.FlinkDeployConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface FlinkDeployConfigMapper extends BaseMapper<FlinkDeployConfig> {

    int insertSelective(FlinkDeployConfig record);

    int updateByPrimaryKeySelective(FlinkDeployConfig record);
}
