package views

import java.time.{Instant, ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter

/**
  * Helpers for formatting dates and times.
  */
object DateFormat {
  def formatInstant(instant: Instant): String =
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm O")
      .format(ZonedDateTime.ofInstant(instant, ZoneId.of("Europe/Stockholm")))
}
