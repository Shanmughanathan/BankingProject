package com.example.BankingProject.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String database_url;

    @Bean
    public SQLServerDataSource dataSource(){
        SQLServerDataSource dataSource = new SQLServerDataSource();
        dataSource.setURL(database_url);
        return dataSource;
    }
}
