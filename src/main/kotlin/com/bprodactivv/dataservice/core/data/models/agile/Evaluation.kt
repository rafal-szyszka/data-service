package com.bprodactivv.dataservice.core.data.models.agile

import jakarta.persistence.*

@Entity
open class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agile_Evaluation_GEN")
    @SequenceGenerator(name = "agile_Evaluation_GEN", sequenceName = "agile_Evaluation_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    open var quality: String? = null

    open var punctuality: String? = null

    @Lob
    open var explanation: String? = null

    companion object {
        const val CLASS_TYPE = "agile.Evaluation"
    }
}