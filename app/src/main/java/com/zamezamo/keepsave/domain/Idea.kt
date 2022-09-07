package com.zamezamo.keepsave.domain


data class Idea(

    val id: String? = "",
    val colorPriority: Int? = 0,

    val title: String? = "",
    val location: Location? = null,
    val dateAndTime: DateAndTime? = null,

    val imgUri: String? = "",
    val description: String? = ""

) {

    data class DateAndTime(

        val day: Int = 0,
        val month: Int = 0,
        val year: Int = 0,

        val hour: Int = 0,
        val minute: Int = 0,

        )

    data class Location(

        val latitude: Double = 0.0,
        val longitude: Double = 0.0,

        val locationName: String = ""

    )

}