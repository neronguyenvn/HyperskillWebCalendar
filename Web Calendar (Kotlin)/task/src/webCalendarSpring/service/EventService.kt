package webCalendarSpring.service

import org.springframework.stereotype.Service
import webCalendarSpring.model.Event
import webCalendarSpring.repository.EventRepository
import java.time.LocalDate

@Service
class EventService(private val repository: EventRepository) {

    fun addEvent(event: Event) {
        repository.save(event)
    }

    fun getAllEvents(): List<Event> {
        return repository.findAll().toList()
    }

    fun getTodayEvents(): List<Event> {
        return getAllEvents().filter {
            it.date == LocalDate.now()
        }
    }
}
