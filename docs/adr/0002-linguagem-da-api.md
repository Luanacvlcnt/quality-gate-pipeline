# ADR 0002 · Linguagem da API

- **Status:** Aceito
- **Data:** 2026-11-05

## Contexto

A API é simples (um CRUD com Postgres); o foco do projeto é o pipeline. A linguagem precisa ter um bom ecossistema de ferramentas de qualidade para que cada gate seja bem demonstrado.

## Opção avaliada: Java + Spring Boot

**A favor**
- Familiaridade prévia com a linguagem
- Padrão de mercado em APIs corporativas
- Ecossistema de qualidade maduro: JUnit 5, Mockito, JaCoCo, Checkstyle/Spotless, SpotBugs
- Testcontainers tem sua melhor integração em Java
- Permite teste de mutação com PIT, um diferencial em projetos de qualidade
- Excelente suporte no SonarCloud

**Pontos de atenção**
- Imagens Docker maiores e build mais lento (mitigado com Dockerfile multi-stage e imagem JRE enxuta)
- Escolher entre Maven e Gradle

## Decisão

Java 17 + Spring Boot 4, com **Maven** como ferramenta de build (via Maven Wrapper, `./mvnw`, para que ninguém precise instalar o Maven).

Maven foi escolhido por ser declarativo (`pom.xml`), o mais comum em projetos Spring e o mais documentado nos plugins de qualidade (JaCoCo, Checkstyle, Sonar).

## Consequências

- Os gates 2 e 3 vão usar Checkstyle/Spotless e JUnit 5 + JaCoCo.
- O Dependabot monitora o `pom.xml`.
- A imagem final usa só a JRE (`eclipse-temurin:17-jre-alpine`), sem Maven nem JDK.
