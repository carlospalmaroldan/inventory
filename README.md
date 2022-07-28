
STARTING THE CLUSTER<br />
Run the following command:<br />
minikube start --driver=docker --container-runtime=containerd

STARTING THE APPLICATION
After minikube has started run the following command
kubectl apply -f inventory_deployment.yaml 

Before asking for endpoints do a port forward
kubectl port-forward svc/inventory 8081:8081




