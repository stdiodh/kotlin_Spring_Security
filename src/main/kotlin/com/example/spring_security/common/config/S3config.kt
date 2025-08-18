package com.example.spring_security.common.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class S3config (
  @Value("\${cloud.aws.credentials.access-key}")
    private val accessKey : String,
  @Value("\${cloud.aws.credentials.secret-key}")
    private val secretKey : String,
  @Value("\${cloud.aws.region.static}")
    private val region : String,
) {
    @Bean
    fun amazonS3Client() : AmazonS3 {
        val credentials = BasicAWSCredentials(accessKey, secretKey)
        return AmazonS3ClientBuilder
            .standard()
            .withRegion(region)
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .build()
    }
}