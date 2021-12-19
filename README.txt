Deploying to local k8s cluster in k8s for docker desktop

1) Check the context of k8s cluster running on docker desktop by running the command below.
Should equal to docker-desktop
kubectl config current-context

2) Check the namespaces available on the context by executing the command below. We will use the default namespace
kubectl get namespaces --context=docker-desktop

3) Navigate to fodmap-service module and create the secret on the k8s cluster with the command below
kubectl  --namespace=default --context=docker-desktop apply -f secret.yml
Check that the secret has been created on the namespace by executing the command below
kubectl  --namespace=default --context=docker-desktop get secrets

4) Navigate to cassandra module. build image for cassandra using dockerfile with the name declared within the pod.yml
k8s normally looks for this image of this name on dockerhub. Since  imagePullPolicy: Never has been added to pod.yml,
k8s will use image of this name stored in the local docker registry
docker build -t local/cassandra .

5) Create pod for cassandra by executing command below
kubectl  --namespace=default --context=docker-desktop apply -f pod.yml

6) Create a service for cassandra. This will allow other applications to connect to the cassandra pod.
Target port is the port cassandra pod in listening on. Port is the port/cluster ip of the service being made for cassandra
The selector is used to map the port to a pod. So any request made to port 9042 will be mapped to port 9042 on a pod named cassandra
https://www.youtube.com/watch?v=5lzUpDtmWgM
kubectl  --namespace=default --context=docker-desktop create -f service.yml
You can see the service create using
kubectl  --namespace=default --context=docker-desktop get services

Running functional tests locally
Need to start wiremock for functional tests involving readiness endpoint
cassandra





