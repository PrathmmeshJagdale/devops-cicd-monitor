#!/bin/bash
set -e

echo "========================================"
echo "   DevOps CI/CD Monitor - Destroy Script "
echo "========================================"

echo ""
echo "Step 1: Connecting kubectl to EKS cluster..."
aws eks update-kubeconfig \
  --name devops-cicd-cluster \
  --region us-east-2

echo ""
echo "Step 2: Deleting Kubernetes namespaces..."
kubectl delete namespace cicd-monitor --ignore-not-found
kubectl delete namespace monitoring --ignore-not-found
kubectl delete namespace argocd --ignore-not-found
kubectl delete namespace ingress-nginx --ignore-not-found

echo ""
echo "Step 3: Waiting for Load Balancer to be deleted by AWS..."
echo "Sleeping 90 seconds for ENIs and Load Balancer to release..."
sleep 90

echo ""
echo "Step 4: Deleting ELB security group if exists..."
VPC_ID=$(aws ec2 describe-vpcs \
  --filters "Name=tag:Name,Values=devops-cicd-vpc" \
  --region us-east-2 \
  --query 'Vpcs[0].VpcId' \
  --output text)

if [ "$VPC_ID" != "None" ] && [ "$VPC_ID" != "" ]; then
  SG_ID=$(aws ec2 describe-security-groups \
    --filters "Name=vpc-id,Values=$VPC_ID" \
              "Name=group-name,Values=k8s-elb-*" \
    --region us-east-2 \
    --query 'SecurityGroups[0].GroupId' \
    --output text)

  if [ "$SG_ID" != "None" ] && [ "$SG_ID" != "" ]; then
    echo "Deleting ELB security group $SG_ID..."
    aws ec2 delete-security-group \
      --group-id $SG_ID \
      --region us-east-2
  fi
fi

echo ""
echo "Step 5: Destroying Terraform infrastructure..."
cd terraform
terraform destroy -auto-approve
cd ..

echo ""
echo "========================================"
echo "   DESTROY COMPLETE!                     "
echo "========================================"
echo ""
echo "Remember to STOP Jenkins EC2 to save costs!"
echo "AWS Console → EC2 → Jenkins → Stop"
