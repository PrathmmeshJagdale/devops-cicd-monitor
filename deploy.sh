#!/bin/bash
set -e

echo "========================================"
echo "   DevOps CI/CD Monitor - Deploy Script  "
echo "========================================"

echo ""
echo "Step 1: Creating AWS Infrastructure with Terraform..."
cd terraform
terraform init
terraform apply -auto-approve
cd ..

echo ""
echo "Step 2: Connecting kubectl to EKS cluster..."
aws eks update-kubeconfig \
  --name devops-cicd-cluster \
  --region us-east-2

echo "Waiting for nodes to be ready..."
kubectl wait --for=condition=ready node \
  --all \
  --timeout=300s

echo ""
echo "Step 3: Deploying application with Helm..."
kubectl create namespace cicd-monitor --dry-run=client -o yaml | kubectl apply -f -

helm upgrade --install cicd-monitor helm/cicd-monitor \
  --namespace cicd-monitor \
  --wait \
  --timeout 10m

echo ""
echo "Step 4: Installing Prometheus + Grafana + AlertManager..."
kubectl create namespace monitoring --dry-run=client -o yaml | kubectl apply -f -

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm upgrade --install monitoring prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --set grafana.adminPassword=admin123 \
  --set prometheus.prometheusSpec.retention=7d \
  --wait \
  --timeout 10m

echo ""
echo "Step 5: Installing ArgoCD..."
kubectl create namespace argocd --dry-run=client -o yaml | kubectl apply -f -

kubectl apply -n argocd \
  -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml \
  --server-side \
  --force-conflicts

echo "Waiting for ArgoCD pods to be ready..."
kubectl wait --for=condition=ready pod \
  --all \
  -n argocd \
  --timeout=300s

kubectl apply -f argocd/argocd-app.yaml

echo ""
echo "========================================"
echo "   DEPLOYMENT COMPLETE!                  "
echo "========================================"
echo ""
echo "Getting Load Balancer URL..."
kubectl get svc -n ingress-nginx
echo ""
echo "Access your dashboard at the EXTERNAL-IP shown above"
echo ""
echo "Grafana Dashboard:"
echo "  Run: kubectl port-forward svc/monitoring-grafana -n monitoring 3000:80"
echo "  Open: http://localhost:3000"
echo "  Username: admin"
echo "  Password: admin123"
echo ""
echo "ArgoCD Dashboard:"
echo "  Run: kubectl port-forward svc/argocd-server -n argocd 9000:443"
echo "  Open: https://localhost:9000"
echo "  Username: admin"
