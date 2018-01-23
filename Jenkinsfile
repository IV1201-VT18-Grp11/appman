#!/usr/bin/env groovy

pipeline {
    agent any

    stages {
        stage('Downloading dependencies') {
            steps {
                sh "sbt update"
            }
        }

        stage('Compiling') {
            steps {
                sh "sbt compile"
            }
        }

        stage('Building assets') {
            steps {
                sh "sbt stageWeb"
            }
        }

        stage('Running tests') {
            steps {
                sh "sbt test"
            }
        }
    }
}
