package com.jiin.admin.config.db;

import com.jiin.admin.mapper.MapMapper;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;


@Configuration
@MapperScan(basePackages = "com.jiin.admin", annotationClass = MapMapper.class,sqlSessionFactoryRef = "sqlSessionFactoryBean_MAP")
public class MapDatabase {

	@Value("${project.datasource.map.driver-class-name}")
	private String driverClassName;
	@Value("${project.datasource.map.url}")
	private String url;
	@Value("${project.datasource.map.username}")
	private String username;
	@Value("${project.datasource.map.password}")
	private String password;
	@Value("${project.datasource.map.mybatis-configPath}")
	private String configPath;
	@Value("${project.datasource.map.mybatis-mapperLocations}")
	private String mapperLocations;

	@Bean
	public DataSource mapDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setTestOnBorrow(true);
		dataSource.setValidationQuery("SELECT 1");
		return dataSource;
	}

	@Bean("sqlSessionFactoryBean_MAP")
	public SqlSessionFactory mapSqlSessionFactory(
			@Qualifier("mapDataSource") DataSource dataSource
			, ApplicationContext applicationContext) throws Exception {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setConfigLocation(applicationContext.getResource(configPath));
		bean.setMapperLocations(resolver.getResources(mapperLocations));
		return bean.getObject();
	}

	/*
	@Bean
	public PlatformTransactionManager mapAnnotationDrivenTransactionManager() {
		return new DataSourceTransactionManager(mapDataSource());
	}
	*/
}
