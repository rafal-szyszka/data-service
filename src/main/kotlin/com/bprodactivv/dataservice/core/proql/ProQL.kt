package com.bprodactivv.dataservice.core.proql

import com.bprodactivv.dataservice.core.data.metadata.definition.FieldDefinition
import com.bprodactivv.dataservice.core.exceptions.UnknownCriteriaException
import com.bprodactivv.dataservice.core.exceptions.UnknownFieldTypeException
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*
import jakarta.persistence.metamodel.EntityType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate

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
                "String" -> handleStringCriteria(root, value, key)
                "Long" -> handleLongCriteria(root, value, key)
                "LocalDate" -> handleLocalDateCriteria(root, value, key)
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

    fun queryPaginated(pageable: Pageable, data: List<T>): Page<T> {
        if (data.isEmpty()) {
            return PageImpl(data, pageable, 0L)
        }

        val offsetStart = pageable.pageNumber * pageable.pageSize
        val offsetEnd = offsetStart + pageable.pageSize

        return PageImpl(
            data.subList(
                offsetStart,
                when (offsetEnd > data.size) {
                    true -> data.size
                    false -> offsetEnd
                }
            ),
            pageable,
            data.size.toLong()
        )
    }

    private fun handleStringCriteria(root: Path<*>, value: Any, key: String): Predicate {
        return when (value) {
            is String -> criteriaBuilder.like(root.get(key), "%$value%")
            is List<*> -> {
                val predicates = value.map { criteriaBuilder.like(root.get(key), it as String) }
                criteriaBuilder.or(*predicates.toTypedArray())
            }

            is Map<*, *> -> handleMapCriteriaForString(root, value, key)
            else -> throw UnknownCriteriaException()
        }
    }

    private fun handleMapCriteriaForString(root: Path<*>, value: Map<*, *>, key: String): Predicate {
        return when {
            "equal" in value -> criteriaBuilder.equal(root.get<String>(key), value["equal"])
            "notEqual" in value -> criteriaBuilder.notEqual(root.get<String>(key), value["notEqual"])
            else -> throw UnknownCriteriaException()
        }
    }

    private fun handleLongCriteria(root: Path<*>, value: Any, key: String): Predicate {
        return when (value) {
            is String -> criteriaBuilder.equal(root.get<Long>(key), value)
            is Map<*, *> -> handleMapCriteriaForLong(root, value, key)
            else -> throw UnknownCriteriaException()
        }
    }

    private fun handleMapCriteriaForLong(root: Path<*>, value: Map<*, *>, key: String): Predicate {
        return when {
            "from" in value -> {
                if (value.containsKey("to")) {
                    val from = value["from"].toString().toLong()
                    val to = value["to"].toString().toLong()
                    criteriaBuilder.between(root.get(key), from, to)
                } else {
                    val from = value["from"].toString().toLong()
                    criteriaBuilder.greaterThanOrEqualTo(root.get(key), from)
                }
            }

            "to" in value -> {
                val from = value["to"].toString().toLong()
                criteriaBuilder.lessThanOrEqualTo(root.get(key), from)
            }

            else -> throw UnknownCriteriaException()
        }
    }

    private fun handleLocalDateCriteria(root: Path<*>, value: Any, key: String): Predicate {
        return when (value) {
            is String -> {
                val date = LocalDate.parse(value)
                criteriaBuilder.equal(root.get<LocalDate>(key), date)
            }

            is Map<*, *> -> handleMapLocalDateCriteria(root, value, key)
            else -> throw UnknownCriteriaException()
        }
    }

    private fun handleMapLocalDateCriteria(root: Path<*>, value: Map<*, *>, key: String): Predicate {
        return when {
            "from" in value -> {
                if (value.containsKey("to")) {
                    val dateFrom = LocalDate.parse(value["from"] as String)
                    val dateTo = LocalDate.parse(value["to"] as String)
                    criteriaBuilder.between(root.get(key), dateFrom, dateTo)
                } else {
                    val fromDate = LocalDate.parse(value["from"] as String)
                    criteriaBuilder.greaterThanOrEqualTo(root.get(key), fromDate)
                }
            }

            "to" in value -> {
                val fromDate = LocalDate.parse(value["to"] as String)
                criteriaBuilder.lessThanOrEqualTo(root.get(key), fromDate)
            }

            else -> throw UnknownCriteriaException()
        }
    }
}