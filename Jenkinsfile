pipeline {
   agent { label "java"}
   stages {
      stage('Unit test') {
          steps {
              withEnv(['GRADLE_HOME=/var/jenkins_home/tools/gradle', 'GRADLE_OPTS="-Dorg.gradle.daemon=true -Xmx1024m -Xms512m -XX:MaxPermSize=2048m"', 'ANDROID_HOME=/var/jenkins_home/tools/android-sdk']) {
                  sh './gradlew clean testDebug'
              }
          }
          post {
            always {
                junit(allowEmptyResults: true, testResults: '**/*.xml')
            }
          }
      }
      stage('Code coverage') {
          steps {
            withEnv(['GRADLE_HOME=/var/jenkins_home/tools/gradle', 'GRADLE_OPTS="-Dorg.gradle.daemon=true -Xmx1024m -Xms512m -XX:MaxPermSize=2048m"', 'ANDROID_HOME=/var/jenkins_home/tools/android-sdk']) {
                  sh './gradlew clean sonarComplete'
            }
          }
          post {
             success {
                androidLint canComputeNew: false, defaultEncoding: '', healthy: '', pattern: '**/build/reports/lint-results*.xml', unHealthy: ''
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'app/build/reports/jacoco/jacocoTestDebugUnitTestReport/html', reportFiles: 'index.html', reportName: 'Code coverage', reportTitles: 'Code coverage'])
             }
          }
      }
      stage('Package') {
          steps {
            withEnv(['GRADLE_HOME=/var/jenkins_home/tools/gradle', 'GRADLE_OPTS="-Dorg.gradle.daemon=true -Xmx1024m -Xms512m -XX:MaxPermSize=2048m"', 'ANDROID_HOME=/var/jenkins_home/tools/android-sdk']) {
                  sh './bundle-db.sh'
                  sh './gradlew clean assembleDebug'
            }
          }
          post {
              success {
                  archive includes:'**/*.apk'
              }
          }
      }
   }
}
