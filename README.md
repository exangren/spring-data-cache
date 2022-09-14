# Spring Data on Kubernetes with Hazelcast Cache
## Description
This is a simple Java application that demonstrates building of 
Spring Boot application with Data JPA (Hibernate) and the second-level
cache based on Hazelcast. The target environment of the application
is Kubernetes.

## Installation
To build and run the application you need:
* JDK 11 (I think the code will work with Java 8, though it will require changes in pom.xml)
* Apache Maven
* Minikube to set up a local Kubernetes cluster
* Docker CLI to build an application Docker image

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

