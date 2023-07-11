package com.bprodactivv.dataservice.core.data.models.demo

import com.bprodactivv.dataservice.core.data.metadata.definition.constraints.NotInsertable
import com.bprodactivv.dataservice.core.data.metadata.definition.constraints.Required
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "demo_task")
open class DemoTask {
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
    open var demoProject: DemoProject? = null
}