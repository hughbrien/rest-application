## Demo Java Application 

```

docker buildx build --platform linux/amd64,linux/arm64 --push -t hughbrien/restapplication:1.0.0 .

docker build --platform linux/arm64  --tag hbrien622/rest-application:1.0.2  .;
docker push hbrien622/rest-application_arm64:1.0.2;

docker build --platform linux/amd64 --tag hbrien622/rest-application:1.0.2  .;
docker push hbrien622/rest-application_amd64:1.0.2;

kubectl rollout status deployment/rest-application -n rest-application
kubectl rollout history deployment/rest-application -n rest-application

kubectl rollout pause deployment/rest-application -n rest-application

kubectl rollout resume deployment/rest-application -n rest-application



```

### Instrumentation
```
kubectl patch deployment rest-application -n rest-application -p '{"spec":{"template":{"metadata":{"annotations":{"instrumentation.opentelemetry.io/inject-java":"default/splunk-otel-collector"}}}}}'

```



