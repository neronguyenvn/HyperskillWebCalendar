package webCalendarSpring.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import webCalendarSpring.model.AddEventResponse
import webCalendarSpring.model.Event
import webCalendarSpring.model.asResponse
import webCalendarSpring.service.EventService

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
            .body(event.asResponse(message = "The event has been added!"))
    }

    @GetMapping
    fun getAllEvents(): ResponseEntity<List<Event>> {
        val events = service.getAllEvents()
        if (events.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NO_CONTENT)
        }

        return ResponseEntity.ok(events)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    private fun handleNotValidException(): ResponseEntity<Any> {
        return ResponseEntity.badRequest().build()
    }
}
