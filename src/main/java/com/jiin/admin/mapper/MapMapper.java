package com.jiin.admin.mapper;

import java.lang.annotation.*;

/**
 * database.map 매핑
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface MapMapper {
}
