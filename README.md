## Demo Java Application 

```

docker buildx build --platform linux/amd64,linux/arm64 --push -t hughbrien/restapplication:1.0.0 .

docker build --platform linux/arm64  --tag hbrien622/restapplication_arm64:1.0.2  .;
docker push hbrien622/restapplication_arm64:1.0.2;

docker build --platform linux/amd64 --tag hbrien622/restapplication_amd64:1.0.2  .;
docker push hbrien622/restapplication_amd64:1.0.2;

kubectl rollout status deployment/restapplication -n restapplication
kubectl rollout history deployment/restapplication -n restapplication

kubectl rollout pause deployment/restapplication -n restapplication

kubectl rollout resume deployment/restapplication -n restapplication



```

### Instrumentation
```
kubectl patch deployment restapplication -n restapplication -p '{"spec":{"template":{"metadata":{"annotations":{"instrumentation.opentelemetry.io/inject-java":"default/splunk-otel-collector"}}}}}'

```



