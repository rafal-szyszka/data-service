package com.bprodactivv.dataservice.core.exceptions

import jakarta.persistence.EntityNotFoundException
import java.util.function.Supplier

class EntityNotFoundSupplier : Supplier<EntityNotFoundException> {
    override fun get(): EntityNotFoundException {
        return EntityNotFoundException()
    }

}
