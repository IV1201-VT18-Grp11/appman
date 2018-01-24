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
                sh "sbt compile test:compile stage"
                archiveArtifacts artifacts: "target/scala-*/*.jar", fingerprint: true
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
