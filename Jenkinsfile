pipeline {
   agent { label "java11"}

   tools{
        gradle 'Gradle-7'
   }

   environment{
        GRADLE_OPTS='-Dorg.gradle.daemon=true -Xmx1024m -Xms512m -XX:MaxPermSize=2048m'
   }

   stages {
      stage('Unit test') {
          steps {
            sh './gradlew clean testDebug'
          }
          post {
            always {
                junit(allowEmptyResults: true, testResults: '**/*.xml')
            }
          }
      }
   //   stage('Code coverage') {
   //       steps {
   //           sh './gradlew clean sonarComplete'
   //       }
   //       post {
   //          success {
   //             androidLint canComputeNew: false, defaultEncoding: '', healthy: '', pattern: '**/build/reports/lint-results*.xml', unHealthy: ''
   //             publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'app/build/reports/jacoco/jacocoTestDebugUnitTestReport/html', reportFiles: 'index.html', reportName: 'Code coverage', reportTitles: 'Code coverage'])
   //          }
   //       }
   //   }
      stage('Package') {
          steps {
            sh './bundle-db.sh'
            sh './gradlew clean assembleDebug'
          }
          post {
              success {
                  archive includes:'**/*.apk'
              }
          }
      }
   }
}
