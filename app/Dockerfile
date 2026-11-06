# ---- Estágio 1: build (JDK completo + Maven) ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /src

# Baixa as dependências antes de copiar o código: enquanto o pom.xml não mudar,
# o Docker reaproveita esta camada do cache e o build fica bem mais rápido.
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw -B -q dependency:go-offline

COPY src src
RUN ./mvnw -B -q package -DskipTests

# ---- Estágio 2: runtime (só a JRE + o .jar, imagem bem menor) ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Não roda como root dentro do container
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /src/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
