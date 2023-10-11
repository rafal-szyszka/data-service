package com.bprodactivv.dataservice.core.data.metadata.cashLoader

import com.bprodactivv.dataservice.config.RedisConfig
import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import redis.clients.jedis.Jedis

@Component
class MetadataCacheLoader(
    private val metadataExtractor: MetadataExtractor,
    private val redisConfig: RedisConfig
) {
    @PostConstruct
    fun saveMetadata() {
        val jedis = Jedis(redisConfig.host, redisConfig.port.toInt())
        jedis.flushAll()

        val models = storeModelsMetadata(jedis)

        storeModelsDetailedMetadata(models, jedis)
    }

    private fun storeModelsDetailedMetadata(models: List<String>, jedis: Jedis) {
        val mapper = ObjectMapper()
        models.forEach {
            jedis.set(it, mapper.writeValueAsString(metadataExtractor.getClassDeclaredFields(it)))
        }
    }

    private fun storeModelsMetadata(jedis: Jedis): List<String> {
        val models = metadataExtractor.getModels()
        jedis.rpush("models", *models.toTypedArray())
        return models
    }
}