# Nexus Verde — Tecnologia para garantir acesso à água em situações de crise

## Integrantes
- Carlos Alberto Guedes Neto RM: 566022
- Eduardo Novaes Mollo RM: 561515
- Vinícius L. E. M. Garcia RM: 563340
- Luan Peixoto Marins Rocha RM: 562258
- Mathaus Victor Souza Marcelino RM: 564146

NexusVerde

---

## Descrição da Solução

O **Nexus Verde** é uma plataforma backend (API RESTful) desenvolvida em **Java com Spring Boot** para facilitar o acesso à água potável no Brasil, principalmente em situações de emergência, como enchentes, secas e outros desastres naturais.

Apesar de o Brasil possuir grandes reservas de água doce, milhões de brasileiros enfrentam dificuldades para acessar água potável, especialmente em regiões vulneráveis e durante desastres. Um exemplo são as enchentes no Rio Grande do Sul, que demonstraram como uma catástrofe pode comprometer sistemas de abastecimento, contaminar fontes de água e deixar comunidades inteiras sem acesso a um recurso essencial para a sobrevivência.

O problema não é apenas a falta de água, mas também a dificuldade de identificar onde ela está disponível, quais locais precisam de ajuda e como distribuir os recursos de maneira eficiente.

A aplicação conecta cidadãos, órgãos públicos, equipes de resgate e organizações humanitárias em um único sistema, permitindo identificar regiões afetadas, monitorar a disponibilidade de água e organizar sua distribuição.

---

## Benefícios para o Negócio

- **Resposta rápida a desastres:** o mapeamento de regiões afetadas e de pontos de água potável reduz o tempo entre a detecção do problema e o envio de ajuda.
- **Priorização inteligente:** a análise de demanda, disponibilidade e criticidade apoia a priorização das regiões mais necessitadas.
- **Fim do retrabalho e da informação fragmentada:** substitui ligações, planilhas e mensagens dispersas por um banco relacional **Azure SQL Database** centralizado e acessível com alta disponibilidade.
- **Transparência e prestação de contas:** cada solicitação de ajuda possui protocolo e status rastreáveis, do recebimento ao atendimento.
- **Automação e qualidade com CI/CD:** a integração contínua garante que os testes automáticos passem antes de gerar o artefato, e a entrega contínua faz o deploy automático no **Azure Web App**, eliminando erros manuais de publicação.

---


## Infraestrutura (Azure CLI)

Os recursos em nuvem são criados por script, conforme exigido na entrega:

- `scripts/azure-infra.sh` — Resource Group, SQL Server, SQL Database, App Service Plan e Web App (Java 17), além de configurar as variáveis de ambiente `DB_URL`, `DB_USER` e `DB_PASS` no Web App.
- `scripts/script-bd.sql` — DDL das tabelas e inserts iniciais.

```bash
az login
export SQL_ADMIN_PASSWORD='fiap#2026'
bash scripts/azure-infra.sh
```

Aplique o DDL do banco com `scripts/script-bd.sql` no **Editor de consultas** do SQL Server no portal Azure.

---

## Arquitetura em Nuvem e Esteira CI/CD (Azure DevOps)

O arquivo de pipeline está na **raiz do repositório**: `azure-pipeline.yml`.

1. **Repositório:** GitHub/Azure Repos integrados ao Azure DevOps.
2. **Esteira de CI (`Build_and_Test_CI`):**
   - Configura a JDK 17 na máquina virtual remota (`ubuntu-latest`).
   - Executa `mvn clean test package` em `nexus-verde/pom.xml`.
   - Publica os resultados de testes JUnit.
   - Publica o artefato (`drop`) no Azure DevOps.
3. **Esteira de CD (`Deploy_CD`):**
   - Disparada automaticamente após o sucesso da etapa de CI.
   - Baixa o artefato `.jar`.
   - Aplica as variáveis de ambiente (`DB_URL`, `DB_USER`, `DB_PASS`) nas **Application Settings** do Azure Web App.
   - Faz o deploy automático no **Azure App Service** com a task `AzureRmWebAppDeployment@4`.
4. **Variáveis de ambiente:** ficam em um **grupo de variáveis** do Azure Pipelines (`nexus-verde-vars`), referenciado no YAML, e nas **Application Settings** do Web App. Nenhuma senha fica no código.

---

## Endpoints da API e Conexões

