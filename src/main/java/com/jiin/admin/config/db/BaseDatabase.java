package com.jiin.admin.config.db;

import com.jiin.admin.mapper.BaseMapper;
import com.jiin.admin.mapper.MapMapper;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 기본 데이터베이스
 * JPA 활성
 */
@Configuration
@MapperScan(basePackages = "com.jiin.admin", annotationClass = BaseMapper.class,sqlSessionFactoryRef = "sqlSessionFactoryBean_BASE")
@EnableJpaRepositories(basePackages = "com.jiin.admin.website", transactionManagerRef = "transactionManager_BASE")
@EntityScan("com.jiin.admin.entity")
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
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setTestOnBorrow(true);
		dataSource.setValidationQuery("SELECT 1");
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

	@Bean("transactionManager_BASE")
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(baseDataSource());
	}

}
