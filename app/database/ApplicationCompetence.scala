package database

import PgProfile.api._

/**
  * A skill that an applicant has.
  */
case class ApplicationCompetence(competence: Id[Competence],
                                 yearsOfExperience: Float,
                                 application: Id[JobApplication])

class ApplicationCompetences(tag: Tag)
    extends Table[ApplicationCompetence](tag, "application_competences") {
  def competenceId      = column[Id[Competence]]("competence")
  def yearsOfExperience = column[Float]("years_of_experience")
  def applicationId     = column[Id[JobApplication]]("application")

  def competence = foreignKey("competence_fk", competenceId, Competences)(_.id)
  def application =
    foreignKey("application_fk", applicationId, JobApplications)(_.id)

  override def * =
    (competenceId, yearsOfExperience, applicationId) <> (ApplicationCompetence.tupled, ApplicationCompetence.unapply)
}

object ApplicationCompetences
    extends TableQuery[ApplicationCompetences](new ApplicationCompetences(_))
