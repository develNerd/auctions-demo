package org.auctions.klaravik.view.data


import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.auctions.klaravik.data.model.Image
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


data class ProductItem(
    val categoryLevel1: Int? = null,
    val categoryLevel2: Int? = null,
    val categoryLevel3: Int? = null,
    val currentBid: Long = 0,
    val description: String? = null,
    val endDate: String? = null,
    val id: Int = 0,
    val image: Image? = null,
    val make: String? = null,
    val model: String? = null,
    val municipalityName: String? = null,
    val name: String? = null,
    val preamble: String? = null,
    val reservePriceStatus: String? = null,
    var isFavorite: Boolean = false
) {

    // Simulate end date
    fun getAuctionCountdownString(): String {
        if (endDate == null) return "Ends in 0 days"

        val (dateTimePart, offsetPart) = endDate.split("+")
        val cleanedDate = dateTimePart.trim() // -> "2023-10-01T12:00:00"
        val hoursOffset = offsetPart.substringBefore(":").toIntOrNull() ?: 0 //  ->  "02:00" -> 2

        val timeZone = TimeZone.of("UTC+$hoursOffset")
        val auctionEnd = LocalDateTime.parse(cleanedDate)
        val auctionEndInstant = auctionEnd.toInstant(timeZone)

        val nowInstant = Clock.System.now()
        val remainingDuration = auctionEndInstant - nowInstant

        val isNegative = remainingDuration.isNegative()
        val duration = if (isNegative) -remainingDuration else remainingDuration

        return if (duration >= 1.days) {
            val days = duration.inWholeDays
            val hours = (duration - days.days).inWholeHours
            val prefix = if (isNegative) "Auction Ended " else "Auction closes in"
            val suffix = if (isNegative) "" else " ago"
            "$prefix$days day(s) $hours hour(s)$suffix"
        } else {
            val hours = duration.inWholeHours
            val minutes = (duration - hours.hours).inWholeMinutes
            val seconds = (duration - hours.hours - minutes.minutes).inWholeSeconds
            val prefix = if (isNegative) "Auction Ended " else "Auction closes in"
            val suffix = if (isNegative) "" else " ago"
            "$prefix${hours.toInt()}:${minutes.toInt()}:${seconds.toInt()}$suffix"
        }
    }

}
