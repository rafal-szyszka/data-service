package com.bprodactivv.dataservice.core.data.repos.demo

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.data.models.demo.Customer
import com.bprodactivv.dataservice.core.data.repos.PersistenceRepo
import com.bprodactivv.dataservice.core.data.repos.ReadRepo
import com.bprodactivv.dataservice.core.exceptions.UnknownRelationException
import com.bprodactivv.dataservice.core.proql.ProQL
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.metamodel.EntityType
import org.springframework.stereotype.Service

@Service("organization.Customer")
class CustomerPersistenceRepo(
    private val repo: CustomerRepository,
    private val metadataExtractor: MetadataExtractor,
) : PersistenceRepo, ReadRepo {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private lateinit var proQL: ProQL<Customer>

    private lateinit var entity: EntityType<Customer>

    override fun save(x: Any): Any {
        return repo.save(x as Customer)
    }

    override fun findById(id: Long): Any? {
        TODO("Not yet implemented")
    }

    override fun findAll(proQLQuery: ProQLQuery): Any {
        initProQL()

        val predicates = mutableListOf(
            *addJoinsWithPredicates(proQLQuery).toTypedArray(),
            *proQL.predicates(proQLQuery).toTypedArray()
        )

        return proQL.where(predicates)
            .query()
    }

    private fun addJoinsWithPredicates(proQLQuery: ProQLQuery): MutableList<Predicate> {
        val joinPredicates: MutableList<Predicate> = mutableListOf()
        proQLQuery.subQueries?.forEach {
            when (it.parentProperty) {
                "projects" -> {
                    val project = proQL.root.join(
                        entity.getList(
                            it.parentProperty,
                            metadataExtractor.findClass("prodactivvity.Project")
                        )
                    )
                    joinPredicates.addAll(
                        proQL.predicatesFor(
                            project,
                            it,
                            metadataExtractor.getClassDeclaredFields("prodactivvity.Project")
                        )
                    )
                }

                else -> throw UnknownRelationException()
            }
        }

        return joinPredicates
    }

    private fun initProQL() {
        entity = entityManager.metamodel.entity(Customer::class.java)
        proQL = ProQL.Builder<Customer>(entityManager)
            .type(Customer::class.java)
            .fields(metadataExtractor.getClassDeclaredFields("organization.Customer"))
            .rootEntity(entityManager.metamodel.entity(Customer::class.java))
            .build()
    }


}