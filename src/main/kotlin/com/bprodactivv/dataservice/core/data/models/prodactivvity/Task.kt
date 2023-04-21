package com.bprodactivv.dataservice.core.data.models.prodactivvity

import com.bprodactivv.dataservice.core.data.metadata.definition.constraints.NotInsertable
import com.bprodactivv.dataservice.core.data.metadata.definition.constraints.Required
import com.bprodactivv.dataservice.core.data.models.organization.AppUser
import com.bprodactivv.dataservice.core.data.models.organization.Customer
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
open class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Task_GEN")
    @SequenceGenerator(name = "Task_GEN", sequenceName = "Task_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @field:Required
    open var name: String? = null

    @Lob
    open var description: String? = null

    open var orderedOn: LocalDateTime? = null

    open var deadline: LocalDateTime? = null

    @ManyToOne
    @JoinColumn(name = "orderer_id")
    @field:Required
    @field:NotInsertable
    open var orderer: AppUser? = null

    @ManyToOne
    @JoinColumn(name = "performer_id")
    @field:Required
    @field:NotInsertable
    open var performer: AppUser? = null

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @field:NotInsertable
    open var customer: Customer? = null

    @ManyToOne
    @JoinColumn(name = "project_id")
    open var project: Project? = null
}