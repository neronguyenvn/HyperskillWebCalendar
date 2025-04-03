package webCalendarSpring.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@Entity
data class Event(

    @get:NotBlank
    @get:NotNull
    val event: String?,

    @get:NotNull
    val date: LocalDate?,

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 1
)
