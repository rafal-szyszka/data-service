package com.bprodactivv.dataservice.core.data.repos.prodactivvity

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.data.models.prodactivvity.Task
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

@Service("prodactivvity.Task")
class TaskPersistenceRepo(
    private val repo: TaskRepository,
    private val metadataExtractor: MetadataExtractor,
) : PersistenceRepo, ReadRepo {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private lateinit var proQL: ProQL<Task>

    private lateinit var entity: EntityType<Task>

    override fun save(x: Any): Any {
        return repo.save(x as Task)
    }

    override fun findById(id: Int): Any {
        return repo.findById(id.toLong())
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
                "orderer" -> {
                    val orderer = proQL.root.join(
                        entity.getSingularAttribute(
                            it.parentProperty,
                            metadataExtractor.findClass("organization.AppUser")
                        )
                    )
                    joinPredicates.addAll(
                        proQL.predicatesFor(
                            orderer,
                            it,
                            metadataExtractor.getClassDeclaredFields("organization.AppUser")
                        )
                    )
                }

                "performer" -> {
                    val performer = proQL.root.join(
                        entity.getSingularAttribute(
                            it.parentProperty,
                            metadataExtractor.findClass("organization.AppUser")
                        )
                    )
                    joinPredicates.addAll(
                        proQL.predicatesFor(
                            performer,
                            it,
                            metadataExtractor.getClassDeclaredFields("organization.AppUser")
                        )
                    )
                }

                "customer" -> {
                    val customer = proQL.root.join(
                        entity.getList(
                            it.parentProperty,
                            metadataExtractor.findClass("organization.Customer")
                        )
                    )
                    joinPredicates.addAll(
                        proQL.predicatesFor(
                            customer,
                            it,
                            metadataExtractor.getClassDeclaredFields("organization.Customer")
                        )
                    )
                }

                "project" -> {
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
        entity = entityManager.metamodel.entity(Task::class.java)
        proQL = ProQL.Builder<Task>(entityManager)
            .type(Task::class.java)
            .fields(metadataExtractor.getClassDeclaredFields("prodactivvity.Project"))
            .rootEntity(entityManager.metamodel.entity(Task::class.java))
            .build()
    }

}