package cn.sliew.flink.gateway.dao.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "cn.sliew.flink.gateway.dao.mapper.gateway",
        sqlSessionFactoryRef = GatewayConfig.GATEWAY_SQL_SESSION_FACTORY_NAME)
public class GatewayConfig extends MybatisPlusConfig {

    static final String GATEWAY_SQL_SESSION_FACTORY_NAME = "cn.sliew.flink.gateway.dao.config.GatewaySqlSessionFactory";

    @Autowired
    @Qualifier(DataSourceConfig.GATEWAY_DATA_SOURCE_NAME)
    private DataSource dataSource;

    @Bean(GATEWAY_SQL_SESSION_FACTORY_NAME)
    public SqlSessionFactory gatewaySqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setConfiguration(buildMybatisConfiguration());
        factoryBean.setGlobalConfig(buildGlobalConfig());
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/gateway/**/*.xml"));
        return factoryBean.getObject();
    }

    private MybatisConfiguration buildMybatisConfiguration() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
        return configuration;
    }

    private GlobalConfig buildGlobalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setBanner(false);
        return globalConfig;
    }

}
