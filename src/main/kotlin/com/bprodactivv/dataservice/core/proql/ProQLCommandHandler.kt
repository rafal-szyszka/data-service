package com.bprodactivv.dataservice.core.proql

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.data.metadata.PLURAL
import com.bprodactivv.dataservice.core.data.repos.PersistenceRepo
import com.bprodactivv.dataservice.core.proql.models.ProQLCommand
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.util.AbstractMap.SimpleEntry

@Component
class ProQLCommandHandler(
    private val reposByType: Map<String, PersistenceRepo>,
    private val jackson: ObjectMapper,
    private val metadataExtractor: MetadataExtractor,
) {

    @Throws(ClassNotFoundException::class)
    fun save(command: ProQLCommand): Any? {
        val properties: List<SimpleEntry<String, Any>>? =
            command.commands?.map { SimpleEntry(it.parentProperty, save(it)) }

        updateNodeProperties(command, properties)

        val serializedObject = jackson.writeValueAsString(command.properties)

        if (command.properties != null && command.properties?.containsKey("id") == true) {
            val id = command.properties?.get("id")
            if (id != null) {
                return reposByType[command.type]!!.findById(id as Long)
            }
        }

        return reposByType[command.type]!!.save(
            jackson.readValue(serializedObject, metadataExtractor.findClass(command.type))
        )
    }

    private fun updateNodeProperties(
        command: ProQLCommand,
        properties: List<SimpleEntry<String, Any>>?
    ) {
        val objDefinition = metadataExtractor.getClassDeclaredFields(command.type)

        val groupingBy = properties?.groupingBy { it.key }
            ?.eachCount()

        groupingBy?.forEach { prop ->
            val fieldDefinition = objDefinition.find { it.name == prop.key }

            if (fieldDefinition?.multiplicity == PLURAL) {
                command.properties?.set(prop.key, properties.filter { property -> property.key == prop.key }
                    .map { property -> property.value } as MutableList<Any>)
            } else {
                command.properties?.set(prop.key, properties.filter { property -> property.key == prop.key }
                    .takeLast(1)
                    .map { property -> property.value }[0])
            }
        }
    }
}