package com.bprodactivv.dataservice.core.data.metadata

import com.bprodactivv.dataservice.core.data.metadata.definition.FieldDefinition
import com.google.common.reflect.ClassPath
import org.springframework.stereotype.Component
import java.lang.reflect.Field

const val PLURAL = "PLURAL"
const val SINGULAR = "SINGULAR"

@Component
class MetadataExtractor(
    private val configuration: MetadataExtractorConfiguration
) {

    @Throws(ClassNotFoundException::class)
    fun getClassDeclaredFields(clazz: String): List<FieldDefinition> {
        val header = findClass(clazz)
        return header.declaredFields
            .filter { field -> !(field.name.equals("CLASS_TYPE") || field.name.equals("Companion")) }
            .map { field: Field -> createFieldDefinition(field) }
    }

    fun getModels(): List<String> {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
            .allClasses.stream()
            .filter { it.packageName.startsWith("BOOT-INF.classes.${configuration.modelsBasePath}") }
            .map { it.name.replace("BOOT-INF.classes.${configuration.modelsBasePath}.", "") }
            .filter { !it.contains("\$Companion") }
            .toList()
    }

    @Throws(ClassNotFoundException::class)
    fun findClass(clazz: String): Class<*> {
        return Class.forName("${configuration.modelsBasePath}.$clazz")
    }

    private fun createFieldDefinition(field: Field): FieldDefinition {
        val typeName = field.annotatedType.type.typeName
        val annotations = field.annotations

        val type = when (field.type.packageName.startsWith(configuration.modelsBasePath)) {
            true -> typeName.replace("${configuration.modelsBasePath}.", "")
            false -> typeName.replace("${field.type.packageName}.", "").replace("${configuration.modelsBasePath}.", "")
        }

        val constraints = when (annotations.size) {
            0 -> mutableListOf()
            else -> annotations
                .filter { it.annotationClass.qualifiedName!!.startsWith(configuration.constraintsPackage) || it.annotationClass.qualifiedName == "jakarta.persistence.Id" }
                .mapNotNull { it.annotationClass.simpleName }
        }

        val multiplicity = when (type.matches(Regex("List<[a-zA-Z.]*>"))) {
            true -> PLURAL
            false -> SINGULAR
        }

        return FieldDefinition(
            field.name,
            type,
            constraints,
            multiplicity
        )
    }
}