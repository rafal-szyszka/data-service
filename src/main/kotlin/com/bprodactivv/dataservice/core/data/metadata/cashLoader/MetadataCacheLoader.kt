package com.bprodactivv.dataservice.core.data.metadata.cashLoader

import com.bprodactivv.dataservice.config.RedisConfig
import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import redis.clients.jedis.Jedis

@Component
class MetadataCacheLoader(private val metadataExtractor: MetadataExtractor, private val redisConfig: RedisConfig) {
    @PostConstruct
    fun saveMetadata() {
        val jedis = Jedis(redisConfig.host, redisConfig.port.toInt())
        jedis.flushAll()
        jedis.rpush("models", *metadataExtractor.getModels().toTypedArray())
    }
}