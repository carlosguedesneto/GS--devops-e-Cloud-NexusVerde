#!/usr/bin/env bash
#
# Provisionamento da infraestrutura do Nexus Verde no Azure via Azure CLI.
#
# O script e idempotente: recursos que ja existem sao reutilizados.
#
# Uso:
#   az login
#   export SQL_ADMIN_PASSWORD='fiap#2026'
#   bash scripts/azure-infra.sh
#
# Para usar nomes diferentes (ex.: server novo para nao conflitar com outro projeto):
#   export SQL_SERVER='nexus-verde-db'
#   export WEBAPP_NAME='app-nexus-verde-rm566022'
#
set -euo pipefail

# ---------- PARAMETROS (ajuste se necessario) ----------
SUBSCRIPTION_ID="${SUBSCRIPTION_ID:-}"
RESOURCE_GROUP="${RESOURCE_GROUP:-rg-nexus-verde}"
LOCATION="${LOCATION:-canadacentral}"
SQL_SERVER="${SQL_SERVER:-sprint4-db}"
SQL_DB="${SQL_DB:-sprint4-nexus-verde}"
SQL_ADMIN_USER="${SQL_ADMIN_USER:-fiapadmin}"
SQL_ADMIN_PASSWORD="${SQL_ADMIN_PASSWORD:-}"
APP_SERVICE_PLAN="${APP_SERVICE_PLAN:-plan-nexus-verde}"
WEBAPP_NAME="${WEBAPP_NAME:-app-nexus-verde}"
RUNTIME="${RUNTIME:-JAVA:17-java17}"
# ------------------------------------------------------

if [[ -z "$SQL_ADMIN_PASSWORD" ]]; then
  echo "ERRO: defina a variavel SQL_ADMIN_PASSWORD antes de rodar." >&2
  exit 1
fi

if [[ -n "$SUBSCRIPTION_ID" ]]; then
  az account set --subscription "$SUBSCRIPTION_ID"
fi

echo ">> Resource Group: $RESOURCE_GROUP"
if [[ "$(az group exists --name "$RESOURCE_GROUP")" == "true" ]]; then
  echo "   ja existe, reutilizando."
else
  az group create --name "$RESOURCE_GROUP" --location "$LOCATION" --output table
fi

echo ">> SQL Server: $SQL_SERVER"
if az sql server show --name "$SQL_SERVER" --resource-group "$RESOURCE_GROUP" --query id -o tsv >/dev/null 2>&1; then
  echo "   ja existe, reutilizando."
else
  az sql server create \
    --name "$SQL_SERVER" \
    --resource-group "$RESOURCE_GROUP" \
    --location "$LOCATION" \
    --admin-user "$SQL_ADMIN_USER" \
    --admin-password "$SQL_ADMIN_PASSWORD" \
    --output table
fi

echo ">> Firewall (permitir servicos do Azure)"
if az sql server firewall-rule show --name AllowAzureServices --server "$SQL_SERVER" --resource-group "$RESOURCE_GROUP" --query id -o tsv >/dev/null 2>&1; then
  echo "   regra ja existe, reutilizando."
else
  az sql server firewall-rule create \
    --name AllowAzureServices \
    --server "$SQL_SERVER" \
    --resource-group "$RESOURCE_GROUP" \
    --start-ip-address 0.0.0.0 \
    --end-ip-address 0.0.0.0 \
    --output table
fi

echo ">> SQL Database: $SQL_DB"
if az sql db show --name "$SQL_DB" --server "$SQL_SERVER" --resource-group "$RESOURCE_GROUP" --query id -o tsv >/dev/null 2>&1; then
  echo "   ja existe, reutilizando."
else
  az sql db create \
    --name "$SQL_DB" \
    --server "$SQL_SERVER" \
    --resource-group "$RESOURCE_GROUP" \
    --service-objective S0 \
    --output table
fi

echo ">> App Service Plan: $APP_SERVICE_PLAN"
if az appservice plan show --name "$APP_SERVICE_PLAN" --resource-group "$RESOURCE_GROUP" --query id -o tsv >/dev/null 2>&1; then
  echo "   ja existe, reutilizando."
else
  az appservice plan create \
    --name "$APP_SERVICE_PLAN" \
    --resource-group "$RESOURCE_GROUP" \
    --location "$LOCATION" \
    --sku B1 \
    --is-linux \
    --output table
fi

echo ">> Web App: $WEBAPP_NAME"
if az webapp show --name "$WEBAPP_NAME" --resource-group "$RESOURCE_GROUP" --query id -o tsv >/dev/null 2>&1; then
  echo "   ja existe, reutilizando."
else
  az webapp create \
    --name "$WEBAPP_NAME" \
    --resource-group "$RESOURCE_GROUP" \
    --plan "$APP_SERVICE_PLAN" \
    --runtime "$RUNTIME" \
    --output table
fi

CONNECTION_STRING="jdbc:sqlserver://${SQL_SERVER}.database.windows.net:1433;database=${SQL_DB};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

echo ">> Application Settings do Web App"
az webapp config appsettings set \
  --name "$WEBAPP_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --settings DB_URL="$CONNECTION_STRING" DB_USER="$SQL_ADMIN_USER" DB_PASS="$SQL_ADMIN_PASSWORD" \
  --output table

echo
echo "=== INFRA PRONTA ==="
echo "Resource Group : $RESOURCE_GROUP"
echo "SQL Server     : $SQL_SERVER.database.windows.net"
echo "SQL Database   : $SQL_DB"
echo "Web App        : $WEBAPP_NAME"
echo "URL            : https://${WEBAPP_NAME}.azurewebsites.net"
echo
echo "DB_URL=$CONNECTION_STRING"
echo "DB_USER=$SQL_ADMIN_USER"
echo "DB_PASS=$SQL_ADMIN_PASSWORD"
echo
echo "Aplique o DDL do banco com: scripts/script-bd.sql"
