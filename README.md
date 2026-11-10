# Quality Gate Pipeline

> Projeto de portfólio focado em **DevOps voltado para qualidade**: um pipeline de CI/CD em que cada etapa funciona como um *quality gate*, capaz de impedir que código com problemas chegue à produção.

![status](https://img.shields.io/badge/status-em%20constru%C3%A7%C3%A3o-yellow)

## Objetivo

Demonstrar, na prática, como garantir qualidade de software de forma automatizada ao longo de todo o ciclo de entrega (*shift-left*):

- Qualidade de código (lint, padrões, análise estática)
- Testes em camadas (unitários, integração, carga)
- Segurança no pipeline (SAST, scan de dependências e containers, detecção de secrets)
- Deploy com verificação pós-deploy

A aplicação em si é simples de propósito: **o foco do projeto é o pipeline, não a API.**

## Quality gates planejados

| # | Gate | Ferramenta (prevista) | Bloqueia quando... | Status |
|---|------|----------------------|--------------------|--------|
| 1 | Detecção de secrets | Gitleaks | um secret é commitado | ✅ ativo |
| 2 | Lint / estilo | Checkstyle / Spotless | o código foge do padrão | ⏳ |
| 3 | Testes unitários + cobertura | JUnit 5 + JaCoCo | um teste falha ou a cobertura fica abaixo de 80% | ⏳ |
| 4 | Análise estática | SonarCloud | o quality gate do Sonar reprova | ⏳ |
| 5 | Testes de integração | Testcontainers | a integração com o banco falha | ⏳ |
| 6 | SAST | Semgrep | uma vulnerabilidade é encontrada | ⏳ |
| 7 | Scan de container | Trivy | a imagem tem CVE crítica/alta | ⏳ |
| 8 | Teste de carga | k6 | o p95 de latência passa do limite | ⏳ |
| 9 | Smoke test pós-deploy | _a definir_ | o serviço não responde após o deploy | ⏳ |

Cada gate é um job do GitHub Actions. Se um gate falha, o PR não pode ser mergeado (via *branch protection* na `main`).

### Fluxo

```
secret-scan ─┬─> lint ─> unit-tests ─┬─> sonarcloud
             │                       └─> integration-tests ─┐
             └─> sast ──────────────────────────────────────┴─> container-scan ─> load-test ─> deploy ─> smoke-test
```

### Critérios de aprovação

- **Secrets:** nenhum secret no histórico (um secret vazado continua no Git mesmo depois de apagado)
- **Lint:** zero violações (Java: Checkstyle ou Spotless)
- **Testes unitários:** todos passam e cobertura de linhas ≥ 80% (Java: JUnit 5 + JaCoCo)
- **SonarCloud:** quality gate padrão aprovado (sem bugs/vulnerabilidades novas, duplicação baixa)
- **Integração:** testes passam contra um Postgres real via Testcontainers
- **SAST:** nenhum achado do Semgrep com severidade ERROR
- **Container:** nenhuma CVE CRITICAL ou HIGH com correção disponível
- **Carga:** p95 < 300 ms e taxa de erro < 1% ([`tests/load/smoke.js`](tests/load/smoke.js))
- **Smoke test:** `/health` responde 200 após o deploy; se falhar, rollback

## API

Um CRUD simples de tarefas em **Java 25 + Spring Boot 4**, persistido em Postgres ([ADR 0002](docs/adr/0002-linguagem-da-api.md)).

O código fica em `app/src/main/java/dev/qgp/tasks/task/`, dividido em camadas:

| Arquivo | Papel |
|---------|-------|
| `TaskController` | Recebe o HTTP, valida a entrada e define o status da resposta |
| `TaskService` | Regras de negócio e transações |
| `TaskRepository` | Acesso ao banco (implementação gerada pelo Spring Data) |
| `Task` | Entidade JPA, mapeada para a tabela `tasks` |
| `TaskRequest` / `TaskResponse` | Formato do JSON de entrada e de saída |

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/health` | Health check (usado pelo smoke test) |
| GET | `/tasks` | Lista tarefas |
| GET | `/tasks/{id}` | Busca uma tarefa |
| POST | `/tasks` | Cria uma tarefa |
| PUT | `/tasks/{id}` | Atualiza uma tarefa |
| DELETE | `/tasks/{id}` | Remove uma tarefa |

Exemplo de corpo para `POST` e `PUT` (`description` e `done` são opcionais):

```json
{ "title": "Estudar Spring", "description": "Capítulo de JPA", "done": false }
```

Respostas: `201` ao criar, `204` ao remover, `400` se o título estiver vazio e `404` se o id não existir.

## Quadro de tarefas (front-end)

Um quadro estilo kanban com duas colunas, **A fazer** e **Feito**, disponível em `http://localhost:8080/`. Permite criar, excluir e mover tarefas entre as colunas (arrastando ou pelos botões).

É um único arquivo, [`app/src/main/resources/static/index.html`](app/src/main/resources/static/index.html), com HTML, CSS e JavaScript puro, sem bibliotecas nem etapa de build. O próprio Spring Boot serve o arquivo, então o front usa a API no mesmo endereço, sem CORS e sem container extra.

## Estrutura do repositório

```
.
├── .github/
│   ├── workflows/ci.yml          # Pipeline de CI com os quality gates
│   ├── dependabot.yml            # Atualização automática de dependências
│   └── pull_request_template.md  # Checklist de qualidade para PRs
├── app/                          # API Spring Boot + quadro em static/ (pom.xml, Dockerfile, src/)
├── docs/adr/                     # Registros de decisões de arquitetura
├── tests/
│   └── load/                     # Testes de carga com k6
├── docker-compose.yml            # Postgres + API
├── .env.example                  # Variáveis de ambiente de exemplo
└── .editorconfig                 # Padrão de formatação entre editores
```

## Como rodar localmente

```bash
# 1. Copie as variáveis de ambiente
cp .env.example .env

# 2. Suba tudo (Postgres + API) com Docker
docker compose up -d --build
curl localhost:8080/health   # depois abra http://localhost:8080/ no navegador
```

Para desenvolver sem rebuildar a imagem a cada mudança, suba só o banco e rode a API pelo Maven Wrapper (precisa do Java 25):

```bash
docker compose up -d postgres
cd app
./mvnw spring-boot:run   # lê as variáveis do ../.env automaticamente
./mvnw test              # roda os testes
```

## Roadmap

- [x] **Fase 1 – Base:** estrutura do repositório, pipeline inicial, detecção de secrets
- [x] **Fase 1 – Base:** API simples (CRUD) + Dockerfile multi-stage
- [ ] **Fase 2 – Qualidade de código:** lint, cobertura mínima, SonarCloud
- [ ] **Fase 3 – Testes em camadas:** integração com Testcontainers, carga com k6
- [ ] **Fase 4 – Segurança:** Semgrep, Trivy, SBOM
- [ ] **Fase 5 – Deploy:** deploy automático, smoke test e rollback

## Decisões de arquitetura

As decisões do projeto são registradas como ADRs em [`docs/adr/`](docs/adr/).
