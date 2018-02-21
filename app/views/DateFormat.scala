package views

import java.time.{Instant, ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter

object DateFormat {
  def formatInstant(instant: Instant): String =
    DateTimeFormatter.RFC_1123_DATE_TIME.format(
      ZonedDateTime
        .ofInstant(instant, ZoneId.of("Europe/Stockholm"))
    )
}
