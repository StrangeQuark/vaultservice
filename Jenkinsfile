pipeline {
    agent { label 'Host PC' }

    stages {
        // Because the vault service cannot request secrets from itself upon start up without extensive initialization
        // need from the user, it is required to establish these credentials in Jenkins before start up
        //
        // To do this:
        // - Navigate to localhost:6080/manage/credentials
        // - Click (global) -> Add credentials
        // - Kind: Secret file
        // - Scope: Global
        // - Choose your .env file
        // - ID: vault-env
        stage("Retrieve Env Vars") {
            steps {
                withCredentials([file(credentialsId: 'vault-env', variable: 'ENV_FILE')]) {
                    script {
                        sh "cp \"$ENV_FILE\" .env"
//                         bat "copy %ENV_FILE% .env" // For windows runs
                    }
                }
            }
        }

        stage("Deploy & Health Check") {
            steps {
                script {
                    try {
                        sh "docker-compose up --build -d"
//                         bat "docker-compose up --build -d" // For windows runs

                        def maxRetries = 4 * 10
                        def retryInterval = 15
                        def success = false

                        for (int i = 0; i < maxRetries; i++) {
                            try {
                                echo "Health check attempt ${i + 1}..."
                                def healthResponse = httpRequest(
                                    url: 'http://localhost:6020/api/vault/health',
                                    validResponseCodes: '200'
                                )
                                echo "App is healthy: ${healthResponse.status}"
                                success = true
                                break
                            } catch (err) {
                                echo "Health check failed, retrying in ${retryInterval} seconds..."
                                sleep(retryInterval)
                            }
                        }

                        if (!success) {
                            echo "Health check ultimately failed. Tearing down containers."
                            sh "docker-compose down"
//                             bat "docker-compose down" // For windows runs
                            error("Deployment failed: service not healthy.")
                        }

                    } catch (ex) {
                        echo "Unexpected failure: ${ex.getMessage()}"
                        sh "docker-compose down"
//                         bat "docker-compose down" // For windows runs
                        error("Deployment crashed.")
                    }
                }
            }
        }
    }
}