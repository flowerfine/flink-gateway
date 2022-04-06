package cn.sliew.flink.gateway.model.result;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class PageResultWrapper<T> extends ResultWrapper<T> {

    @Serial
    private static final long serialVersionUID = -6572748018216329573L;

    /**
     * 总记录数。分页信息
     */
    private long total;

    /**
     * 当前页码。分页信息
     * 从 1 开始。
     */
    private long current;

    /**
     * 当前页数。分页信息
     */
    private long size;
}
