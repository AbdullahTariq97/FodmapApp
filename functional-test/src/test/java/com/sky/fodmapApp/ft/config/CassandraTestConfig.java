package com.sky.fodmapApp.ft.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.ReconnectionPolicy;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.time.Duration;
import java.util.List;

import static java.lang.System.getProperty;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Configuration
@ComponentScan("com.sky.fodmapApp.ft.*")
@Slf4j
public class CassandraTestConfig {

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspace;

    @Value("${spring.data.cassandra.port}")
    private int port;

    @Value("${spring.data.cassandra.contact-points}")
    private String contactPoints;

    @Bean("cassandraSession")
    @Profile("local")
    @DependsOn("cassandraContainer")
    public Session localCassandraSession() throws InterruptedException {
        System.out.println("Keyspace:" + keyspace);
        System.out.println("Port: " + port);
        System.out.println("contactPoints " + contactPoints);
        return fodmapCassandraSession();
    }

    @Bean
    @Profile("local")
    public void cassandraContainer() {
        DefaultDockerClientConfig.Builder config = DefaultDockerClientConfig.createDefaultConfigBuilder();
        DockerClient dockerClient = DockerClientBuilder
                .getInstance(config)
                .build();

        List<Container> containers = dockerClient.listContainersCmd().withNameFilter(singletonList("fodmap-cassandra")).exec();
        if (containers.isEmpty()) {
            CreateContainerResponse container = dockerClient.createContainerCmd("local/fodmap-cassandra")
                    .withPortBindings(PortBinding.parse("9042:9042"))
                    .withName("fodmap-cassandra")
                    .exec();

            dockerClient.startContainerCmd(container.getId()).exec();

            log.info("waiting for cassandra container to be healthy");
            await().atMost(Duration.ofSeconds(90L)).untilAsserted(() -> {
                InspectContainerResponse inspectContainerResponse = dockerClient.inspectContainerCmd(container.getId()).exec();
                assertThat(inspectContainerResponse.getState().getHealth().getStatus()).isEqualTo("healthy");
            });
            log.info("cassandra started successfully");
        } else {
            log.info("Cassandra container already running, using this for tests.");
        }
    }

    public Session fodmapCassandraSession() throws InterruptedException {
        log.info("KEYSPACE: {}, PCS -CONTACT-POINTS: {}, PORT: {}, ACTIVE PROFILE: {}", keyspace, contactPoints, port, getProperty("spring.profiles.active"));

        ReconnectionPolicy reconnectionPolicy = new ConstantReconnectionPolicy(10_000);
        SocketOptions socketOptions = new SocketOptions();
        socketOptions.setReadTimeoutMillis(5000);
        socketOptions.setConnectTimeoutMillis(5000);

        Cluster cluster = Cluster.builder()
                .addContactPoints(contactPoints.split(","))
                .withPort(port)
                .withSocketOptions(socketOptions)
                .withReconnectionPolicy(reconnectionPolicy)
                .build();

        return cluster.connect(keyspace);
    }
}