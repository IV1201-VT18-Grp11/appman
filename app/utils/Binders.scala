package utils

import database.{HasId, Id}
import play.api.mvc.PathBindable

object Binders {
  implicit def idPathBindable[A <: HasId](
    implicit inner: PathBindable[A#IdType]
  ): PathBindable[Id[A]] = inner.transform(Id(_), _.raw)
}
