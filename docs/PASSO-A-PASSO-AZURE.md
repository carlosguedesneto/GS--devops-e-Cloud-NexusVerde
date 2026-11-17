# Nexus Verde — Passo a passo de provisionamento e publicação no Azure

---

## 0. Preparação

**Ferramentas**
- [Azure CLI](https://learn.microsoft.com/pt-br/cli/azure/install-azure-cli) instalado.
- Git instalado.
- Conta Azure com uma subscription ativa (a conta da FIAP).

**Verificar**
```bash
az version
az login
az account show
```


---

## 1. Provisionamento em nuvem (Azure CLI)

No terminal, na raiz do projeto:

```bash
cd nexus-verde

# Escolha uma senha forte para o administrador do SQL Server
export SQL_ADMIN_PASSWORD='TroqueEstaSenha@123'

# Cria Resource Group, SQL Server, SQL Database, App Service Plan e Web App
bash scripts/azure-infra.sh
```

O script imprime no final os valores de `DB_URL`, `DB_USER` e `DB_PASS`. **Anote-os** — você vai usá-los nas variáveis do pipeline.

**Aplicar o DDL no banco**

1. Portal Azure → seu **SQL Server** (`sprint4-db`) → **Editor de consultas (visualização prévia)**.
2. Entre com o usuário `fiapadmin` e a senha definida.
3. Cole o conteúdo de `scripts/script-bd.sql` e execute.
4. Confira as tabelas: `regioes_afetadas`, `pontos_agua`, `solicitacoes_ajuda`.

**Validar o Web App**
- Portal Azure → **App Services** → `app-nexus-verde-rm566022` → **Visão geral** → anote a URL (`https://app-nexus-verde-rm566022.azurewebsites.net`).
- Em **Configurações de configuração** (Configuration), confira as variáveis `DB_URL`, `DB_USER`, `DB_PASS` criadas pelo script.

---

## 2. Projeto no Azure DevOps

1. Acesse `https://dev.azure.com` e entre com sua conta.
2. Se não tiver organização, crie uma (`New organization`). Anote o link da organização.
3. **New project**:
   - **Name:** `Nexus Verde`
   - **Visibility:** **Private** (requisito 01)
   - **Version control:** **Git**
   - **Work item process:** `Agile` (ou `Scrum`)
   - **Create**.



---

## 3. Código no Azure Repos

1. No projeto → **Repos** → **New repository**:
   - **Name:** `nexus-verde`
   - **Type:** Git
   - **Private** (desmarque "Add a README")
   - **Create**.
2. No terminal local, dentro da pasta do projeto:

```bash
cd nexus-verde
git init
git branch -M main
git add .
git commit -m "chore: estrutura inicial do Nexus Verde"

# URL do remote: Repos > nexus-verde > Clone > Clone in VS Code / copie a URL HTTPS
git remote add origin https://dev.azure.com/<sua-organizacao>/Nexus%20Verde/_git/nexus-verde
git push -u origin main
```

3. No portal, em **Repos > Files**, confirme que `azure-pipeline.yml` está na **raiz**.

---

## 4. Azure Boards — tarefa inicial

1. No projeto → **Boards** → **Work items** → **New Work Item** → **Task**.
2. Preencha:
   - **Title:** `Implementar CRUD do Nexus Verde e publicar em nuvem`
   - **Description:** objetivo, escopo (regiões afetadas, pontos de água, solicitações de ajuda) e critérios de aceite.
   - **Area / Iteration:** os do projeto.
   - **Save**. Anote o **ID** (ex.: `#1001`).
3. Para vincular commits, branches e PRs, use o prefixo do ID nas mensagens:
   - `git commit -m "feat: ajusta status da solicitacao AB#1001"`
   - O Azure Boards cria o link automaticamente em **Work item > Development**.
4. Também dá para vincular manualmente: abra o work item → **Add link** → **Branch / Commit / Pull request**.

---

## 5. Service Connection (conexão com a Azure)

1. **Project settings** → **Service connections** → **New service connection**.
2. Escolha **Azure Resource Manager** → **Next**.
3. **Authentication method:** **Service principal (automatic)**.
4. **Scope level:** **Subscription**.
5. Selecione sua **Subscription** e o **Resource Group** `rg-nexus-verde`.
6. **Service connection name:** `Azure-Service-Connection` (exatamente esse nome, pois é o que o pipeline usa).
7. **Save**.

---

## 6. Variáveis do pipeline

1. No projeto → **Pipelines** → **Library** → **+ Variable group**.
2. **Variable group name:** `nexus-verde-vars`.
3. Marque **Allow access to all pipelines**.
4. Adicione as variáveis:

| Name | Value |
| :--- | :--- |
| `azureSubscription` | `Azure-Service-Connection` |
| `webAppName` | `app-nexus-verde-rm566022` |
| `resourceGroupName` | `rg-nexus-verde` |
| `DB_URL` | connection string gerada pelo script (`jdbc:sqlserver://...`) |
| `DB_USER` | `fiapadmin` |
| `DB_PASS` | a senha definida em `SQL_ADMIN_PASSWORD` |

5. **Save**.

---

## 7. Pipeline de Build e Release (CI/CD)

O repositório já traz o `azure-pipeline.yml` na raiz, com as duas stages (`Build_and_Test_CI` e `Deploy_CD`).

1. No projeto → **Pipelines** → **Create pipeline**.
2. **Azure Repos Git** → selecione `nexus-verde` → **Existing Azure Pipelines YAML file**.
3. **Path:** `/azure-pipeline.yml` → **Continue** → **Run**.
4. Confira o trigger no topo do arquivo:
   ```yaml
   trigger:
     branches:
       include:
         - main
   ```
   O CI roda automaticamente a cada merge na `main`.
5. A stage de CD roda sozinha após o artefato do CI (requisito 06).

**O que o CI faz**
- Instala JDK 17.
- `mvn clean test package` em `nexus-verde/pom.xml`.
- Publica os resultados **JUnit**.
- Publica o artefato `drop` com o `.jar`.

**O que o CD faz**
- Baixa o artefato.
- Aplica `DB_URL`, `DB_USER`, `DB_PASS` nas Application Settings do Web App.
- Faz o deploy com `AzureRmWebAppDeployment@4`.

---

## 8. Proteção da branch `main`

1. **Project settings** → **Repos** → **Repositories** → selecione `nexus-verde` → aba **Policies**.
2. Em **Branch policies**, selecione **main** e habilite:

| Política | Configuração |
| :--- | :--- |
| **Require a minimum number of reviewers** | 1 |
| **Default reviewers** | adicione `[seu-email]` (seu RM) e `[email-do-professor]` |
| **Prohibit the most recent pusher from approving their own changes** | **desmarque** (requisito 05: o aluno pode aprovar a própria PR) |
| **Check for linked work items** | **Required** (requisito 03) |
| **Check for comment resolution** | habilitado |
| **Merge types** | permita `Squash` ou `Merge` |

3. **Save**.


---

## 9. Simulação: branch, alteração no código, PR e merge

1. Crie a branch:
   ```bash
   git checkout -b feature/altera-status-solicitacao
   ```
2. Altere um **arquivo de código** (nunca o README). Exemplo: em
   `nexus-verde/src/main/java/com/example/nexusverde/entity/SolicitacaoAjuda.java`
   ajuste algo visível, ou em `SolicitacaoAjudaService.java` inclua um novo status padrão.
3. Commit referenciando a tarefa:
   ```bash
   git add .
   git commit -m "feat: ajusta status padrao da solicitacao AB#1001"
   git push -u origin feature/altera-status-solicitacao
   ```
4. No Azure DevOps → **Repos** → **Pull requests** → **New pull request**:
   - Source: `feature/altera-status-solicitacao`
   - Target: `main`
   - Vincule o work item `#1001` e adicione `[email-do-professor]` como revisor.
5. **Complete merge** (Squash). O pipeline dispara sozinho.

---

## 10. Validar o deploy e o CRUD em nuvem

**API**
```bash
BASE_URL="https://app-nexus-verde-rm566022.azurewebsites.net"

curl "$BASE_URL/api/regioes-afetadas"
```

**CRUD com os arquivos JSON**

Use os arquivos em `scripts/crud/` como fonte dos bodies. Exemplos:

```bash
# CREATE regiao
curl -X POST "$BASE_URL/api/regioes-afetadas" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Vale do Sinos","municipio":"Sao Leopoldo","uf":"RS","populacaoAfetada":8000,"nivelRisco":"ALTO","status":"ATIVA","latitude":-29.76,"longitude":-51.15}'

# CREATE ponto de agua
curl -X POST "$BASE_URL/api/pontos-agua" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Caixa Dagua Sao Leopoldo","endereco":"Rua Lindolfo Collor, 200","municipio":"Sao Leopoldo","uf":"RS","tipo":"CAIXA_DAGUA","capacidadeLitros":30000,"status":"DISPONIVEL","regiao":{"id":1}}'

# CREATE solicitacao
curl -X POST "$BASE_URL/api/solicitacoes-ajuda" \
  -H "Content-Type: application/json" \
  -d '{"nomeSolicitante":"Ana Pereira","contato":"(51) 99999-0003","descricao":"Comunidade sem agua potavel.","pessoasAfetadas":15,"prioridade":1,"regiao":{"id":2}}'

# UPDATE (use o id retornado)
curl -X PUT "$BASE_URL/api/regioes-afetadas/1" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Baixada Fluminense","municipio":"Nova Iguacu","uf":"RJ","populacaoAfetada":26000,"nivelRisco":"CRITICO","status":"ATIVA","latitude":-22.7596,"longitude":-43.4510}'

# DELETE (use o id retornado)
curl -X DELETE "$BASE_URL/api/regioes-afetadas/3"
```

**Comprovar persistência no banco (SELECT, não GET)**

1. Portal Azure → **SQL Server** → **Editor de consultas**.
2. Execute:
   ```sql
   SELECT * FROM regioes_afetadas;
   SELECT * FROM pontos_agua;
   SELECT * FROM solicitacoes_ajuda;
   ```

---

## 11. Artefatos e testes da pipeline

1. **Pipelines** → abra o run mais recente.
2. Na página do run, veja as duas stages: `Build_and_Test_CI` e `Deploy_CD`.
3. Aba **Summary** → **Related** → artefato **`drop`** com o `.jar`.
4. Aba **Tests** → resultados **JUnit** (testes de `RegiaoAfetadaServiceTest`, `PontoAguaServiceTest` e `SolicitacaoAjudaServiceTest`).
5. Baixe o artefato para comprovar o pacote.

---


