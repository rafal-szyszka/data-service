package com.bprodactivv.dataservice.core.data.repos

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.proql.ProQL
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import com.bprodactivv.dataservice.core.proql.models.ProQLSubQuery
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.metamodel.EntityType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

abstract class AbstractPersistenceService<T : Any>(
    protected val metadataExtractor: MetadataExtractor,
    private val clazz: Class<T>,
    private val classType: String,
) {

    protected var proQL: ProQL<T> = buildProQLEngine()

    protected var entity: EntityType<T> = buildEntity()

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    abstract fun save(x: Any): Any

    abstract fun getRepository(): JpaRepository<T, Long>

    abstract fun addJoinsWithPredicates(proQLQuery: ProQLQuery): MutableList<Predicate>

    fun findAll(proQLQuery: ProQLQuery): Any {
        val predicates = mutableListOf(
            *addJoinsWithPredicates(proQLQuery).toTypedArray(),
            *proQL.predicates(proQLQuery).toTypedArray()
        )

        return proQL.where(predicates)
            .query()
    }

    fun findById(id: Long): Optional<T> {
        return getRepository().findById(id)
    }

    protected fun join(proQLSubQuery: ProQLSubQuery, classType: String): MutableList<Predicate> {
        val client = proQL.root.join(
            entity.getList(
                proQLSubQuery.parentProperty,
                metadataExtractor.findClass(classType)
            )
        )

        return proQL.predicatesFor(client, proQLSubQuery, metadataExtractor.getClassDeclaredFields(classType))
    }

    private fun buildProQLEngine(): ProQL<T> {
        return ProQL.Builder<T>(entityManager)
            .type(clazz)
            .fields(metadataExtractor.getClassDeclaredFields(classType))
            .rootEntity(entityManager.metamodel.entity(clazz))
            .build()
    }

    private fun buildEntity(): EntityType<T> {
        return entityManager.metamodel.entity(clazz)
    }

}