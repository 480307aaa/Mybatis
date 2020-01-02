package com.yonyoucloud.ec.sns.starter.db.rw.splitting;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author liuhaoi
 */
@Data
@Validated
@ConfigurationProperties(prefix = DbReadWriteProperties.PROP_ROOT)
public class DbReadWriteProperties {

    public static final String PROP_ROOT = "yonyoucloud.upesn.datasource";
    public static final String PROP_CHECK = PROP_ROOT + ".master.url";

    @NotNull
    @Valid
    @NestedConfigurationProperty
    private DataSourceConfig master;

    @NotNull
    @Valid
    @NestedConfigurationProperty
    private List<DataSourceConfig> slaves;


    @Data
    @Validated
    public static class DataSourceConfig {

        @Valid
        @NotNull
        private String url;

        @Valid
        @NotNull
        private String username;

        @Valid
        @NotNull
        private String password;

        private String driverClassName = "com.mysql.jdbc.Driver";

        private Integer minIdle = 10;
        private Integer maxActive = 200;
        private Integer initialSize = 10;
        private Integer maxWait = 300000;
        private Integer timeBetweenEvictionRunsMillis = 120000;
        private Integer minEvictableIdleTimeMillis = 300000;
        private String validationQuery = "select 'x'";
        private Boolean testWhileIdle = true;
        private Boolean testOnBorrow = false;
        private Boolean testOnReturn = false;
        private Boolean poolPreparedStatements = true;
        private Integer maxPoolPreparedStatementPerConnectionSize = 50;
        private Boolean removeAbandoned = true;
        private String filters = "stat";


    }

}