package webCalendarSpring.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException.BadRequest
import webCalendarSpring.model.AddEventResponse
import webCalendarSpring.model.Event
import webCalendarSpring.model.asResponse
import java.lang.RuntimeException

@RestController
class CalendarController {

    @GetMapping("/event/today")
    fun getToday(): ResponseEntity<List<Unit>> {
        return ResponseEntity.ok().body(emptyList())
    }

    @PostMapping("/event")
    fun addEvent(
        @RequestBody @Valid event: Event
    ): ResponseEntity<AddEventResponse> {
        return ResponseEntity
            .ok()
            .body(event.asResponse(message = "The event has been added!"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    private fun handleNotValidException(): ResponseEntity<Any> {
        return ResponseEntity.badRequest().build()
    }
}
