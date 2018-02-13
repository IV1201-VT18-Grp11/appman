package controllers

import database.{HasId, Id}
import play.api.data.Mapping

object MoreForms {
  def id[A <: HasId](inner: Mapping[A#IdType]): Mapping[Id[A]] =
    inner.transform(Id[A](_), _.raw)
}
