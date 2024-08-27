package com.mydaytodo.sfa.asset.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Getter
@Setter
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "aws")
@EnableDynamoDBRepositories(basePackages = "com.mydaytodo.web.backend.repository")
@Slf4j
public class AWSConfig {

    private String region;
    private String key;
    private String secret;
    private String s3UploadBucketName;

    @Value("${aws.dynamo-db.amazonDBEndpoint}")
    private String amazonDBEndpoint;

    @Value("${aws.upload-limit}")
    private Integer uploadLimit;

    /**
     * AWS region set to AP_SOUTHEAST_2
     * It can be changed in the config.yaml file
     *
     * @return
     */
    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        log.info("About to create DynamoDB client with {}", amazonDBEndpoint);
        return new AmazonDynamoDBClient(amazonAwsCredentials())
                .withEndpoint(amazonDBEndpoint)
                .withRegion(Regions.fromName(region));
    }

    @Bean
    public AWSCredentials amazonAwsCredentials() {
        return new BasicAWSCredentials(key, secret);
    }

    @Bean
    public AmazonS3Client s3Client() {
        return new AmazonS3Client(amazonAwsCredentials())
                .withRegion(Regions.fromName(region));
    }
}
