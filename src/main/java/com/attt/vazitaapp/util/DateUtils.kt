package com.attt.vazitaapp.util


import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for date and time formatting operations
 */
object DateUtils {
    private const val DATE_FORMAT = "yyyy-MM-dd"
    private const val TIME_FORMAT = "HH:mm:ss"
    private const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val DISPLAY_DATE_FORMAT = "dd/MM/yyyy"
    private const val DISPLAY_DATETIME_FORMAT = "dd/MM/yyyy HH:mm"

    private val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    private val timeFormatter = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
    private val dateTimeFormatter = SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault())
    private val displayDateFormatter = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
    private val displayDateTimeFormatter = SimpleDateFormat(DISPLAY_DATETIME_FORMAT, Locale.getDefault())

    /**
     * Format date to API format (yyyy-MM-dd)
     */
    fun formatDateForApi(date: Date): String {
        return dateFormatter.format(date)
    }

    /**
     * Format date and time to API format (yyyy-MM-dd HH:mm:ss)
     */
    fun formatDateTimeForApi(date: Date): String {
        return dateTimeFormatter.format(date)
    }

    /**
     * Format date for display (dd/MM/yyyy)
     */
    fun formatDateForDisplay(date: Date): String {
        return displayDateFormatter.format(date)
    }

    /**
     * Format date and time for display (dd/MM/yyyy HH:mm)
     */
    fun formatDateTimeForDisplay(date: Date): String {
        return displayDateTimeFormatter.format(date)
    }

    /**
     * Parse date from API format (yyyy-MM-dd)
     */
    fun parseDate(dateString: String): Date? {
        return try {
            dateFormatter.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parse date and time from API format (yyyy-MM-dd HH:mm:ss)
     */
    fun parseDateTime(dateTimeString: String): Date? {
        return try {
            dateTimeFormatter.parse(dateTimeString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get current date in API format
     */
    fun getCurrentDateForApi(): String {
        return formatDateForApi(Date())
    }

    /**
     * Get current date and time in API format
     */
    fun getCurrentDateTimeForApi(): String {
        return formatDateTimeForApi(Date())
    }
}