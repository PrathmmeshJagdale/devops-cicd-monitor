pipeline {
    agent any
    environment {
        AWS_REGION            = 'us-east-2'
        AWS_ACCOUNT_ID        = '219850556995'
        ECR_REGISTRY          = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        PROJECT_NAME          = 'devops-cicd'
        IMAGE_TAG             = "${BUILD_NUMBER}"
        AWS_ACCESS_KEY_ID     = credentials('aws-access-key')
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-key')
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'devops',
                    credentialsId: 'github-credentials',
                    url: 'https://github.com/PrathmmeshJagdale/devops-cicd-monitor.git'
            }
        }
        stage('Build Docker Images') {
            steps {
                sh """
                    docker build -t ${PROJECT_NAME}/auth-service:${IMAGE_TAG} ./auth-service
                    docker build -t ${PROJECT_NAME}/pipeline-service:${IMAGE_TAG} ./pipeline-service
                    docker build -t ${PROJECT_NAME}/alert-service:${IMAGE_TAG} ./alert-service
                    docker build -t ${PROJECT_NAME}/retry-service:${IMAGE_TAG} ./retry-service
                    docker build -t ${PROJECT_NAME}/report-service:${IMAGE_TAG} ./report-service
                    docker build -t ${PROJECT_NAME}/frontend:${IMAGE_TAG} ./frontend
                """
            }
        }
        stage('Tag Docker Images') {
            steps {
                sh """
                    docker tag ${PROJECT_NAME}/auth-service:${IMAGE_TAG} ${ECR_REGISTRY}/${PROJECT_NAME}/auth-service:${IMAGE_TAG}
                    docker tag ${PROJECT_NAME}/auth-service:${IMAGE_TAG} ${ECR_REGISTRY}/${PROJECT_NAME}/auth-service:latest

                    docker tag ${PROJECT_NAME}/pipeline-service:${IMAGE_TAG} ${ECR_REGISTRY}/${PROJECT_NAME}/pipeline-service:${IMAGE_TAG}
                    docker tag ${PROJECT_NAME}/pipeline-service:${IMAGE_TAG} ${ECR_REGISTRY}/${PROJECT_NAME}/pipeline-service:latest

                    docker tag ${PROJECT_NAME}/alert-service:${IMAGE_TAG} ${ECR_REGISTRY}/${PROJECT_NAME}/alert-service:${IMAGE_TAG}
                    docker tag ${PROJECT_NAME}/alert-service:${IMAGE_TAG} ${ECR_REGISTRY}/${PROJECT_NAME}/alert-service:latest

                    docker tag ${PROJECT_NAME}/retry-service:${IMAGE_TAG} ${ECR_REGISTRY}/${PROJECT_NAME}/retry-service:${IMAGE_TAG}
                    docker tag ${PROJECT_NAME}/retry-service:${IMAGE_TAG} ${ECR_REGISTRY}/${PROJECT_NAME}/retry-service:latest

                    docker tag ${PROJECT_NAME}/report-service:${IMAGE_TAG} ${ECR_REGISTRY}/${PROJECT_NAME}/report-service:${IMAGE_TAG}
                    docker tag ${PROJECT_NAME}/report-service:${IMAGE_TAG} ${ECR_REGISTRY}/${PROJECT_NAME}/report-service:latest

                    docker tag ${PROJECT_NAME}/frontend:${IMAGE_TAG} ${ECR_REGISTRY}/${PROJECT_NAME}/frontend:${IMAGE_TAG}
                    docker tag ${PROJECT_NAME}/frontend:${IMAGE_TAG} ${ECR_REGISTRY}/${PROJECT_NAME}/frontend:latest
                """
            }
        }
        stage('Push to ECR') {
            steps {
                sh """
                    aws ecr get-login-password --region ${AWS_REGION} | \
                    docker login --username AWS --password-stdin ${ECR_REGISTRY}

                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/auth-service:${IMAGE_TAG}
                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/auth-service:latest

                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/pipeline-service:${IMAGE_TAG}
                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/pipeline-service:latest

                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/alert-service:${IMAGE_TAG}
                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/alert-service:latest

                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/retry-service:${IMAGE_TAG}
                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/retry-service:latest

                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/report-service:${IMAGE_TAG}
                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/report-service:latest

                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/frontend:${IMAGE_TAG}
                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/frontend:latest
                """
            }
        }
        stage('Cleanup Local Images') {
            steps {
                sh """
                    docker rmi ${PROJECT_NAME}/auth-service:${IMAGE_TAG} || true
                    docker rmi ${PROJECT_NAME}/pipeline-service:${IMAGE_TAG} || true
                    docker rmi ${PROJECT_NAME}/alert-service:${IMAGE_TAG} || true
                    docker rmi ${PROJECT_NAME}/retry-service:${IMAGE_TAG} || true
                    docker rmi ${PROJECT_NAME}/report-service:${IMAGE_TAG} || true
                    docker rmi ${PROJECT_NAME}/frontend:${IMAGE_TAG} || true

                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/auth-service:${IMAGE_TAG} || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/auth-service:latest || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/pipeline-service:${IMAGE_TAG} || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/pipeline-service:latest || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/alert-service:${IMAGE_TAG} || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/alert-service:latest || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/retry-service:${IMAGE_TAG} || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/retry-service:latest || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/report-service:${IMAGE_TAG} || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/report-service:latest || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/frontend:${IMAGE_TAG} || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/frontend:latest || true
                """
            }
        }
    }
    post {
        success {
            echo 'Pipeline completed successfully - images pushed to ECR with build number and latest tags'
        }
        failure {
            echo 'Pipeline failed - check logs above'
        }
        always {
            cleanWs()
        }
    }
}
        
                    
