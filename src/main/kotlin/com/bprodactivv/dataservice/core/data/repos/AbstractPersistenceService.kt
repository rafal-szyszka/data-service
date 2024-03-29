package com.bprodactivv.dataservice.core.data.repos

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.proql.ProQL
import com.bprodactivv.dataservice.core.proql.ProQLPersistenceService
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import com.bprodactivv.dataservice.core.proql.models.ProQLSubQuery
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.metamodel.EntityType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository

abstract class AbstractPersistenceService<T : Any>(
    protected val metadataExtractor: MetadataExtractor,
    private val clazz: Class<T>,
    private val classType: String,
) : ProQLPersistenceService, ReadRepo {

    protected lateinit var proQL: ProQL<T>

    protected lateinit var entity: EntityType<T>

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    abstract override fun save(x: Any): Any

    abstract fun getRepository(): JpaRepository<T, Long>

    abstract fun addJoinsWithPredicates(proQLQuery: ProQLQuery): MutableList<Predicate>

    @Suppress("KotlinConstantConditions")
    override fun findAll(proQLQuery: ProQLQuery): Any {
        val predicates = mutableListOf(
            *addJoinsWithPredicates(proQLQuery).toTypedArray(),
            *proQL.predicates(proQLQuery).toTypedArray()
        )

        if (proQLQuery.size != null && proQLQuery.page != null) {
            return getPaginatedResult(predicates, proQLQuery)
        }

        if (predicates.isEmpty()) {
            return getRepository().findAll()
        }

        return proQL.where(predicates).query()
    }

    private fun getPaginatedResult(predicates: MutableList<Predicate>, proQLQuery: ProQLQuery): Page<T> {
        val data: List<T> = if (predicates.isEmpty()) {
            getRepository().findAll()
        } else {
            proQL.where(predicates).query()
        }

        return proQL.queryPaginated(
            PageRequest.of(proQLQuery.page!! - 1, proQLQuery.size!!),
            data
        )
    }

    override fun findById(id: Long): Any? {
        return getRepository().findById(id)
    }

    protected fun join(proQLSubQuery: ProQLSubQuery, classType: String, isSingular: Boolean): MutableList<Predicate> {
        val joined = when (isSingular) {
            true -> proQL.root.join(
                entity.getSingularAttribute(
                    proQLSubQuery.parentProperty,
                    metadataExtractor.findClass(classType)
                )
            )

            false -> proQL.root.join(
                entity.getList(
                    proQLSubQuery.parentProperty,
                    metadataExtractor.findClass(classType)
                )
            )
        }

        return proQL.predicatesFor(joined, proQLSubQuery, metadataExtractor.getClassDeclaredFields(classType))
    }

    @PostConstruct
    private fun buildProQLEngine() {
        proQL = ProQL.Builder<T>(entityManager)
            .type(clazz)
            .fields(metadataExtractor.getClassDeclaredFields(classType))
            .rootEntity(entityManager.metamodel.entity(clazz))
            .build()
    }

    @PostConstruct
    private fun buildEntity() {
        entity = entityManager.metamodel.entity(clazz)
    }

}