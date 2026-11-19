# Guia de Deploy no Render - CitMobi Backend

Este guia orienta a implantação contínua da API Spring Boot `CitMobi` no [Render](https://render.com) utilizando Docker na branch `deploy`.

---

## 🚀 Opção 1: Deploy Automático via Render Blueprint (`render.yaml`)

O repositório inclui um arquivo [`render.yaml`](render.yaml) pré-configurado com a API e o banco PostgreSQL.

1. Acesse o [Dashboard do Render](https://dashboard.render.com/).
2. Clique em **Blueprints** no menu superior e depois em **New Blueprint Instance**.
3. Conecte o repositório do CitMobi e selecione a branch `deploy`.
4. O Render detectará automaticamente os dois serviços:
   - **PostgreSQL Database** (`citmobi-db`)
   - **Web Service Docker** (`citmobi-backend`)
5. Clique em **Apply**. O Render provisionará o banco, injetará a variável `DATABASE_URL` automaticamente e construirá a imagem Docker.

---

## 🛠️ Opção 2: Deploy Manual pelo Dashboard do Render

Se preferir criar os recursos individualmente:

### Passo 1: Criar o Banco PostgreSQL no Render
1. No dashboard do Render, clique em **New +** -> **PostgreSQL**.
2. Defina:
   - **Name**: `citmobi-db`
   - **Database**: `dbmobi`
   - **User**: `postgres`
   - **Plan**: Free
3. Clique em **Create Database**.
4. Quando o banco estiver criado, copie a **Internal Database URL** (ex: `postgresql://postgres:senha@dpg-xxxxxx-a.oregon-postgres.render.com/dbmobi`).

### Passo 2: Criar o Web Service (Docker)
1. Clique em **New +** -> **Web Service**.
2. Conecte o repositório GitHub e selecione:
   - **Branch**: `deploy`
   - **Runtime**: `Docker`
   - **Dockerfile Path**: `Dockerfile`
   - **Plan**: Free
3. Na seção **Environment Variables**, adicione:
   | Variável | Valor | Descrição |
   | :--- | :--- | :--- |
   | `DATABASE_URL` | *(Cole a Internal Database URL do Passo 1)* | URL de conexão fornecida pelo Render |
   | `SPRING_PROFILES_ACTIVE` | `prod` | Ativa o perfil de produção |
   | `JWT_SECRET` | *(Gere uma string segura de 32+ caracteres)* | Segredo de assinatura dos tokens JWT |
   | `CORS_ALLOWED_ORIGINS` | `*` (ou suas URLs frontend) | Permite chamadas de apps web e mobile |
   | `SERVER_SERVLET_CONTEXT_PATH` | `/citmobi` | Mantém compatibilidade com `/citmobi/v1/...` |
   | `SPRING_FLYWAY_LOCATIONS` | `classpath:db/migration,classpath:db/mocks` | Executa migrations e popula dados iniciais |
4. Na seção **Health Check Path**, informe:
   - `/citmobi/v3/api-docs`
5. Clique em **Create Web Service**.

---

## ⚙️ Variáveis de Ambiente Suportadas

O contêiner processa dinamicamente as variáveis de ambiente:

- **`PORT`**: Porta HTTP da aplicação (injetada automaticamente pelo Render, padrão `10000` ou `8080`).
- **`DATABASE_URL`**: URL PostgreSQL no padrão Render (`postgresql://user:pass@host:port/db`). O script [`entrypoint.sh`](entrypoint.sh) converte automaticamente para o formato JDBC exigido pelo Spring Boot / HikariCP.
- **`SPRING_DATASOURCE_URL`**, **`SPRING_DATASOURCE_USERNAME`**, **`SPRING_DATASOURCE_PASSWORD`**: Alternativa explícita caso não use `DATABASE_URL`.
- **`SPRING_PROFILES_ACTIVE`**: Perfil Spring Boot (`prod`, `dev` ou `hom`). Padrão: `prod`.
- **`JWT_SECRET`**: Segredo para validação e geração de tokens JWT.
- **`CORS_ALLOWED_ORIGINS`**: Lista de origens permitidas separadas por vírgula (ex: `http://localhost:3000,https://meu-app.vercel.app` ou `*`).
- **`SERVER_SERVLET_CONTEXT_PATH`**: Context path da API (padrão `/citmobi`). Se desejar servir diretamente na raiz `/`, configure como `/`.
- **`SPRING_FLYWAY_LOCATIONS`**: Localizações dos scripts SQL do Flyway. Por padrão inclui mocks para popular o banco novo. Caso queira apenas as tabelas puras sem seeds, altere para `classpath:db/migration`.
- **`JAVA_OPTS`**: Opções de tuning da JVM (padrão: `-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0`).

---

## 🔍 Como Testar e Validar

Assim que o deploy for concluído no Render (`https://sua-api.onrender.com`):

1. **Documentação Swagger UI**:
   - `https://sua-api.onrender.com/citmobi/swagger-ui.html`
2. **OpenAPI Spec (JSON)**:
   - `https://sua-api.onrender.com/citmobi/v3/api-docs`
3. **Teste de Login (POST /v1/auth/login)**:
   - URL: `https://sua-api.onrender.com/citmobi/v1/auth/login`
   - Body:
     ```json
     {
       "login": "admin",
       "senha": "amarelo1"
     }
     ```
