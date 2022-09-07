package com.zamezamo.keepsave.utils

import android.annotation.SuppressLint
import com.zamezamo.keepsave.domain.Idea.DateAndTime
import java.text.SimpleDateFormat
import java.util.*

object DateTimeConverter {

    @SuppressLint("SimpleDateFormat")
    fun convert(dateAndTime: DateAndTime?, currentCalendar: Calendar): String? {

        if (dateAndTime == null)
            return null

        val ideaCalendar =
            GregorianCalendar(
                dateAndTime.year,
                dateAndTime.month,
                dateAndTime.day,
                dateAndTime.hour,
                dateAndTime.minute
            )

        return if (ideaCalendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR))
            SimpleDateFormat("MMMM d, y, h:mm a").format(ideaCalendar.time)
        else if (ideaCalendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR))
            SimpleDateFormat("h:mm a").format(ideaCalendar.time)
        else if (ideaCalendar.get(Calendar.DAY_OF_YEAR) - currentCalendar.get(Calendar.DAY_OF_YEAR) == 1)
            "Tomorrow, " + SimpleDateFormat("Tomorrow, h:mm a").format(ideaCalendar.time)
        else if (currentCalendar.get(Calendar.DAY_OF_YEAR) - ideaCalendar.get(Calendar.DAY_OF_YEAR) == 1)
            "Yesterday, " + SimpleDateFormat("h:mm a").format(ideaCalendar.time)
        else if (ideaCalendar.get(Calendar.WEEK_OF_YEAR) == currentCalendar.get(Calendar.WEEK_OF_YEAR))
            "This week, " + SimpleDateFormat("EEEE, h:mm a").format(ideaCalendar.time)
        else
            SimpleDateFormat("MMMM d, h:mm a").format(ideaCalendar.time)

    }

}