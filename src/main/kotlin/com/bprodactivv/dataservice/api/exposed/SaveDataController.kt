package com.bprodactivv.dataservice.api.exposed

import com.bprodactivv.dataservice.api.commands.SaveDataService
import com.bprodactivv.dataservice.core.proql.models.ProQLCommand
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["save"])
class SaveDataController(private val saveDataService: SaveDataService) {

    @PostMapping
    fun save(@RequestBody command: ProQLCommand): ResponseEntity<Any> {
        return ResponseEntity.ok(
            saveDataService.saveData(command)
        )
    }

    @PostMapping("/all")
    fun saveAll(@RequestBody commands: List<ProQLCommand>): ResponseEntity<List<Any>> {
        return ResponseEntity.ok(
            saveDataService.saveAllData(commands)
        )
    }

}