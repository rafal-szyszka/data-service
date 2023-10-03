package com.bprodactivv.dataservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class RedisConfig {
    @Value("\${redis.host}")
    lateinit var host : String
    @Value("\${redis.port}")
    lateinit var port : String
}