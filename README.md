# Spring Data on Kubernetes with Hazelcast Cache
## Description
This is a simple Java application that demonstrates building of 
Spring Boot application with Data JPA (Hibernate) and the second-level
cache based on Hazelcast. The target environment of the application
is Kubernetes.

## Installation
To build and run the application you need:
* **JDK 11** - Highly likely Java 8 will work as well,
though it will require changes in pom.xml
* **Apache Maven**
* **Minikube** to set up a local Kubernetes cluster
* **Docker CLI** to build an application Docker image
* **MySQL** - The version is not important. 
`MYSQL_HOST`, `MYSQL_PORT`, and other environment 
variables might be set up or updated correspondingly. 
See `app-k8s-deployment.yaml` and `database-k8s-deployment.yaml`, 
where these variables are used.

The application was build and run on Microsoft Windows, 
but I believe it will work on other platforms as well after some 
tweaking of slashes.

## Usage
Build:
```shell
mvn clean verify
```
Build Docker image:
```shell
docker build -f ./docker/Dockerfile -t rapp/appusers:1.0 .
```
Optionally (MySQL deployment):
```shell
kubectl apply -f k8s/database-k8s-deployment.yaml
```
Deploy on Kubernetes (Minikube):
```shell
kubectl apply -f k8s/app-hazelcast-service.yaml
kubectl apply -f k8s/app-hazelcast-rbac.yaml
kubectl apply -f k8s/app-k8s-deployment.yaml
kubectl apply -f k8s/app-k8s-service.yaml
```
**Note:** Update app-k8s-deployment.yaml before deploying so it declares
correct MySQL host and port values.

## Kubernetes Auto Discovery in DNS Lookup Mode
According to documentation on [Kubernetes Auto Discovery](https://docs.hazelcast.com/hazelcast/5.2/kubernetes/kubernetes-auto-discovery.adoc):
> "Hazelcast allows members to discover each other automatically, using discovery modes."

Two discovery modes are Kubernetes API and DNS Lookup. Originally, in this tutorial it is used
Kubernetes API, which is stated as more flexible and easier to set up. However, the using of
Kubernetes API *"requires setting up RoleBinding (to allow access to Kubernetes API)"*, which is not
always allowed by a service mesh like *Istio*.

In the branch **dns_based_discovery** you can find updated settings and configurations
to run the application with Hazelcast embedded cluster using DNS lookup
(**headless** service) without cluster IP.

The changes to use **headless** discovery service are not large.
1. Set that discovery service does not need `ClusterIP`:
```yaml
  # k8s/app-hazelcast-service.yaml
  clusterIP: None
```
2. The service does not need discover other services inside Kubernetes cluster:
```yaml
  # k8s/app-hazelcast-rbac.yaml
  # remove the following line from rules.resources section
  - services
```
3. Change Hazelcast configuration to use DNS lookup instead of a service:
```yaml
  # src/main/resources/cache.yaml
  # replace 'service-name: hazelcast' line by the following line:
  service-dns: hazelcast.default.svc.cluster.local
```
The name of **headless** service is formed from the name of the service, which is defined in
`k8s/app-hazelcast-service.yaml` appended by a namespace name followed by a default suffix
`svc.cluster.local`.

Re-apply Kubernetes configs and redeploy the application. In the logs you will see that Hazelcast
now is using DNS lookup for cache member discovery.
