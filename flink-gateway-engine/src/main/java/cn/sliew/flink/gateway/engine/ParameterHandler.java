package cn.sliew.flink.gateway.engine;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.Configuration;

/**
 * 配置的分组和排序。
 * 声明那些是必需，那些是可选的。
 * 可以组合成装饰器，或者类似 spring security dsl 那种
 * 异或者是组合模式？
 * @param <T>
 */
public interface ParameterHandler<T> {

    ConfigOption<T> getOption();

    T getValue();

    void apply(Configuration configuration);
}
