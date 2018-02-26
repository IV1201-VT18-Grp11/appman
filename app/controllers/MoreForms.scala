package controllers

import database.{HasId, Id}
import play.api.data.Mapping

/**
  * Form mappings for custom types.
  */
object MoreForms {

  /**
    * Allows [[database.Id]]s to be treated as their underlying types by the form system.
    */
  def id[A <: HasId](inner: Mapping[A#IdType]): Mapping[Id[A]] =
    inner.transform(Id[A](_), _.raw)
}
