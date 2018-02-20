package views

import java.time.Instant

object DateFormat {
  def formatInstant(instant: Instant): String =
    java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(
      java.time.ZonedDateTime
        .ofInstant(instant, java.time.ZoneId.of("Europe/Stockholm"))
    )
}
