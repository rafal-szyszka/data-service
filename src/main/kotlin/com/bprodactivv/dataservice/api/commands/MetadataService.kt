package com.bprodactivv.dataservice.api.commands

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.data.metadata.definition.FieldDefinition
import org.springframework.stereotype.Service

@Service
class MetadataService(private val metadataExtractor: MetadataExtractor) {

    fun getModels(): List<String> {
        return metadataExtractor.getModels()
    }

    fun getClassData(clazz: String): List<FieldDefinition> {
        return metadataExtractor.getClassDeclaredFields(clazz)
    }

}