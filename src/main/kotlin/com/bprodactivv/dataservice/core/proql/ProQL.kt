package com.bprodactivv.dataservice.core.proql

import com.bprodactivv.dataservice.core.data.metadata.definition.FieldDefinition
import com.bprodactivv.dataservice.core.exceptions.UnknownFieldTypeException
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*
import jakarta.persistence.metamodel.EntityType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class ProQL<T> private constructor(
    private val entityManager: EntityManager,
    val criteriaBuilder: CriteriaBuilder,
    val criteria: CriteriaQuery<T>,
    val root: Root<T>,
    val rootEntity: EntityType<T>,
    val fields: List<FieldDefinition>,
) {

    private val joinPredicates: MutableList<Predicate> = mutableListOf()

    data class Builder<T : Any>(
        private var entityManager: EntityManager,
        private var criteriaBuilder: CriteriaBuilder? = null,
        private var root: Root<T>? = null,
        private var criteria: CriteriaQuery<T>? = null,
        private var rootEntity: EntityType<T>? = null,
        private var fields: List<FieldDefinition>? = null,
    ) {
        fun type(type: Class<T>) = apply {
            this.criteria = entityManager.criteriaBuilder.createQuery(type)
            this.root = criteria!!.from(type)
        }

        fun rootEntity(rootEntity: EntityType<T>) = apply { this.rootEntity = rootEntity }
        fun fields(fields: List<FieldDefinition>) = apply { this.fields = fields }
        fun build() = ProQL(entityManager, entityManager.criteriaBuilder, criteria!!, root!!, rootEntity!!, fields!!)
    }

    fun predicates(proQLQuery: ProQLQuery): MutableList<Predicate> {
        return predicatesFor(root, proQLQuery, fields)
    }

    fun predicatesFor(
        root: Path<*>,
        proQLQuery: ProQLQuery,
        rootFields: List<FieldDefinition>
    ): MutableList<Predicate> {
        val predicates = mutableListOf<Predicate>()
        proQLQuery.properties?.map { (key, value) ->
            when (rootFields.find { it.name == key }?.type) {
                "String" -> criteriaBuilder.like(root.get(key), "%${value as String}%")
                "Long" -> criteriaBuilder.equal(root.get<Long>(key), value)
                else -> throw UnknownFieldTypeException()
            }
        }?.let {
            predicates.addAll(it)
        }
        return predicates
    }

    fun where(predicates: List<Predicate>?): ProQL<T> {
        if (predicates?.isNotEmpty() == true) {
            criteria.where(
                *predicates.toTypedArray()
            )
        }
        return this
    }

    fun query(): List<T> {
        return entityManager.createQuery(criteria).resultList
    }

    fun queryPaginated(pageable: Pageable): Page<T> {
        val responseFromDb = entityManager.createQuery(criteria).resultList
        val pageNumber = if (pageable.pageNumber==1) 0 else (pageable.pageNumber-1)*pageable.pageSize
        return PageImpl(responseFromDb.subList(pageNumber, pageNumber+pageable.pageSize), pageable, responseFromDb.size.toLong())
    }

}