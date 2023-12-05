package com.bprodactivv.dataservice.core.data.metadata.cashLoader


import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.redis.RedisComponent
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class MetadataCacheLoader(
    private val metadataExtractor: MetadataExtractor,
    private val redisComponent: RedisComponent,

    ) {
    @PostConstruct
    fun saveMetadata() {

        val models = storeModelsMetadata()

        storeModelsDetailedMetadata(models)
    }

    private fun storeModelsDetailedMetadata(models: List<String>) {
        val mapper = ObjectMapper()
        models.forEach {
            redisComponent.redis().set(it, mapper.writeValueAsString(metadataExtractor.getClassDeclaredFields(it)))
        }
    }

    private fun storeModelsMetadata(): List<String> {
        val models = metadataExtractor.getModels()
        redisComponent.redis().rpush("models", *models.toTypedArray())
        return models
    }
}
