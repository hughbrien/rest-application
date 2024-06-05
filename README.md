## Demo Java Application 

```

docker buildx build --platform linux/amd64,linux/arm64 --push -t hughbrien/restapplication:1.0.0 .

docker build --platform linux/arm64  --tag hbrien622/rest-appdynamics_arm64:1.0.2  .;
docker push hbrien622/rest-appdynamics_arm64:1.0.2;

docker build --platform linux/amd64 --tag hbrien622/rest-appdynamics_amd64:1.0.2  .;
docker push hbrien622/rest-appdynamics_amd64:1.0.2;

kubectl rollout status deployment/rest-appdynamics -n rest-appdynamics
kubectl rollout history deployment/rest-appdynamics -n rest-appdynamics

kubectl rollout pause deployment/rest-appdynamics -n rest-appdynamics

kubectl rollout resume deployment/rest-appdynamics -n rest-appdynamics



```

### Instrumentation
```
kubectl patch deployment rest-appdynamics -n rest-appdynamics -p '{"spec":{"template":{"metadata":{"annotations":{"instrumentation.opentelemetry.io/inject-java":"default/splunk-otel-collector"}}}}}'

```



