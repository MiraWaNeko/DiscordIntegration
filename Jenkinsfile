pipeline {
  agent any
  options {
    timeout(time: 30, unit: 'MINUTES')
  }
  tools {
    jdk 'jdk_8u144'
    nodejs 'node_8.4.0'
  }
  stages {
    stage('Prepare') {
      steps {
        sh 'git submodule update --init'
        sh 'chmod +x gradlew'
        sh './gradlew setupCiWorkspace clean spotlessApply'
      }
    }
    stage('Build') {
      steps {
        sh './gradlew build jar'
      }
    }
    stage('Test') {
      steps {
        sh './gradlew test'
      }
      post {
        always {
          junit 'build/test-results/TEST-*.xml'
        }
      }
    }
    stage('Run Server Test') {
      steps {
        sh 'mkdir -p run/config/Chikachi'
        sh 'mkdir -p run/mods'
        writeFile file: 'run/eula.txt', text: 'eula=true'
        withCredentials([file(credentialsId: 'discordintegration.test.config', variable: 'CONFIG_FILE')]) {
          sh 'cp "$CONFIG_FILE" ./run/config/Chikachi/'
          dir('serverTest') {
            sh 'npm update'
            sh 'tsc'
            sh 'npm start'
          }
        }
      }
      post {
        always {
          archiveArtifacts 'run/logs/fml-server-latest.log'
          cleanWs deleteDirs: true, notFailBuild: true, patterns: [[pattern: 'run/config/**', type: 'INCLUDE'], [pattern: 'run/mods/**', type: 'INCLUDE']]
        }
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts 'build/libs/*.jar'
        fingerprint 'build/libs/*.jar'
      }
    }
  }
}