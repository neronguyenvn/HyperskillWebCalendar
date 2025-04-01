package webCalendarSpring.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class Event(

    @get:NotBlank
    @get:NotNull
    val event: String?,

    @get:NotNull
    val date: LocalDate?
)
