stage 'Unit test'
    node('android') {
        checkout scm
        withEnv(['GRADLE_HOME=/var/jenkins_home/tools/gradle', 'GRADLE_OPTS="-Dorg.gradle.daemon=true -Xmx1024m -Xms512m -XX:MaxPermSize=2048m"', 'ANDROID_HOME=/var/jenkins_home/tools/android-sdk']) {
           try {
               sh '$GRADLE_HOME/bin/gradle clean testDebug'
           } finally {
               step([$class: 'JUnitResultArchiver', testResults: '**/*.xml'])
           }
        }
    }

//stage 'Code coverage'
  //  node('android') {
    //    checkout scm
    //    withEnv(['GRADLE_HOME=/var/jenkins_home/tools/gradle', 'GRADLE_OPTS="-Dorg.gradle.daemon=true -Xmx1024m -Xms512m -XX:MaxPermSize=2048m"', 'ANDROID_HOME=/var/jenkins_home/tools/android-sdk']) {
   //        try {
   //            sh '$GRADLE_HOME/bin/gradle clean sonarComplete'
   //        } finally {
   //            androidLint canComputeNew: false, defaultEncoding: '', healthy: '', pattern: '**/build/outputs/lint-results*.xml', unHealthy: ''
   //            publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'app/build/reports/jacoco/jacocoTestReport/html', reportFiles: 'index.html', reportName: 'Code coverage'])
   //        }
   //     }
   // }