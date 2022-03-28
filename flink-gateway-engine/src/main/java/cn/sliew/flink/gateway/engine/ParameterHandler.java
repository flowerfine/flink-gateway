package cn.sliew.flink.gateway.engine;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.Configuration;

/**
 * @param <T>
 */
public interface ParameterHandler<T> {

    ConfigOption<T> getOption();

    T getValue();

    void apply(Configuration configuration);
}
