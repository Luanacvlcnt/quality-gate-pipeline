# Quality Gate Pipeline

> Projeto focado em **DevOps voltado para qualidade**: um pipeline de CI/CD em que cada etapa funciona como um *quality gate*, capaz de impedir que código com problemas chegue à produção.

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
| 2 | Lint / estilo | _a definir com a linguagem_ | o código foge do padrão | ⏳ |
| 3 | Testes unitários + cobertura | _a definir_ | um teste falha ou a cobertura fica abaixo de 80% | ⏳ |
| 4 | Análise estática | SonarCloud | o quality gate do Sonar reprova | ⏳ |
| 5 | Testes de integração | Testcontainers | a integração com o banco falha | ⏳ |
| 6 | SAST | Semgrep | uma vulnerabilidade é encontrada | ⏳ |
| 7 | Scan de container | Trivy | a imagem tem CVE crítica/alta | ⏳ |
| 8 | Teste de carga | k6 | o p95 de latência passa do limite | ⏳ |
| 9 | Smoke test pós-deploy | _a definir_ | o serviço não responde após o deploy | ⏳ |

Detalhes de cada gate em [`docs/pipeline.md`](docs/pipeline.md).

## Estrutura do repositório

```
.
├── .github/
│   ├── workflows/ci.yml          # Pipeline de CI com os quality gates
│   ├── dependabot.yml            # Atualização automática de dependências
│   └── pull_request_template.md  # Checklist de qualidade para PRs
├── app/                          # Código da API (linguagem a definir)
├── docs/
│   ├── adr/                      # Registros de decisões de arquitetura
│   └── pipeline.md               # Descrição de cada quality gate
├── tests/
│   └── load/                     # Testes de carga com k6
├── docker-compose.yml            # Dependências locais (Postgres)
├── .env.example                  # Variáveis de ambiente de exemplo
└── .editorconfig                 # Padrão de formatação entre editores
```

## Como rodar localmente

```bash
# 1. Copie as variáveis de ambiente
cp .env.example .env

# 2. Suba as dependências
docker compose up -d
```

_Instruções da aplicação serão adicionadas quando a API for implementada._

## Roadmap

- [x] **Fase 1 – Base:** estrutura do repositório, pipeline inicial, detecção de secrets
- [ ] **Fase 1 – Base:** API simples (CRUD) + Dockerfile multi-stage
- [ ] **Fase 2 – Qualidade de código:** lint, cobertura mínima, SonarCloud
- [ ] **Fase 3 – Testes em camadas:** integração com Testcontainers, carga com k6
- [ ] **Fase 4 – Segurança:** Semgrep, Trivy, SBOM
- [ ] **Fase 5 – Deploy:** deploy automático, smoke test e rollback

## Decisões de arquitetura

As decisões do projeto são registradas como ADRs em [`docs/adr/`](docs/adr/).
