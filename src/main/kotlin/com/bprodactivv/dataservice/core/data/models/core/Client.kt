package com.bprodactivv.dataservice.core.data.models.core

import jakarta.persistence.*

@Entity
open class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "core_Client_GEN")
    @SequenceGenerator(name = "core_Client_GEN", sequenceName = "core_Client_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    open var name: String? = null

    open var acronym: String? = null

    open var taxNumber: String? = null

    companion object {
        const val CLASS_TYPE = "core.Client"
    }
}