package cn.sliew.flink.gateway.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseEntity {

    /**
     * id, 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 删除标识。0: 未删除, 1: 已删除
     * mybatis 会自动将 tinyint 类型转换为 boolean 类型
     */
    @TableField("deleted")
    private boolean deleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
