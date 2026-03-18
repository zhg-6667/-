#!/usr/bin/env groovy

// The library is defined under <Jenkins_URL>/configure - "Global Pipeline Libraries".
library("quark@lib")
//library("quark@dev")
//library("quark@master")
buildSpringbootDubboApp builtJar: "license-service/target/license-service-0.0.1-SNAPSHOT.jar", deployJar: "service-license-0.0.1-SNAPSHOT.jar",
        rdK8s: "true", projectType: "service", rdSvcName: "service-license", dubboPort: 20903, springProfile: "DUMMY", javaOpts: " -XX:HeapDumpPath=/usr/local/service-license/logs/dump/"

