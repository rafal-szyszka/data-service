package com.bprodactivv.dataservice.core.data.models.agile

import com.bprodactivv.dataservice.core.data.models.core.Client
import jakarta.persistence.*
import java.time.LocalDate

@Entity
open class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agile_Project_GEN")
    @SequenceGenerator(name = "agile_Project_GEN", sequenceName = "agile_Project_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    open var name: String? = null

    open var status: String? = null

    open var startDate: LocalDate? = null

    open var endDate: LocalDate? = null

    @ManyToOne
    @JoinColumn(name = "client_id")
    open var client: Client? = null

    @ManyToOne
    @JoinColumn(name = "ordering_client_id")
    open var orderingClient: Client? = null

    companion object {
        const val CLASS_TYPE = "agile.Project"
    }
}