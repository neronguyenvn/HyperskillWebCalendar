package webCalendarSpring.controller

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/event")
class CalendarController {

    @GetMapping("/today")
    fun getToday(): ResponseEntity<List<Unit>> {
        return ResponseEntity.ok().body(emptyList())
    }
}
