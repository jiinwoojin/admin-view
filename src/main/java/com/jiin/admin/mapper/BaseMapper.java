package com.jiin.admin.mapper;

import java.lang.annotation.*;

/**
 * database.base 매핑
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface BaseMapper {
}
