package com.bprodactivv.dataservice.core.data.metadata.definition.constraints

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class SaveByText(val field: String)
