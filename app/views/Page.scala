package views

sealed trait Page
object Page {
  case object Home extends Page
  case object Login extends Page
  case object Register extends Page
  case object ApplicationList extends Page
}
