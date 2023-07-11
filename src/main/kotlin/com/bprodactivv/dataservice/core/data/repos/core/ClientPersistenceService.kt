package com.bprodactivv.dataservice.core.data.repos.core

import com.bprodactivv.dataservice.core.data.metadata.MetadataExtractor
import com.bprodactivv.dataservice.core.data.models.core.Client
import com.bprodactivv.dataservice.core.data.repos.AbstractPersistenceService
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service(Client.CLASS_TYPE)
class ClientPersistenceService(
    private val repository: ClientRepository,
    metadataExtractor: MetadataExtractor,
) : AbstractPersistenceService<Client>(
    metadataExtractor,
    Client::class.java,
    Client.CLASS_TYPE,
) {
    override fun save(x: Any): Any {
        return repository.save(x as Client)
    }

    override fun getRepository(): JpaRepository<Client, Long> {
        return repository
    }

    override fun addJoinsWithPredicates(proQLQuery: ProQLQuery): MutableList<Predicate> {
        return mutableListOf()
    }
}