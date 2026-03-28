FROM maven:3.9.14-eclipse-temurin-25 AS builder

ARG UID=1010
ARG GID=1010

RUN groupadd -g "${GID}" jac \
    && useradd --create-home --no-log-init -u "${UID}" -g jac jac

WORKDIR /opt/app

COPY --chown=jac:jac pom.xml .
RUN mvn dependency:go-offline -q

COPY --chown=jac:jac src ./src

RUN mvn clean package -DskipTests -q


FROM eclipse-temurin:25-jre AS runtime

ARG BUILD_DATE=unknown
ARG BUILD_VERSION=unknown
ARG IMAGE_DESCRIPTION=unknown
ARG IMAGE_NAME="JAC-Manager-API"
ARG UID=1010
ARG GID=1010

LABEL group6.jac-manager.build-date=$BUILD_DATE \
      group6.jac-manager.name=$IMAGE_NAME \
      group6.jac-manager.description=$IMAGE_DESCRIPTION \
      group6.jac-manager.base.image="eclipse-temurin:25-jre" \
      group6.jac-manager.version=$BUILD_VERSION \
      maintainer="Sebastian"

RUN groupadd -g "${GID}" jac \
    && useradd --create-home --no-log-init -u "${UID}" -g jac jac

WORKDIR /opt/app

COPY --from=builder --chown=jac:jac /opt/app/target/jac-manager-application.jar app.jar

EXPOSE 8080

USER jac

# Flags optimizados para WebFlux reactivo + Java 25
# -XX:+UseZGC           → GC de baja latencia (ideal para APIs reactivas)
# -XX:+ZGenerational    → ZGC generacional (disponible desde Java 21, default en 25)
# -Xss256k              → stack más pequeño por hilo (WebFlux usa pocos hilos)
# -XX:MaxRAMPercentage  → límite de heap relativo al RAM del contenedor
ENTRYPOINT ["java", \
  "-XX:+UseZGC", \
  "-XX:+ZGenerational", \
  "-Xss256k", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]