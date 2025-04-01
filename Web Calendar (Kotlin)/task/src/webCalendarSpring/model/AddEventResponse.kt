package webCalendarSpring.model

data class AddEventResponse(
    val message: String,
    val event: String,
    val date: String
)

fun Event.asResponse(message: String) = AddEventResponse(
    message = message,
    event = event.orEmpty(),
    date = date.toString()
)
