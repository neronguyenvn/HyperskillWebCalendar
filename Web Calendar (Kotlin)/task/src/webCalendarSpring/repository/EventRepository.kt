package webCalendarSpring.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import webCalendarSpring.model.Event

@Repository
interface EventRepository : CrudRepository<Event, Long>
