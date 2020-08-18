pipeline {
    agent any
    stages {
        /*stage('Clone') {
            steps {
                script {
                 def ws = pwd()+"/"+"AclaimUiAuto"
                 echo ws
                dir('AclaimUiAuto') {
                         deleteDir()
                    }
                }
                //bat "cmd  /c 'del /s /q Test-Pipeline'"
                bat "git clone https://gitlab.wuintranet.net/complainceqa/AclaimUiAuto.git"
                bat "cd AclaimUiAuto && git checkout api_integration"

            }
        }*/

        stage('Build') {
            steps {
              //bat "cd AclaimUiAuto && gradle clean build"
               bat "gradle clean build"

            }
        }

        stage('Test') {
            steps {
            //bat "cd AclaimUiAuto && gradle run -PisAPIOnly=$isAPIOnly -PupdateJira=$updateJira -PaclaimInstalledLocation=$aclaimInstalledLocation -Ptest_filters=$test_filters"
            bat "gradle run -PisAPIOnly=$isAPIOnly -PupdateJira=$updateJira -PaclaimInstalledLocation=$aclaimInstalledLocation -Ptest_filters=$test_filters"

            }
        }
        stage('reports') {
            steps {
               script {
                  allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
                  ])
                }
             }
         }

    }
}