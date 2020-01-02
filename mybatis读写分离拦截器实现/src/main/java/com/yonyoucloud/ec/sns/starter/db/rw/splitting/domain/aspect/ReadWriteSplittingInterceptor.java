package com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.aspect;

import com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.datasource.DataOperationType;
import com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.datasource.DataSourceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.jdbc.UncategorizedSQLException;

import java.util.Properties;


/**
 * @author liuhaoi
 */
@Intercepts({
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}
        ),
        @Signature(
                type = Executor.class,
                method = "queryCursor",
                args = {MappedStatement.class, Object.class, RowBounds.class}
        ),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
        ),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
@Slf4j
public class ReadWriteSplittingInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

        DataOperationType type;

        if (SqlCommandType.SELECT == mappedStatement.getSqlCommandType()) {
            type = DataOperationType.READ;
        } else {
            type = DataOperationType.UPDATE;
        }
        DataSourceContext.setDbOperationType(type);

        try {
            return invocation.proceed();
        } catch (UncategorizedSQLException e) {
            //处理readOnly问题
            log.warn("Exception when run sql with type " + type);
            throw e;
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        String dialect = properties.getProperty("dialect");
        log.info("mybatis intercept dialect:" + dialect);
    }
}
