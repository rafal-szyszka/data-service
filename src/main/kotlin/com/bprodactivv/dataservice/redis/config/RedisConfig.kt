package com.bprodactivv.dataservice.redis.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class RedisConfig {
    @Value("\${redis.host}")
    lateinit var host: String

    @Value("\${redis.port}")
    lateinit var port: String
}