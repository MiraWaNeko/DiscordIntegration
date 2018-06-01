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
        withCredentials([file(credentialsId: 'discordintegration.test.config', variable: 'CONFIG_FILE')]) {
          sh 'mkdir -p run/config/Chikachi'
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
          archiveArtifacts 'run/logs/*latest.log'
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
  post {
    success {
      withCredentials([string(credentialsId: 'discord.webhook.channel', variable: 'WEBHOOK_CHANNEL'), string(credentialsId: 'discord.webhook.token', variable: 'WEBHOOK_TOKEN')]) {
        sh "curl -X POST --data '{ \"embeds\": [{\"title\":\"DiscordIntegration\",\"type\":\"link\",\"description\":\"Build $BRANCH_NAME $BUILD_DISPLAY_NAME\",\"url\":\"$BUILD_URL\",\"color\":709124,\"fields\":[{\"name\":\"Status\",\"value\":\"$currentBuild.currentResult\"}]}] }' -H \"Content-Type: application/json\"  https://discordapp.com/api/webhooks/$WEBHOOK_CHANNEL/$WEBHOOK_TOKEN"
      }
    }
    unstable {
      withCredentials([string(credentialsId: 'discord.webhook.channel', variable: 'WEBHOOK_CHANNEL'), string(credentialsId: 'discord.webhook.token', variable: 'WEBHOOK_TOKEN')]) {
        sh "curl -X POST --data '{ \"embeds\": [{\"title\":\"DiscordIntegration\",\"type\":\"link\",\"description\":\"Build $BRANCH_NAME $BUILD_DISPLAY_NAME\",\"url\":\"$BUILD_URL\",\"color\":15989262,\"fields\":[{\"name\":\"Status\",\"value\":\"$currentBuild.currentResult\"}]}] }' -H \"Content-Type: application/json\"  https://discordapp.com/api/webhooks/$WEBHOOK_CHANNEL/$WEBHOOK_TOKEN"
      }
    }
    failure {
      withCredentials([string(credentialsId: 'discord.webhook.channel', variable: 'WEBHOOK_CHANNEL'), string(credentialsId: 'discord.webhook.token', variable: 'WEBHOOK_TOKEN')]) {
        sh "curl -X POST --data '{ \"embeds\": [{\"title\":\"DiscordIntegration\",\"type\":\"link\",\"description\":\"Build $BRANCH_NAME $BUILD_DISPLAY_NAME\",\"url\":\"$BUILD_URL\",\"color\":13698309,\"fields\":[{\"name\":\"Status\",\"value\":\"$currentBuild.currentResult\"}]}] }' -H \"Content-Type: application/json\"  https://discordapp.com/api/webhooks/$WEBHOOK_CHANNEL/$WEBHOOK_TOKEN"
      }
    }
    aborted {
      withCredentials([string(credentialsId: 'discord.webhook.channel', variable: 'WEBHOOK_CHANNEL'), string(credentialsId: 'discord.webhook.token', variable: 'WEBHOOK_TOKEN')]) {
        sh "curl -X POST --data '{ \"embeds\": [{\"title\":\"DiscordIntegration\",\"type\":\"link\",\"description\":\"Build $BRANCH_NAME $BUILD_DISPLAY_NAME\",\"url\":\"$BUILD_URL\",\"fields\":[{\"name\":\"Status\",\"value\":\"$currentBuild.currentResult\"}]}] }' -H \"Content-Type: application/json\"  https://discordapp.com/api/webhooks/$WEBHOOK_CHANNEL/$WEBHOOK_TOKEN"
      }
    }
  }
}