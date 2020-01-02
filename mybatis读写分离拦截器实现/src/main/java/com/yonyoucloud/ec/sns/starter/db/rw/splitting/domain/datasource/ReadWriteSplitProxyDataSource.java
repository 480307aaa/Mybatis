package com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.datasource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author liuhaoi
 */
@Slf4j
@RequiredArgsConstructor
public class ReadWriteSplitProxyDataSource extends AbstractRoutingDataSource {

    private static final String MASTER = "MASTER";

    private final DataSourceFactory dataSourceFactory;

    private List<String> slaveDataSourceKeyList;

    @Override
    public void afterPropertiesSet() {

        Map<Object, Object> allDataSources = new HashMap<>(4);

        List<DataSource> slaveDataSources = dataSourceFactory.getSlaveDataSources();
        DataSource masterDataSource = dataSourceFactory.getMasterDataSource();

        allDataSources.put(MASTER, masterDataSource);

        slaveDataSourceKeyList = new ArrayList<>(slaveDataSources.size());
        for (int i = 0; i < slaveDataSources.size(); i++) {
            String dataSourceKey = "SLAVE_" + i;
            allDataSources.put(dataSourceKey, slaveDataSources.get(i));
            slaveDataSourceKeyList.add(dataSourceKey);
        }

        super.setDefaultTargetDataSource(masterDataSource);
        super.setTargetDataSources(allDataSources);

        super.afterPropertiesSet();
    }

    @Override
    protected String determineCurrentLookupKey() {

        DataOperationType dbOperationType = DataSourceContext.getDbOperationType();
        DataSourceType dataSourceType = DataSourceContext.getDataSourceIndicator();

        String dataSourceKey = chooseDataSource(dbOperationType, dataSourceType);

        if (log.isDebugEnabled()) {
            log.debug("Data Source [{}] will be used for db operation with operation type {}, data source type {}",
                    dataSourceKey, dbOperationType, dataSourceType);
        }

        DataSourceContext.setDbOperationType(null);
        return dataSourceKey;
    }

    private String chooseDataSource(DataOperationType dbOperationType, DataSourceType dataSourceType) {

        if (dataSourceType == DataSourceType.SLAVE && dbOperationType == DataOperationType.UPDATE) {
            log.error(
                    "FATAL: Wrong DataSourceType used, Data update operation must use MASTER data source, Auto Change TO Master");
            return MASTER;
        }

        if (dataSourceType != null) {
            if (DataSourceType.MASTER == dataSourceType) {
                return MASTER;
            } else {
                return getRandomBackupDatasource();
            }
        }

        if (DataOperationType.READ == dbOperationType) {
            return getRandomBackupDatasource();
        }

        return MASTER;
    }

    private String getRandomBackupDatasource() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, slaveDataSourceKeyList.size());
        return slaveDataSourceKeyList.get(randomNum);
    }

}