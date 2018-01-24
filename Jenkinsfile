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
                sh "sbt webStage"
            }
        }

        stage('Running tests') {
            steps {
                sh "xvfb-run sbt test"
            }
        }
    }

    post {
        always {
            junit "target/test-report/**/*.xml"
        }
    }
}
