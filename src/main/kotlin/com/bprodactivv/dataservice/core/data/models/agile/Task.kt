package com.bprodactivv.dataservice.core.data.models.agile

import com.bprodactivv.dataservice.core.data.models.core.SysUser
import jakarta.persistence.*
import java.time.LocalDate

@Entity
open class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agile_Task_GEN")
    @SequenceGenerator(name = "agile_Task_GEN", sequenceName = "agile_Task_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    open var name: String? = null

    @Lob
    open var description: String? = null

    @Lob
    open var answer: String? = null

    open var dateOfCreation: LocalDate? = null

    open var dueDate: LocalDate? = null

    open var endDate: LocalDate? = null

    open var sprint: String? = null

    open var status: String? = null

    open var type: String? = null

    open var urgency: String? = null

    open var priority: String? = null

    @ManyToOne
    @JoinColumn(name = "description_ext_id")
    open var descriptionExtension: Description? = null

    @ManyToOne
    @JoinColumn(name = "orderer_id")
    open var orderer: SysUser? = null

    @ManyToOne
    @JoinColumn(name = "performer_id")
    open var performer: SysUser? = null

    @ManyToOne
    @JoinColumn(name = "project_id")
    open var project: Project? = null

    @ManyToOne
    @JoinColumn(name = "capacity_id")
    open var capacity: Capacity? = null

    @ManyToOne
    @JoinColumn(name = "evaluation_id")
    open var evaluation: Evaluation? = null

    companion object {
        const val CLASS_TYPE = "agile.Task"
    }
}