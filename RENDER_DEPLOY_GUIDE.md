# Guia de Deploy no Render - CitMobi Backend

Este documento orienta a conexão da API Spring Boot com o banco **PostgreSQL no Render** e a execução automática das migrações/tabelas.

---

## 🗄️ Detalhes da sua Instância PostgreSQL no Render

Com base no seu banco provisionado:
- **Internal Database URL** (Uso dentro do Render - Web Service):
  ```
  postgresql://admin:8CalWFyrePr03VeDCUVbZgB52uj04dvV@dpg-dbv5plek1f9s73bhp06g-a/dbmobi_1uyd
  ```
- **External Database URL** (Uso fora do Render - DBeaver, psql local, etc.):
  ```
  postgresql://admin:8CalWFyrePr03VeDCUVbZgB52uj04dvV@dpg-dbv5plek1f9s73bhp06g-a.oregon-postgres.render.com/dbmobi_1uyd
  ```

> [!IMPORTANT]
> **As tabelas e dados já foram criados!**
> Todas as 20 migrações (estruturas, tabelas, usuários, operadores, linhas, itinerários e veículos) foram executadas e validadas com sucesso no seu banco do Render.

---

## 🚀 Como Criar as Tabelas no Banco do Render?

Você tem duas formas de criar/manter as tabelas:

### 1. Automático pelo Spring Boot (Flyway) — Recomendado
O projeto está configurado com **Flyway**:
- Toda vez que o contêiner do CitMobi inicia no Render, o Spring Boot se conecta ao banco e **executa automaticamente qualquer migração pendente** localizada em `classpath:db/migration` e `classpath:db/mocks`.
- Você **nunca precisa rodar scripts SQL manualmente**! O Flyway cuida do versionamento das tabelas automaticamente.

### 2. Manualmente via Maven Wrapper (da sua máquina local)
Se algum dia você quiser rodar as migrações manualmente do seu terminal antes mesmo de iniciar o serviço:
```bash
./mvnw flyway:migrate "-Dflyway.url=jdbc:postgresql://dpg-dbv5plek1f9s73bhp06g-a.oregon-postgres.render.com:5432/dbmobi_1uyd?sslmode=require" "-Dflyway.user=admin" "-Dflyway.password=8CalWFyrePr03VeDCUVbZgB52uj04dvV"
```

---

## ⚙️ Configuração do Web Service no Render

No Dashboard do Render, ao criar ou editar o seu **Web Service** (Docker):

1. **Repositório**: Seu repositório Git
2. **Branch**: `deploy`
3. **Runtime**: `Docker`
4. **Environment Variables**:
   Adicione as seguintes variáveis na aba **Environment**:

   | Variável | Valor | Motivo |
   | :--- | :--- | :--- |
   | `DATABASE_URL` | `postgresql://admin:8CalWFyrePr03VeDCUVbZgB52uj04dvV@dpg-dbv5plek1f9s73bhp06g-a/dbmobi_1uyd` | Conexão interna rápida e gratuita dentro do Render |
   | `SPRING_PROFILES_ACTIVE` | `prod` | Ativa configurações de produção |
   | `JWT_SECRET` | `minha_chave_secreta_super_segura_citmobi_2026` | Segredo para gerar tokens JWT |
   | `CORS_ALLOWED_ORIGINS` | `*` | Permite requisições de frontends web e mobile |
   | `SERVER_SERVLET_CONTEXT_PATH` | `/citmobi` | Mantém os endpoints em `/citmobi/v1/...` |

5. **Health Check Path**:
   ```
   /citmobi/v3/api-docs
   ```

---

## 🔍 Como Testar sua Aplicação

Após o deploy concluir com sucesso:

- **Swagger UI**: `https://<sua-url-render>.onrender.com/citmobi/swagger-ui.html`
- **OpenAPI Docs**: `https://<sua-url-render>.onrender.com/citmobi/v3/api-docs`
- **Login Admin**:
  - `POST https://<sua-url-render>.onrender.com/citmobi/v1/auth/login`
  - Body:
    ```json
    {
      "login": "admin",
      "senha": "amarelo1"
    }
    ```
