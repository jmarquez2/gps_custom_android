package com.jrms.gpsviewer.utils

import com.jrms.gpsviewer.data.DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun getFormatter(locale: Locale) : SimpleDateFormat{
    return SimpleDateFormat(DATE_FORMAT, locale)
}

fun parseDate(date : String, locale: Locale) : Date? {
    return getFormatter(locale).parse(date)
}


fun formatDate(date : java.util.Date, locale: Locale) : String{
    return getFormatter(locale).format(date)
}