package com.bprodactivv.dataservice.core.data.models.agile

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
open class Capacity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agile_Capacity_GEN")
    @SequenceGenerator(name = "agile_Capacity_GEN", sequenceName = "agile_Capacity_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(precision = 8, scale = 2)
    open var budget: BigDecimal? = null

    @Column(precision = 8, scale = 2)
    open var estimation: BigDecimal? = null

    @Column(precision = 8, scale = 2)
    open var timeLeft: BigDecimal? = null

    @Column(precision = 8, scale = 2)
    open var timeConsumed: BigDecimal? = null

    companion object {
        const val CLASS_TYPE = "agile.Capacity"
    }
}