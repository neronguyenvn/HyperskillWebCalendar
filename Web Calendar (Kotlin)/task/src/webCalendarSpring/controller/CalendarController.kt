package webCalendarSpring.controller

import jakarta.validation.Valid
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import webCalendarSpring.model.AddEventResponse
import webCalendarSpring.model.Event
import webCalendarSpring.model.MessageResponse
import webCalendarSpring.model.asResponse
import webCalendarSpring.service.EventService
import java.time.LocalDate

@RestController
@RequestMapping("/event")
class CalendarController(private val service: EventService) {

    @GetMapping("/today")
    fun getTodayEvents(): ResponseEntity<List<Event>> {
        val events = service.getTodayEvents()
        return ResponseEntity.ok(events)
    }

    @PostMapping
    fun addEvent(
        @RequestBody @Valid event: Event
    ): ResponseEntity<AddEventResponse> {
        service.addEvent(event)
        return ResponseEntity
            .ok()
            .body(event.asResponse(message = ADDED_MESSAGE))
    }

    @GetMapping
    fun getAllEvents(
        @RequestParam("start_time") startTime: LocalDate?,
        @RequestParam("end_time") endTime: LocalDate?
    ): ResponseEntity<List<Event>> {
        val events = service.getAllEvents(
            startTime = startTime,
            endTime = endTime
        )
        
        if (events.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NO_CONTENT)
        }

        return ResponseEntity.ok(events)
    }

    @GetMapping("/{id}")
    fun getEventById(@PathVariable id: Long): ResponseEntity<Any> {
        val event = service.getEventById(id) ?: run {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(MessageResponse(NOT_FOUND_MESSAGE))
        }

        return ResponseEntity.ok(event)
    }

    @DeleteMapping("/{id}")
    fun deleteEventById(@PathVariable id: Long): ResponseEntity<Any> {
        val event = service.deleteEventById(id) ?: run {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(MessageResponse(NOT_FOUND_MESSAGE))
        }

        return ResponseEntity.ok(event)
    }


    @ExceptionHandler(MethodArgumentNotValidException::class)
    private fun handleNotValidException(): ResponseEntity<Any> {
        return ResponseEntity.badRequest().build()
    }

    companion object {
        private const val ADDED_MESSAGE = "The event has been added!"
        private const val NOT_FOUND_MESSAGE = "The event doesn't exist!"
    }
}
