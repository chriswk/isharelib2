package com.chriswk.isharelib.config;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories(basePackages = "com.chriswk.isharelib")
public class IsharelibDataConfiguration extends Neo4jConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(IsharelibDataConfiguration.class);

    @Value("#{systemEnvironment['NEO4J_URL']}")
    private String neo4jUrl;

    @Override
    @Autowired
    public void setGraphDatabaseService(GraphDatabaseService service) {
        LOGGER.info("GraphDatabaseService set to {}", service);
        super.setGraphDatabaseService(service);
    }

    @Bean(destroyMethod = "shutdown")
    public GraphDatabaseService graphDatabaseService() {
        final Map<String, String> options = new HashMap<>();
        options.put("enable_remote_shell", "true");
        return new GraphDatabaseFactory().newEmbeddedDatabase(Optional.ofNullable(neo4jUrl).orElse("neo4jdb"));
    }
}
