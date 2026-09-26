pipeline {
    agent { label 'linux-agent' }

    environment {
        KUBERNETES_CICD_TOKEN = credentials('KUBERNETES_CICD_TOKEN')
    }

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
                        sh "cp \"$ENV_FILE\" vaultservice.env"
//                         bat "copy %ENV_FILE% .env" // For windows runs
                    }
                }
            }
        }

        stage("Test") {
            steps {
                sh "./mvnw clean test"
            }
        }

        stage("Deploy & Health Check") {
            steps {
                script {
                    def kubernetesEnabled = env.KUBERNETES_ENABLED == "true"

                    if(kubernetesEnabled) {
                        def imageRepository = env.SERVICE_IMAGE_REPOSITORY
                        def kubernetesServiceUrl = env.KUBERNETESERVICE_URL

                        if(imageRepository.isEmpty() || kubernetesServiceUrl.isEmpty())
                            error("VaultService Kubernetes deployment configuration is incomplete")

                        def image = imageRepository + ":" + env.BUILD_NUMBER

                        withEnv(["SERVICE_IMAGE=" + image, "KUBERNETESERVICE_URL=" + kubernetesServiceUrl]) {
                            sh "docker build -t " + image + " ."
                            sh "docker push " + image
                            sh '''
                                curl --fail-with-body -X POST \\
                                    -H "X-CICD-TOKEN: $KUBERNETES_CICD_TOKEN" \\
                                    -F "serviceName=vaultservice" \\
                                    -F "image=$SERVICE_IMAGE" \\
                                    -F "environmentFile=@vaultservice.env" \\
                                    "$KUBERNETESERVICE_URL/api/kubernetes/deploy"
                            '''
                        }
                        return
                    }

                    try {
                        sh "docker compose --env-file vaultservice.env up --build -d"
//                         bat "docker compose --env-file vaultservice.env up --build -d" // For windows runs

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
                            sh "docker compose down"
//                             bat "docker compose down" // For windows runs
                            error("Deployment failed: service not healthy.")
                        }

                    } catch (ex) {
                        echo "Unexpected failure: ${ex.getMessage()}"
                        sh "docker compose down"
//                         bat "docker compose down" // For windows runs
                        error("Deployment crashed.")
                    }
                }
            }
        }
    }
    post {
        always {
            sh "rm -f vaultservice.env"
            echo "Cleaned up vaultservice.env"
        }
    }
}
