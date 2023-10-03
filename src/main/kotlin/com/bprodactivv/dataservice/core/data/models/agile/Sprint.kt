package com.bprodactivv.dataservice.core.data.models.agile

import jakarta.persistence.*
import java.time.LocalDate

@Entity
open class Sprint {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agile_Sprint_GEN")
    @SequenceGenerator(name = "agile_Sprint_GEN", sequenceName = "agile_Sprint_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    open var name: String? = null

    open var dateFrom: LocalDate? = null

    open var dateTo: LocalDate? = null

    @ManyToMany
    @JoinTable(
        name = "sprint_tasks",
        joinColumns = [JoinColumn(name = "sprint_id")],
        inverseJoinColumns = [JoinColumn(name = "task_id")]
    )
    open var tasks: MutableList<Task> = mutableListOf()
}