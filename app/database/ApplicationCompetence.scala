package database

import PgProfile.api._

case class ApplicationCompetence(
                                competence: Id[Competence],
                                yearsOfExperience: Float,
                                application: Id[JobApplication]
                                )


class ApplicationCompetences(tag: Tag) extends Table[ApplicationCompetence](tag, "competences") {
  def competenceId   = column[Id[Competence]]("competence")
  def yearsOfExperience    = column[Float]("years of experience")
  def applicationId = column[Id[JobApplication]]("application")

  def competence = foreignKey("competence_fk", competenceId, Competences)(_.id)
  def application = foreignKey("application_fk", applicationId, JobApplications) (_.id)

  override def * =
    (competenceId, yearsOfExperience, applicationId) <> (ApplicationCompetence.tupled, ApplicationCompetence.unapply)
}

object ApplicationCompetences extends TableQuery[ApplicationCompetences](new ApplicationCompetences(_))
