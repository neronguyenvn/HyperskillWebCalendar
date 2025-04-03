package webCalendarSpring.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import webCalendarSpring.model.Event
import webCalendarSpring.repository.EventRepository
import java.time.LocalDate

@Service
class EventService(private val repository: EventRepository) {

    fun addEvent(event: Event) {
        repository.save(event)
    }

    fun getAllEvents(
        startTime: LocalDate?,
        endTime: LocalDate?
    ): List<Event> {
        val events = repository.findAll()

        if (startTime != null && endTime != null) {
            return events.filter {
                val eventDate = it.date
                eventDate != null && eventDate in startTime..endTime
            }
        }

        return repository.findAll().toList()
    }

    fun getTodayEvents(): List<Event> {
        return getAllEvents(LocalDate.now(), LocalDate.now())
    }

    fun getEventById(id: Long): Event? {
        return repository.findByIdOrNull(id)
    }

    fun deleteEventById(id: Long): Event? {
        val event = getEventById(id) ?: return null
        repository.delete(event)
        return event
    }
}
