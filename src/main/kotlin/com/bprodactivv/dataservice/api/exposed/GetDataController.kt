package com.bprodactivv.dataservice.api.exposed

import com.bprodactivv.dataservice.api.queries.LoadDataService
import com.bprodactivv.dataservice.core.proql.models.ProQLQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["load"])
class GetDataController(private val loadDataService: LoadDataService) {

    @PostMapping
    fun loadData(@RequestBody proQLQuery: ProQLQuery): ResponseEntity<Any?> {
        val load = loadDataService.load(proQLQuery)
        return ResponseEntity.ok(load)
    }

}