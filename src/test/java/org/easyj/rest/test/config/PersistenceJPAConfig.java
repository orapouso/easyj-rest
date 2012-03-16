package org.easyj.rest.test.config;

import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan( { "org.easyj.orm" } )
@EnableTransactionManagement(proxyTargetClass=true)
public class PersistenceJPAConfig {
	
	private String driverClassName = "org.hsqldb.jdbc.JDBCDriver";
	
	private String url = "jdbc:hsqldb:mem:easyjtest";
	
	private String hibernateDialect = "org.hibernate.dialect.HSQLDialect";
	
	private boolean hibernateShowSql = false;
	
	private boolean jpaGenerateDdl = true;
        
        private String hibernateHbm2ddlAuto = "create-drop";
	
	public PersistenceJPAConfig(){
		super();
	}
	
	// beans
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(){
		final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource( restDataSource() );
		factoryBean.setPackagesToScan( new String[ ] { "org.rest" } );
		
		final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter(){
			{
				setDatabase( Database.HSQL );
				setDatabasePlatform( hibernateDialect );
				setShowSql( hibernateShowSql );
				setGenerateDdl( jpaGenerateDdl );
			}
		};
		factoryBean.setJpaVendorAdapter( vendorAdapter );
		
		factoryBean.setJpaProperties( additionlProperties() );
		
		return factoryBean;
	}
	
	@Bean
	public DataSource restDataSource(){
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName( driverClassName );
		dataSource.setUrl( url );
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}
	
	@Bean
	public JpaTransactionManager transactionManager(){
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory( entityManagerFactoryBean().getObject() );
		
		return transactionManager;
	}
	
	@Bean
	public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor(){
		return new PersistenceExceptionTranslationPostProcessor();
	}
	
	//
	final Properties additionlProperties(){
		return new Properties(){
			{
				// use this to inject additional properties in the EntityManager
                                setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
			}
		};
	}
	
}
