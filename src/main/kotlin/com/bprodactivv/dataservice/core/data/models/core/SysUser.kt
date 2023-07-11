package com.bprodactivv.dataservice.core.data.models.core

import jakarta.persistence.*
import java.time.LocalDate

@Entity
open class SysUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "core_SysUser_GEN")
    @SequenceGenerator(name = "core_SysUser_GEN", sequenceName = "core_SysUser_SEQ")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    open var login: String? = null

    open var password: String? = null

    open var email: String? = null

    open var firstName: String? = null

    open var lastName: String? = null

    open var birthDate: LocalDate? = null

    companion object {
        const val CLASS_TYPE: String = "core.SysUser"
    }
}