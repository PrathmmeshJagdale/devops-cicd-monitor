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
                echo '========== Checking out code =========='
                git branch: 'main',
                    credentialsId: 'github-credentials',
                    url: 'https://github.com/PrathmmeshJagdale/devops-cicd-monitor.git'
            }
        }

        stage('Build Docker Images') {
            steps {
                echo '========== Building Docker Images =========='
                sh '''
                    docker build -t auth-service:${BUILD_NUMBER} ./auth-service
                    docker build -t pipeline-service:${BUILD_NUMBER} ./pipeline-service
                    docker build -t alert-service:${BUILD_NUMBER} ./alert-service
                    docker build -t retry-service:${BUILD_NUMBER} ./retry-service
                    docker build -t report-service:${BUILD_NUMBER} ./report-service
                    docker build -t frontend:${BUILD_NUMBER} ./frontend
                '''
            }
        }

        stage('Tag Docker Images') {
            steps {
                echo '========== Tagging Images for ECR =========='
                sh '''
                    docker tag auth-service:${BUILD_NUMBER} ${ECR_REGISTRY}/${PROJECT_NAME}/auth-service:${BUILD_NUMBER}
                    docker tag pipeline-service:${BUILD_NUMBER} ${ECR_REGISTRY}/${PROJECT_NAME}/pipeline-service:${BUILD_NUMBER}
                    docker tag alert-service:${BUILD_NUMBER} ${ECR_REGISTRY}/${PROJECT_NAME}/alert-service:${BUILD_NUMBER}
                    docker tag retry-service:${BUILD_NUMBER} ${ECR_REGISTRY}/${PROJECT_NAME}/retry-service:${BUILD_NUMBER}
                    docker tag report-service:${BUILD_NUMBER} ${ECR_REGISTRY}/${PROJECT_NAME}/report-service:${BUILD_NUMBER}
                    docker tag frontend:${BUILD_NUMBER} ${ECR_REGISTRY}/${PROJECT_NAME}/frontend:${BUILD_NUMBER}
                '''
            }
        }

        stage('Push to ECR') {
            steps {
                echo '========== Logging into AWS ECR =========='
                sh '''
                    aws ecr get-login-password --region ${AWS_REGION} | \
                    docker login --username AWS --password-stdin ${ECR_REGISTRY}

                    echo '========== Pushing Images to ECR =========='
                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/auth-service:${BUILD_NUMBER}
                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/pipeline-service:${BUILD_NUMBER}
                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/alert-service:${BUILD_NUMBER}
                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/retry-service:${BUILD_NUMBER}
                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/report-service:${BUILD_NUMBER}
                    docker push ${ECR_REGISTRY}/${PROJECT_NAME}/frontend:${BUILD_NUMBER}
                '''
            }
        }

        stage('Cleanup Local Images') {
            steps {
                echo '========== Cleaning up local Docker images =========='
                sh '''
                    docker rmi auth-service:${BUILD_NUMBER} || true
                    docker rmi pipeline-service:${BUILD_NUMBER} || true
                    docker rmi alert-service:${BUILD_NUMBER} || true
                    docker rmi retry-service:${BUILD_NUMBER} || true
                    docker rmi report-service:${BUILD_NUMBER} || true
                    docker rmi frontend:${BUILD_NUMBER} || true

                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/auth-service:${BUILD_NUMBER} || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/pipeline-service:${BUILD_NUMBER} || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/alert-service:${BUILD_NUMBER} || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/retry-service:${BUILD_NUMBER} || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/report-service:${BUILD_NUMBER} || true
                    docker rmi ${ECR_REGISTRY}/${PROJECT_NAME}/frontend:${BUILD_NUMBER} || true
                '''
            }
        }
    }

    post {
        success {
            echo "========== Pipeline SUCCESS — Images pushed with tag: ${BUILD_NUMBER} =========="
        }
        failure {
            echo '========== Pipeline FAILED — Check logs above =========='
        }
        always {
            cleanWs()
        }
    }
}
