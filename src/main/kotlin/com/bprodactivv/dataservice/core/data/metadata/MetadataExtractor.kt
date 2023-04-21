package com.bprodactivv.dataservice.core.data.metadata

import com.bprodactivv.dataservice.core.data.metadata.definition.FieldDefinition
import com.google.common.reflect.ClassPath
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import java.lang.reflect.Field

const val PLURAL = "PLURAL"
const val SINGULAR = "SINGULAR"

@Component
@PropertySource(value = ["classpath:DataHeaderExtractor.properties"])
class MetadataExtractor {

    @Value("\${modelsBasePath}")
    private lateinit var modelsBasePath: String

    @Value("\${constraintsPackage}")
    private lateinit var constraintsPackage: String

    @Throws(ClassNotFoundException::class)
    fun getClassDeclaredFields(clazz: String): List<FieldDefinition> {
        val header = findClass(clazz)
        return header.declaredFields
            .map { field: Field -> createFieldDefinition(field) }
    }

    fun getModels(): List<String> {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
            .allClasses.stream()
            .filter { it.packageName.startsWith(modelsBasePath) }
            .map { it.name.replace("$modelsBasePath.", "") }
            .toList()
    }

    @Throws(ClassNotFoundException::class)
    fun findClass(clazz: String): Class<*> {
        return Class.forName("$modelsBasePath.$clazz")
    }

    private fun createFieldDefinition(field: Field): FieldDefinition {
        val typeName = field.annotatedType.type.typeName
        val annotations = field.annotations

        val type = when (field.type.packageName.startsWith(modelsBasePath)) {
            true -> typeName.replace("$modelsBasePath.", "")
            false -> typeName.replace("${field.type.packageName}.", "").replace("$modelsBasePath.", "")
        }

        val constraints = when (annotations.size) {
            0 -> mutableListOf()
            else -> annotations
                .filter { it.annotationClass.qualifiedName!!.startsWith(constraintsPackage) || it.annotationClass.qualifiedName == "jakarta.persistence.Id" }
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