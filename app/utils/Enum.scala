package utils

trait Enum[A] {
  def values: Seq[A]
  final def valueMap: Map[String, A] =
    values.map(a => a.toString() -> a).toMap
}
