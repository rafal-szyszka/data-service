package com.bprodactivv.dataservice.core.data.models.agile

import jakarta.persistence.*

@Entity
open class Description {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agile_Description_GEN")
    @SequenceGenerator(name = "agile_Description_GEN", sequenceName = "agile_Description_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Lob
    open var businessDescription: String? = null

    @Lob
    open var definitionOfDone: String? = null

    @Lob
    open var technicalAnswer: String? = null

    companion object {
        const val CLASS_TYPE = "agile.Description"
    }
}