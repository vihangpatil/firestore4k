# Developer guidelines

## Checking for dependency updates

```bash
./gradlew refreshVersions
```

## Checking for dependency resolution

```bash
./gradlew dependencyInsight --configuration runtimeClasspath --dependency dependency-name
```

