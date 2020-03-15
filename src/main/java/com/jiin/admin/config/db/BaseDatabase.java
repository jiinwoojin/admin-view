package com.jiin.admin.config.db;

import com.jiin.admin.mapper.BaseMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * 기본 데이터베이스
 * JPA 활성
 */
@Configuration
@MapperScan(basePackages = "com.jiin.admin", annotationClass = BaseMapper.class,sqlSessionFactoryRef = "sqlSessionFactoryBean_BASE")
public class BaseDatabase {

	@Value("${project.datasource.base.driver-class-name}")
	private String driverClassName;
	@Value("${project.datasource.base.url}")
	private String url;
	@Value("${project.datasource.base.username}")
	private String username;
	@Value("${project.datasource.base.password}")
	private String password;
	@Value("${project.datasource.base.mybatis-configPath}")
	private String configPath;
	@Value("${project.datasource.base.mybatis-mapperLocations}")
	private String mapperLocations;

	@Primary
	@Bean
	public DataSource baseDataSource() {
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setUrl(url);
		dataSource.setUser(username);
		dataSource.setPassword(password);
		dataSource.setReWriteBatchedInserts(true);
		return dataSource;
	}

	@Primary
	@Bean("sqlSessionFactoryBean_BASE")
	public SqlSessionFactory baseSqlSessionFactory(@Qualifier("baseDataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setConfigLocation(applicationContext.getResource(configPath));
		bean.setMapperLocations(resolver.getResources(mapperLocations));
		return bean.getObject();
	}

	@Primary
	@Bean("transactionManager_BASE")
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

}
