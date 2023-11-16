package com.zamezamo.keepsave.domain


data class Idea(

    var id: String? = null,
    val colorPriority: Int? = null,

    val title: String? = null,
    val location: Location? = null,
    val dateAndTime: DateAndTime? = null,

    val imagesUri: List<String> = emptyList(),
    val description: String? = null

) : java.io.Serializable {

    data class DateAndTime(

        val day: Int? = null,
        val month: Int? = null,
        val year: Int? = null,

        val hour: Int? = null,
        val minute: Int? = null,

        ) : java.io.Serializable

    data class Location(

        val latitude: Double? = null,
        val longitude: Double? = null,

        val locationName: String? = null

    ) : java.io.Serializable

}