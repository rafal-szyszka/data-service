package com.bprodactivv.dataservice.api.exposed

import com.bprodactivv.dataservice.api.commands.MetadataService
import com.bprodactivv.dataservice.core.data.metadata.definition.FieldDefinition
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["meta"])
class MetadataController(private val metadataService: MetadataService) {

    @GetMapping
    fun getModels(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(metadataService.getModels())
    }

    @GetMapping("/{className}")
    fun getClass(@PathVariable className: String): ResponseEntity<List<FieldDefinition>> {
        return ResponseEntity.ok(metadataService.getClassData(className))
    }

}