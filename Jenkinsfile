#!/usr/bin/env groovy

pipeline {
    agent any

    stages {
        stage('Downloading dependencies') {
            steps {
                sh "sbt update web-assets:webNodeModules"
            }
        }

        stage('Compiling') {
            steps {
                sh "sbt compile test:compile stage"
                archiveArtifacts artifacts: "target/scala-*/*.jar", fingerprint: true
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'target/scala-2.12/api', reportFiles: 'index.html', reportName: 'Scaladoc', reportTitles: ''])
            }
        }

        stage('Building assets') {
            steps {
                sh "sbt webStage"
            }
        }

        stage('Running tests') {
            steps {
                sh "xvfb-run sbt coverage test coverageReport || true"
            }
        }
    }

    post {
        always {
            junit "target/test-report/**/*.xml"
            step([$class: 'ScoveragePublisher', reportDir: 'target/scala-2.12/scoverage-report', reportFile: 'scoverage.xml'])
        }
        failure {
            emailextrecipients([[$class: 'FirstFailingBuildSuspectsRecipientProvider']])
        }
        unstable {
            emailextrecipients([[$class: 'FailingTestSuspectsRecipientProvider']])
        }
    }
}
