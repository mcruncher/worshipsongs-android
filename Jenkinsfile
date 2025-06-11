pipeline {
  agent {
    kubernetes {
      defaultContainer 'gradle'
      yamlFile 'kubernetes/gradle-pod.yaml'
    }
  }

  environment {
    GRADLE_OPTS='-Dorg.gradle.daemon=true -Xmx1024m -Xms512m -XX:MaxPermSize=2048m'
    WORSHIPSONGS_KEYSTORE_PASSWORD = credentials('worshipsongs-android-keystore-pwd')
    WORSHIPSONGS_KEY_ALIAS = credentials('worshipsongs-android-key-alias')
    WORSHIPSONGS_KEY_PASSWORD = credentials('worshipsongs-android-key-pwd')
  }

  stages {
    stage('Unit Test') {
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

    stage ('Package and Publish') {
      when {
        anyOf {
          branch 'master'; branch 'release/*'; branch 'hotfix/*'
        }
      }

      steps {
        sh './bundle-db.sh'
        sh './gradlew clean assembleRelease'
      }

      post {
        always {
          archive includes:'**/*.apk'
        }

        success {
          script {
            env.WORKSPACE = pwd()
            env.FILENAME = readFile "${env.WORKSPACE}/changelist"
          }

          androidApkUpload apkFilesPattern: '**/*.apk', googleCredentialsId: "$GOOGLE_DEVELOPER_CREDENTIALS_ID", recentChangeList: [[language: 'en-GB', text: "${env.FILENAME}"]], trackName: 'beta', rolloutPercentage: '0%'
        }
      }
    }

  }
}
