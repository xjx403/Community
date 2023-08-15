package com.coder.community.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Configuration
public class ElasticsearchConfiguration extends AbstractElasticsearchConfiguration {
    /**
     * ElasticSearch 客户端配置
     *
     * @author geng
     * 2020/12/19
     */
    @Value("${spring.elasticsearch.rest.uris}")
    private String uris;
    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(uris)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

}
