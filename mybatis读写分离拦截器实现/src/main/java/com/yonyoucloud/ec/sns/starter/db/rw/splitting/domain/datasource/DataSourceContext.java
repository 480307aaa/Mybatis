package com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.datasource;


/**
 * @author liuhaoi
 */
public class DataSourceContext {

    private final static ThreadLocal<DataSourceType> DATA_SOURCE_INDICATOR = new ThreadLocal<>();
    private final static ThreadLocal<DataOperationType> DB_OPERATION_TYPE = new ThreadLocal<>();


    public static void setDataSourceIndicator(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            DATA_SOURCE_INDICATOR.remove();
            return;
        }
        DATA_SOURCE_INDICATOR.set(dataSourceType);
    }

    public static DataSourceType getDataSourceIndicator() {
        return DATA_SOURCE_INDICATOR.get();
    }

    public static void setDbOperationType(DataOperationType dbOperationType) {
        if (dbOperationType == null) {
            DB_OPERATION_TYPE.remove();
            return;
        }
        DB_OPERATION_TYPE.set(dbOperationType);
    }

    public static DataOperationType getDbOperationType() {
        DataOperationType dbOperationType = DB_OPERATION_TYPE.get();

        if (null == dbOperationType) {
            dbOperationType = DataOperationType.UPDATE;
        }

        return dbOperationType;
    }

    public static void clear() {
        DB_OPERATION_TYPE.remove();
        DATA_SOURCE_INDICATOR.remove();
    }

}
