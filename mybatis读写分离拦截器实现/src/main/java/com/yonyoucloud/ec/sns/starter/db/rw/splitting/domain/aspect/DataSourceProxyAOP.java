package com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.aspect;

import com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.datasource.DataSourceContext;
import com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.datasource.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;

/**
 * @author liuhaoi
 */
@Aspect
@Slf4j
public class DataSourceProxyAOP implements Ordered {


    @Pointcut("@annotation(com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.aspect.UseMasterDataSource) || @annotation(org.springframework.transaction.annotation.Transactional)")
    public void annotationPointCut() {
    }

    @Around("annotationPointCut()")
    public Object proceed(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        DataSourceContext.setDataSourceIndicator(DataSourceType.MASTER);
        try {
            return proceedingJoinPoint.proceed();
        } finally {
            DataSourceContext.setDataSourceIndicator(null);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

}