- **Aplicação Web (API Base):** `app-nexus-verde-rm566022` (Azure Web App; a URL final é `https://app-nexus-verde-rm566022.azurewebsites.net` após o deploy)
- **Rotas de Regiões Afetadas:** `/api/regioes-afetadas` (GET, POST, PUT, DELETE)
- **Rotas de Pontos de Água:** `/api/pontos-agua` (GET, POST, PUT, DELETE)
- **Rotas de Solicitações de Ajuda:** `/api/solicitacoes-ajuda` (GET, POST, PUT, DELETE)

### Tabela de Rotas de Regiões Afetadas

| Verbo HTTP | Endpoint | Descrição | Exemplo de Body (JSON) |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/regioes-afetadas` | Listar todas as regiões afetadas | - |
| **GET** | `/api/regioes-afetadas/{id}` | Buscar região por ID | - |
| **POST** | `/api/regioes-afetadas` | Cadastrar uma nova região | `{"nome": "Baixada Fluminense", "municipio": "Nova Iguacu", "uf": "RJ", "populacaoAfetada": 25000, "nivelRisco": "ALTO", "status": "ATIVA", "latitude": -22.7596, "longitude": -43.4510}` |
| **PUT** | `/api/regioes-afetadas/{id}` | Atualizar uma região existente | `{"nome": "Baixada Fluminense", "municipio": "Nova Iguacu", "uf": "RJ", "populacaoAfetada": 26000, "nivelRisco": "CRITICO", "status": "ATIVA", "latitude": -22.7596, "longitude": -43.4510}` |
| **DELETE** | `/api/regioes-afetadas/{id}` | Remover uma região por ID | - |

### Tabela de Rotas de Pontos de Água

| Verbo HTTP | Endpoint | Descrição | Exemplo de Body (JSON) |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/pontos-agua` | Listar todos os pontos de água | - |
| **GET** | `/api/pontos-agua/{id}` | Buscar ponto de água por ID | - |
| **POST** | `/api/pontos-agua` | Cadastrar um novo ponto de água | `{"nome": "Caixa Dagua Central", "endereco": "Praca Central, s/n", "municipio": "Nova Iguacu", "uf": "RJ", "tipo": "CAIXA_DAGUA", "capacidadeLitros": 50000, "status": "DISPONIVEL", "regiao": {"id": 1}}` |
| **PUT** | `/api/pontos-agua/{id}` | Atualizar um ponto de água existente | `{"nome": "Caixa Dagua Central", "endereco": "Praca Central, s/n", "municipio": "Nova Iguacu", "uf": "RJ", "tipo": "CAIXA_DAGUA", "capacidadeLitros": 40000, "status": "DISPONIVEL", "regiao": {"id": 1}}` |
| **DELETE** | `/api/pontos-agua/{id}` | Remover um ponto de água por ID | - |

### Tabela de Rotas de Solicitações de Ajuda

| Verbo HTTP | Endpoint | Descrição | Exemplo de Body (JSON) |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/solicitacoes-ajuda` | Listar todas as solicitações | - |
| **GET** | `/api/solicitacoes-ajuda/{id}` | Buscar solicitação por ID | - |
| **POST** | `/api/solicitacoes-ajuda` | Registrar uma nova solicitação | `{"nomeSolicitante": "Maria Silva", "contato": "(21) 99999-0001", "descricao": "Falta de agua ha 3 dias.", "pessoasAfetadas": 4, "prioridade": 1, "regiao": {"id": 1}}` |
| **PUT** | `/api/solicitacoes-ajuda/{id}` | Atualizar uma solicitação existente | `{"protocolo": "NV-2026-0001", "nomeSolicitante": "Maria Silva", "contato": "(21) 99999-0001", "descricao": "Falta de agua ha 3 dias.", "pessoasAfetadas": 4, "prioridade": 1, "status": "EM_ATENDIMENTO", "regiao": {"id": 1}}` |
| **DELETE** | `/api/solicitacoes-ajuda/{id}` | Remover uma solicitação por ID | - |

---

## Executando Localmente

Para rodar a aplicação na sua máquina:

**Opção 1 - SQL Server:**
- Instale o SQL Server Express e crie o banco `sprint4-nexus-verde`.
- Execute `scripts/script-bd.sql` no banco.
- Configure `application.properties` com sua conexão JDBC.
- Rode: `cd nexus-verde && ./mvnw spring-boot:run`.

**Opção 2 - H2 (sem instalação):**
- Rode: `cd nexus-verde && ./mvnw spring-boot:run -Dspring.profiles.active=h2`.
- Acesse a console H2 em `http://localhost:8080/h2-console`.

**Opção 3 - Apenas testes unitários:**
- Rode: `./mvnw test`.

---



