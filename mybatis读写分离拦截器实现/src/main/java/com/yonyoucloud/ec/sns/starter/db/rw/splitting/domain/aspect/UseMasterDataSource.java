package com.yonyoucloud.ec.sns.starter.db.rw.splitting.domain.aspect;


import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UseMasterDataSource {
}
