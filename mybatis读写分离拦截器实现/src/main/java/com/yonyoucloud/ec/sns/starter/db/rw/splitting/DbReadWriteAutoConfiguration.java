package com.yonyoucloud.ec.sns.starter.db.rw.splitting;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.aspect.DataSourceProxyAOP;
import com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.aspect.ReadWriteSplittingInterceptor;
import com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.datasource.DataSourceFactory;
import com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.datasource.ReadWriteSplitProxyDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author liuhaoi
 */
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
@ConditionalOnProperty(DbReadWriteProperties.PROP_CHECK)
@AutoConfigureBefore({DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
@RequiredArgsConstructor
@EnableConfigurationProperties(DbReadWriteProperties.class)
public class DbReadWriteAutoConfiguration {

    private final DbReadWriteProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public DataSourceFactory dataSourceFactory() {
        return new DataSourceFactory(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReadWriteSplittingInterceptor readWriteSplittingInterceptor() {
        return new ReadWriteSplittingInterceptor();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnMissingBean
    public ReadWriteSplitProxyDataSource readWriteSplitProxyDataSource(DataSourceFactory dataSourceFactory) {
        log.info("Initializing Read Write Split Proxy DataSource");
        return new ReadWriteSplitProxyDataSource(dataSourceFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSourceProxyAOP dataSourceProxyAOP() {
        return new DataSourceProxyAOP();
    }


}
