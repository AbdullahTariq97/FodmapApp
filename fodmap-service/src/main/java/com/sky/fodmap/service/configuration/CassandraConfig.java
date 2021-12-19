package com.sky.fodmap.service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableConfigurationProperties(CassandraProperties.class)
@EnableCassandraRepositories("com.sky.fodmap.service.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Autowired
    private CassandraProperties cassandraProperties;

    @Override
    protected String getKeyspaceName() {
        return cassandraProperties.getKeyspaceName();
    }

    @Bean
    public CassandraClusterFactoryBean cluster(){
        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
        cluster.setContactPoints(cassandraProperties.getContactPoints());
        cluster.setPort(cassandraProperties.getPort());
        return cluster;
    }

    @Bean
    public CassandraMappingContext cassandraMapping(){
        CassandraMappingContext mappingContext = new CassandraMappingContext();
        mappingContext.setUserTypeResolver(new
                SimpleUserTypeResolver(cluster().getObject(), cassandraProperties.getKeyspaceName()));
        return mappingContext;
    }
}
