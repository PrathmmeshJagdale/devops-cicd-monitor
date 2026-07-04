# 🚀 DevOps CI/CD Pipeline Monitor & Alerting System

![AWS](https://img.shields.io/badge/AWS-EKS%20%7C%20ECR%20%7C%20VPC-orange?logo=amazon-aws)
![Terraform](https://img.shields.io/badge/Terraform-IaC-purple?logo=terraform)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Orchestration-blue?logo=kubernetes)
![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-red?logo=jenkins)
![Helm](https://img.shields.io/badge/Helm-Package%20Manager-blue?logo=helm)
![ArgoCD](https://img.shields.io/badge/ArgoCD-GitOps-orange?logo=argo)
![Prometheus](https://img.shields.io/badge/Prometheus-Monitoring-red?logo=prometheus)
![Grafana](https://img.shields.io/badge/Grafana-Dashboards-orange?logo=grafana)

> A production-grade DevOps automation platform that monitors CI/CD pipelines,
> sends real-time alerts on failures, auto-retries failed builds,
> and deploys to AWS EKS using GitOps — fully automated end to end.

---

## 📌 Problem Statement

In modern software teams, developers push code multiple times a day.
Without proper monitoring:

- ❌ Build failures go unnoticed for hours
- ❌ Failed deployments require manual intervention
- ❌ No visibility into pipeline health across services
- ❌ Teams waste time manually checking Jenkins
- ❌ No automatic retry when builds fail due to temporary issues

## ✅ Solution

This platform **fully automates the CI/CD lifecycle** by:

- ✅ Monitoring all Jenkins pipelines in real-time
- ✅ Sending instant Slack and email alerts on failures
- ✅ Auto-retrying failed builds up to 3 times
- ✅ Generating daily build reports automatically
- ✅ Deploying everything to AWS EKS via GitOps
- ✅ Providing live dashboards via Grafana

---

## 🏗️ Architecture Diagram

```mermaid
%%{
  init: {
    'theme': 'dark',
    'themeVariables': {
      'edgeLabelBackground': 'transparent',
      'tertiaryColor': 'transparent'
    }
  }
}%%
graph TD
    %% README Badge Matching Color Scheme (Grey Completely Removed)
    classDef dev fill:#1e3a8a,stroke:#3b82f6,stroke-width:1px,color:#fff;
    classDef git fill:#dc2626,stroke:#111827,stroke-width:1px,color:#fff;
    classDef jenkins fill:#ca8a04,stroke:#111827,stroke-width:1px,color:#fff;
    classDef ecr fill:#ea580c,stroke:#111827,stroke-width:1px,color:#fff;
    classDef argo fill:#f97316,stroke:#111827,stroke-width:1px,color:#fff;
    
    %% Infrastructure & Microservices Custom Colors
    classDef svc fill:#1e293b,stroke:#38bdf8,stroke-width:2px,color:#fff;
    classDef db fill:#000000,stroke:#2563eb,stroke-width:1.5px,color:#38bdf8;
    classDef monitor fill:#16a34a,stroke:#111827,stroke-width:1px,color:#fff;

    %% Base Pipeline Infrastructure Nodes
    DEV[👨‍💻 DEVELOPER]:::dev
    WEB[🌐 GITHUB WEBHOOK]:::git
    JENK[🤖 JENKINS EC2 BUILD SERVER]:::jenkins
    ECR[📦 AWS ECR REGISTRY]:::ecr
    ARGO[🐙 ARGOCD GITOPS ENGINE]:::argo

    subgraph EKS [☸️ PRODUCTION AWS EKS CLUSTER]
        subgraph AUTH [Auth App]
            AS[auth-service :8081]:::svc
            AD[(mysql-auth)]:::db
            AS === AD
        end
        
        subgraph PIPE [Pipeline App]
            PS[pipeline-svc :8082]:::svc
            PD[(mysql-pipeline)]:::db
            PS === PD
        end

        subgraph ALER [Alert App]
            ALS[alert-service :8083]:::svc
            ALD[(mysql-alert)]:::db
            ALS === ALD
        end

        subgraph RETR [Retry App]
            RS[retry-service :8084]:::svc
            RD[(mysql-retry)]:::db
            RS === RD
        end

        subgraph REPO [Report App]
            RPS[report-service :8085]:::svc
            RPD[(mysql-report)]:::db
            RPS === RPD
        end

        subgraph FRONT [Frontend]
            FS[frontend UI :80]:::svc
        end
    end
    style EKS fill:#0f172a,stroke:#38bdf8,stroke-width:3px,color:#fff
    style AUTH fill:#1e293b,stroke:#2563eb,stroke-width:1px,color:#fff
    style PIPE fill:#1e293b,stroke:#2563eb,stroke-width:1px,color:#fff
    style ALER fill:#1e293b,stroke:#2563eb,stroke-width:1px,color:#fff
    style RETR fill:#1e293b,stroke:#2563eb,stroke-width:1px,color:#fff
    style REPO fill:#1e293b,stroke:#2563eb,stroke-width:1px,color:#fff
    style FRONT fill:#1e293b,stroke:#2563eb,stroke-width:1px,color:#fff

    subgraph MON [📊 OBSERVABILITY STACK]
        PROM[Prometheus Time-Series]:::monitor
        GRAF[Grafana Visual Dashboards]:::monitor
        ALERTM[AlertManager Core]:::monitor
        PROM --> GRAF
        PROM --> ALERTM
    end
    style MON fill:#0f172a,stroke:#16a34a,stroke-width:2px,color:#fff

    %% High-Visibility Pipeline Connections (Dark Blue Letter Colors Configured Here)
    DEV -->|<font color='#2563eb'><b>1. git push</b></font>| WEB
    WEB -->|<font color='#2563eb'><b>2. api trigger</b></font>| JENK
    JENK -->|<font color='#2563eb'><b>3. container push</b></font>| ECR
    ECR -->|<font color='#2563eb'><b>4. sync webhook</b></font>| ARGO
    ARGO -->|<font color='#2563eb'><b>5. GitOps deploy</b></font>| EKS
    EKS -->|<font color='#2563eb'><b>6. metrics scrape</b></font>| PROM
    
    %% Main Line Routing Strokes
    linkStyle 0,1,2,3,4,5 stroke:#38bdf8,stroke-width:2px;
```

## 📸 Live Dashboard Screenshots

<p align="center">
  <img src="screenshots/overview-dashboard.png" width="49%" alt="Overview Dashboard" />
  <img src="screenshots/pipelines-view.png" width="49%" alt="Pipelines View" />
</p>
<p align="center">
  <sub><b>Overview</b> — build stats, success rate, active alerts at a glance &nbsp;|&nbsp; <b>Pipelines</b> — live status of every CI/CD job</sub>
</p>

<p align="center">
  <img src="screenshots/alerts-view.png" width="49%" alt="Alerts View" />
  <img src="screenshots/retries-view.png" width="49%" alt="Retries View" />
</p>
<p align="center">
  <sub><b>Alerts</b> — real-time critical failure history &nbsp;|&nbsp; <b>Retries</b> — auto-retry attempts and outcomes</sub>
</p>

<p align="center">
  <img src="screenshots/health-view.png" width="49%" alt="Service Health View" />
</p>
<p align="center">
  <sub><b>Health</b> — live UP/DOWN status of all 5 microservices</sub>
</p>

---

## 🛠️ Tech Stack

| Category | Technology | Purpose |
|----------|-----------|---------|
| **Language** | Java 17 + Spring Boot 3 | Microservices backend |
| **Frontend** | HTML + CSS + JS + Nginx | Dashboard UI |
| **Containerisation** | Docker | Package services into images |
| **CI/CD** | Jenkins + Jenkinsfile | Automated build and push pipeline |
| **Image Registry** | AWS ECR | Store versioned Docker images |
| **Infrastructure** | Terraform | Provision all AWS resources as code |
| **Cloud** | AWS (EKS, VPC, ECR, IAM, EBS) | Production cloud platform |
| **Orchestration** | Kubernetes (EKS) | Run and manage containers |
| **Packaging** | Helm | Package and deploy K8s manifests |
| **GitOps** | ArgoCD | Auto-sync GitHub to EKS |
| **Ingress** | Nginx Ingress Controller | Route traffic to services |
| **Monitoring** | Prometheus | Collect metrics from all pods |
| **Dashboards** | Grafana | Visualise metrics in real-time |
| **Alerting** | AlertManager | Fire alerts on CPU/memory/pod issues |
| **Database** | MySQL 8 (StatefulSet) | Persistent storage per service |
| **Security** | JWT + IAM + IRSA | Authentication and authorisation |
| **Source Control** | GitHub + Webhooks | Code versioning and CI triggers |

---

## 📁 Project Structure

```text
devops-cicd-monitor/
├── auth-service/           # JWT authentication service
│   ├── src/                # Java source code
│   └── Dockerfile          # Container build instructions
├── pipeline-service/       # Jenkins pipeline monitoring
├── alert-service/          # Slack + email alerting
├── retry-service/          # Auto-retry failed builds
├── report-service/         # Daily build reports
├── frontend/               # HTML/CSS/JS dashboard
│   ├── index.html          # Main dashboard page
│   └── nginx.conf          # Nginx configuration
├── helm/                   # Kubernetes packaging
│   └── cicd-monitor/       # Main Helm chart
│       ├── Chart.yaml      # Chart metadata
│       ├── values.yaml     # Configuration values
│       └── templates/      # K8s manifest templates
│           ├── deployment.yaml  # Pod definitions
│           ├── service.yaml     # Network exposure
│           ├── configmap.yaml   # Environment variables
│           ├── hpa.yaml         # Auto scaling rules
│           ├── ingress.yaml     # Traffic routing
│           └── mysql.yaml       # Database StatefulSets
└── terraform/              # AWS Infrastructure as Code
    ├── main.tf             # Provider + S3 backend
    ├── variables.tf        # All configurable values
    ├── vpc.tf              # Network architecture
    ├── eks.tf              # EKS cluster + nodes
    ├── iam.tf              # IAM roles + policies
    └── ecr.tf              # Container registries
```

---

## ⚙️ Prerequisites

Before running this project make sure you have:

| Tool | Version | Purpose |
|------|---------|---------|
| AWS CLI | v2+ | Interact with AWS services |
| Terraform | v1.0+ | Provision infrastructure |
| kubectl | v1.28+ | Manage Kubernetes cluster |
| Helm | v3.0+ | Deploy Kubernetes packages |
| eksctl | v0.100+ | Manage EKS cluster |
| Docker | v20+ | Build container images |
| Java | 17 | Build Spring Boot services |

Also required:
- AWS Account with IAM user credentials configured
- GitHub repository forked or cloned
- Jenkins EC2 instance running with credentials configured

---

## 🚀 How to Run

### Step 1 — Start Jenkins EC2
```
AWS Console → EC2 → Jenkins instance → Start
```

### Step 2 — Run Jenkins Pipeline
```
Jenkins Dashboard → your pipeline job → Build Now
Wait for all stages to show green
This builds and pushes all Docker images to ECR
```

### Step 3 — Deploy Everything
```bash
cd devops-cicd-monitor
./deploy.sh
```

This single script will:
- ✅ Create all AWS infrastructure with Terraform
- ✅ Connect kubectl to EKS cluster
- ✅ Deploy all 6 services via Helm
- ✅ Install Prometheus + Grafana + AlertManager
- ✅ Install and configure ArgoCD
- ✅ Print Load Balancer URL when complete

### Step 4 — Access the Dashboard
```
Open browser → http://LOAD_BALANCER_URL
```

### Step 5 — Access Grafana
```bash
kubectl port-forward svc/monitoring-grafana -n monitoring 3000:80
```
```
Open browser → http://localhost:3000
Username: admin
Password: admin123
```

### Step 6 — Access ArgoCD
```bash
kubectl port-forward svc/argocd-server -n argocd 9000:443
```
```
Open browser → https://localhost:9000
Username: admin
```

---

## 💥 How to Destroy

### Step 1 — Stop Jenkins EC2
```
AWS Console → EC2 → Jenkins instance → Stop
```

### Step 2 — Run Destroy Script
```bash
cd devops-cicd-monitor
./destroy.sh
```

This single script will:
- ✅ Delete all Kubernetes namespaces
- ✅ Wait for Load Balancer to be released
- ✅ Delete leftover ELB security groups
- ✅ Destroy all Terraform infrastructure
- ✅ No stuck VPCs or dangling resources

> ⚠️ **Warning:** This deletes ALL AWS resources including
> ECR images, EKS cluster, VPC and all data.
> Run Jenkins pipeline again before next deploy.sh run.

---

## ✨ Key Features

| Feature | Description |
|---------|-------------|
| **Real-time Monitoring** | Dashboard shows live pipeline status across all services |
| **Instant Alerts** | Slack and email notifications on build failures |
| **Auto Retry** | Failed builds automatically retried up to 3 times |
| **Daily Reports** | Automated build summary report generated every day at 8am |
| **GitOps** | ArgoCD auto-deploys on every git push to main branch |
| **Self Healing** | Kubernetes restarts crashed pods automatically |
| **Auto Scaling** | HPA scales pods based on CPU utilisation |
| **IaC** | Entire AWS infrastructure provisioned with single command |
| **Observability** | Prometheus metrics, Grafana dashboards, AlertManager alerts |
| **Security** | JWT auth, IAM least privilege, private subnets, IRSA |

---

## 🌐 Microservices Overview

| Service | Port | Responsibility |
|---------|------|---------------|
| **auth-service** | 8081 | JWT login, registration, role-based access (ADMIN/VIEWER) |
| **pipeline-service** | 8082 | Polls Jenkins API every 60s, stores build history |
| **alert-service** | 8083 | Sends Slack and email alerts on build failures |
| **retry-service** | 8084 | Auto-retries failed builds up to 3 attempts |
| **report-service** | 8085 | Generates daily build reports at 8am via cron |
| **frontend** | 80 | HTML/CSS/JS dashboard served via Nginx |

---

## 📊 Monitoring & Observability

- **Prometheus** — scrapes metrics from all pods every 15 seconds
- **Grafana** — pre-built Kubernetes dashboards showing CPU, memory, pod health
- **AlertManager** — fires alerts when CPU > 80%, pods crash or memory pressure detected
- **Access Grafana:** `kubectl port-forward svc/monitoring-grafana -n monitoring 3000:80`

<p align="center">
  <img src="screenshots/grafana-cluster-metrics.png" width="49%" alt="Grafana Cluster Metrics" />
  <img src="screenshots/grafana-namespace-metrics.png" width="49%" alt="Grafana Namespace Metrics" />
</p>
<p align="center">
  <sub><b>Cluster-wide</b> compute resources &nbsp;|&nbsp; <b>Namespace-level</b> CPU/memory for cicd-monitor</sub>
</p>

<p align="center">
  <img src="screenshots/grafana-pod-metrics.png" width="49%" alt="Grafana Pod Metrics" />
  <img src="screenshots/prometheus-targets.png" width="49%" alt="Prometheus Targets" />
</p>
<p align="center">
  <sub><b>Pod-level</b> CPU usage for ArgoCD controller &nbsp;|&nbsp; <b>Prometheus</b> scrape targets, all healthy</sub>
</p>

### GitOps Sync Status

<p align="center">
  <img src="screenshots/argocd-sync-status.png" width="80%" alt="ArgoCD Sync Status" />
</p>
<p align="center">
  <sub>ArgoCD — application <b>Healthy</b> and <b>Synced</b>, auto-sync enabled across all service configs</sub>
</p>

---

## 🔐 Security Highlights

- JWT-based authentication with role-based access control (ADMIN/VIEWER)
- EKS worker nodes deployed in **private subnets** — not exposed to internet
- IAM roles with **least privilege** — each role has only required permissions
- **IRSA** (IAM Roles for Service Accounts) — pod level AWS permissions
- ECR image scanning enabled — every push scanned for vulnerabilities
- All inter-service communication inside private VPC network

---

## 👨‍💻 Author

**Prathmmesh Jagdale**
- GitHub: [PrathmmeshJagdale](https://github.com/PrathmmeshJagdale)
- LinkedIn: [PrathmmeshJagdale](https://www.linkedin.com/in/prathmmesh-jagdale)

---

## 📄 License

This project is built for learning and portfolio purposes.

---

*Built with ❤️ using AWS, Kubernetes, Terraform, Jenkins, Helm, ArgoCD, Prometheus and Grafana*
