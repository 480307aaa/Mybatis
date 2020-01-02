package com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.yonyoucloud.ec.sns.error.ECConfigurationException;
import com.yonyoucloud.ec.sns.starter.core.support.beans.BeanUtils;
import com.yonyoucloud.ec.sns.starter.db.rw.splitting.DbReadWriteProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liuhaoi
 */
@RequiredArgsConstructor
@Slf4j
public class DataSourceFactory {

    private final DbReadWriteProperties properties;

    @Getter
    private DataSource masterDataSource;

    @Getter
    private List<DataSource> slaveDataSources;


    @PostConstruct
    public void init() {

        masterDataSource = createDataSource(properties.getMaster());

        List<DbReadWriteProperties.DataSourceConfig> slaves = properties.getSlaves();

        //兼容没有从库的情况
        if (CollectionUtils.isEmpty(slaves)) {
            slaveDataSources = Lists.newArrayList(masterDataSource);
        } else {
            slaveDataSources = slaves.stream()
                    .map(this::createDataSource)
                    .collect(Collectors.toList());
        }
    }


    private DataSource createDataSource(DbReadWriteProperties.DataSourceConfig config) {

        Map<String, String> introspect;
        try {
            introspect = BeanUtils.introspectAsStringFields(config);
            return DruidDataSourceFactory.createDataSource(introspect);
        } catch (Exception e) {
            String msg = "Exception when create datasource";
            log.error(msg, e);
            throw new ECConfigurationException(msg, e);
        }

    }


}
