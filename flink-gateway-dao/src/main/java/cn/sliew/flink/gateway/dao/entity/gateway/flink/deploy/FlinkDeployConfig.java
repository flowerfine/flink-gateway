package cn.sliew.flink.gateway.dao.entity.gateway.flink.deploy;

import cn.sliew.flink.gateway.common.enums.DeploymentMode;
import cn.sliew.flink.gateway.common.enums.ResourceProvider;
import cn.sliew.flink.gateway.dao.entity.BaseEntityExt;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlinkDeployConfig extends BaseEntityExt {

    /**
     * Flink Resource 方式
     */
    @TableField("resource_provider")
    private ResourceProvider resourceProvider;

    /**
     * Flink 部署模式
     */
    @TableField("deploy_mode")
    private DeploymentMode deploymentMode;

    /**
     * flink 版本
     */
    @TableField("flink_version")
    private String flink_version;

    /**
     * flink 配置目录。支持 file、hdfs、s3 协议
     */
    @TableField("flink_home")
    private String flink_home;

    /**
     * yarn 配置地址或 kubernetes context 地址。支持 file、hdfs、s3 协议
     */
    @TableField("deploy_context")
    private String deploy_context;

    /**
     * 描述
     */
    @TableField("desc")
    private String desc;
}
