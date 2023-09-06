package com.emedlogix;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
@Configuration
@EnableJpaRepositories(basePackages = "com.emedlogix")
@EnableElasticsearchRepositories(basePackages = "com.emedlogix")
@ComponentScan(basePackages = {"com.emedlogix"})
public class EmedlogixServApplication {
    @Value("${elasticsearch.cluster-nodes}")
    String elsaticsearchEndpoint;
    @Value("${elasticsearch.cluster-port}")
    Integer elasticsearchport;

    public static void main(String[] args) {

        SpringApplication.run(EmedlogixServApplication.class, args);
    }

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restClient() {

        RestClientBuilder builder = RestClient.builder(new HttpHost(elsaticsearchEndpoint,elasticsearchport, "http"))
                .setRequestConfigCallback(requestConfigBuilder ->
                        requestConfigBuilder.setSocketTimeout(60000))
                // .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .setDefaultHeaders(compatibilityHeaders());
        return new RestHighLevelClient(builder);
    }

    private Header[] compatibilityHeaders() {
        return new Header[]{new BasicHeader(HttpHeaders.ACCEPT, "application/json;compatible-with=7"),
                new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json;compatible-with=7")};
    }


}
