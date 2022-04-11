package cn.sliew.flink.gateway.dao.mapper.gateway.flink;

import cn.sliew.flink.gateway.dao.entity.gateway.flink.FlinkArtifact;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface FlinkArtifactMapper extends BaseMapper<FlinkArtifact> {

    int insertSelective(FlinkArtifact record);

    int updateByPrimaryKeySelective(FlinkArtifact record);
}
