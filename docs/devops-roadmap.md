# DevOps Roadmap: Employee Management System
### Jenkins + Kubernetes (kind) + Terraform + LocalStack

This is your end-to-end path from "just a Java app" to a production-style
CI/CD pipeline, entirely runnable on your own machine at zero cost.

---

## Phase 0 — Tooling install (one-time)

| Tool | Purpose |
|---|---|
| Docker Desktop / Docker Engine | run containers, build images |
| `kind` | local Kubernetes cluster |
| `kubectl` | talk to the cluster |
| `helm` | package/deploy the app to k8s |
| `terraform` | provision AWS resources (against LocalStack) |
| LocalStack CLI + `awslocal` | run a fake AWS locally, CLI wrapper for it |
| Jenkins (as a Docker container) | CI/CD orchestrator |
| Git + a GitHub repo | source control, what Jenkins polls/webhooks |

We'll install these as we reach the phase that needs them, so nothing sits
unused.

---

## Phase 1 — Containerize the application (Docker)
**Goal:** the Spring Boot app runs as a container, talking to a
containerized MySQL, no IDE needed.

- Multi-stage `Dockerfile` (Maven build stage → slim JRE runtime stage)
- `docker-compose.yml`: app + MySQL, wired via the `mysql` Spring profile
  you already have
- `.dockerignore`
- Verify: `docker compose up`, hit `http://localhost:8080/api/v1/employees`

*(Building this now — see below.)*

---

## Phase 2 — LocalStack: your fake AWS
**Goal:** LocalStack running, `awslocal` CLI working, first manual resource
created by hand so you understand what Terraform will automate later.

- Run LocalStack via Docker (`localstack/localstack` image)
- Install `awslocal`
- Manually create an ECR repo and an S3 bucket with `awslocal` to build
  intuition before automating it

---

## Phase 3 — Infrastructure as Code (Terraform → LocalStack)
**Goal:** all AWS resources defined as code, applied against LocalStack.

Resources to provision:
- **ECR** repository (to store your Docker image)
- **S3** bucket (build artifacts / Terraform state later)
- **Secrets Manager** entry (DB credentials — no hardcoded passwords)
- **IAM** role/policy (least-privilege practice, even if LocalStack is lenient)
- Optional: **RDS** (MySQL) — or keep MySQL in-cluster; both are valid
  learning paths, RDS is more "production-like"

Terraform provider block points at LocalStack (`http://localhost:4566`)
instead of real AWS — everything else is standard Terraform.

---

## Phase 4 — Local Kubernetes cluster (kind)
**Goal:** a real multi-node-capable k8s cluster on your laptop.

- `kind create cluster`
- Understand `kubectl get nodes`, namespaces, contexts
- Load your locally-built Docker image into `kind` (or pull from the
  LocalStack ECR — both approaches taught)

---

## Phase 5 — Kubernetes manifests / Helm chart for the app
**Goal:** the app deployed on k8s the "real" way.

- `Deployment` (replicas, resource limits, probes using your `/actuator/health`)
- `Service` (ClusterIP, then a way to reach it — NodePort or port-forward)
- `ConfigMap` for non-secret config, `Secret` for DB credentials (pulled
  from LocalStack Secrets Manager in the "production-grade" version)
- Package it as a **Helm chart** so it's parameterized per environment

---

## Phase 6 — Jenkins pipeline (CI/CD, orchestrating everything above)
**Goal:** push to Git → Jenkins builds, tests, containerizes, provisions
infra, deploys — automatically.

Pipeline stages (a `Jenkinsfile`):
1. Checkout
2. `mvn test` (unit tests gate the build)
3. `mvn package` → build jar
4. Build Docker image, tag with commit SHA
5. Push image to LocalStack ECR
6. `terraform plan` / `apply` (infra as code, only applies diffs)
7. `helm upgrade --install` to the kind cluster
8. Smoke test the deployed `/actuator/health` endpoint

Jenkins itself runs as a Docker container with the Docker socket mounted
so it can build images ("Docker-in-Docker" pattern).

---

## Phase 7 — Observability
**Goal:** know what's happening in the running system.

- Prometheus + Grafana on the cluster (Spring Boot Actuator already
  exposes metrics-friendly endpoints)
- Centralized logs (even just `kubectl logs`, then optionally a log
  aggregator)

---

## Phase 8 — GitHub Actions (second CI tool, comparative learning)
**Goal:** the same pipeline, cloud-native style, so you can compare
Jenkins (self-hosted, stateful, plugin-driven) vs GitHub Actions
(hosted, YAML-native, ephemeral runners).

---

## Phase 9 — Production-grade polish
- Multiple environments (dev/staging/prod) via Terraform workspaces +
  separate Helm value files
- Secrets never in Git — pulled from Secrets Manager at deploy time
- Rolling updates / rollback strategy in k8s
- (Stretch) GitOps with ArgoCD instead of Jenkins pushing directly

---

## How we'll work through this

Each phase = one focused session: I generate the files, explain *why* each
piece exists (not just paste-and-run), you execute it locally, we debug
together, then move on. Nothing here assumes you already know the tool —
we build the mental model as we go.

**Next up: Phase 1 — Dockerizing the app.**
