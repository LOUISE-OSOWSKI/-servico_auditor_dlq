#!/bin/bash

# Script de Testes com cURL
# Enviar mensagens para o SQS e validar processamento

set -e

# Configurações
SQS_ENDPOINT="http://localhost:4566"
QUEUE_NAME="servico-auditor-dlq-queue"
QUEUE_URL="$SQS_ENDPOINT/000000000000/$QUEUE_NAME"
REGION="us-east-1"
AWS_ACCESS_KEY_ID="test"
AWS_SECRET_ACCESS_KEY="test"

echo "🚀 Iniciando testes..."
echo "Queue URL: $QUEUE_URL"

# ============================================
# Teste 1: LOW Severity (total < 50)
# ============================================
echo ""
echo "📨 Teste 1: Enviando mensagem LOW (total = 45)..."

MESSAGE_LOW='{
  "zipCode": "80010000",
  "customerId": 1,
  "orderItems": [
    {"sku": 1, "amount": 20},
    {"sku": 2, "amount": 15},
    {"sku": 3, "amount": 10}
  ],
  "origin": "SQS_QUEUE",
  "occurredAt": "2024-05-20T14:30:00Z"
}'

aws sqs send-message \
  --queue-url "$QUEUE_URL" \
  --message-body "$MESSAGE_LOW" \
  --endpoint-url "$SQS_ENDPOINT" \
  --region "$REGION" \
  --profile default \
  2>/dev/null || echo "⚠️  Sem credenciais AWS configuradas"

echo "✅ Mensagem LOW enviada"

# ============================================
# Teste 2: MEDIUM Severity (50 <= total <= 100)
# ============================================
echo ""
echo "📨 Teste 2: Enviando mensagem MEDIUM (total = 75)..."

MESSAGE_MEDIUM='{
  "zipCode": "80010000",
  "customerId": 2,
  "orderItems": [
    {"sku": 101, "amount": 35},
    {"sku": 102, "amount": 25},
    {"sku": 103, "amount": 15}
  ],
  "origin": "SQS_QUEUE",
  "occurredAt": "2024-05-20T15:00:00Z"
}'

aws sqs send-message \
  --queue-url "$QUEUE_URL" \
  --message-body "$MESSAGE_MEDIUM" \
  --endpoint-url "$SQS_ENDPOINT" \
  --region "$REGION" \
  2>/dev/null || echo "⚠️  Sem credenciais AWS configuradas"

echo "✅ Mensagem MEDIUM enviada"

# ============================================
# Teste 3: HIGH Severity (total > 100)
# ============================================
echo ""
echo "📨 Teste 3: Enviando mensagem HIGH (total = 130)..."

MESSAGE_HIGH='{
  "zipCode": "80010000",
  "customerId": 3,
  "orderItems": [
    {"sku": 201, "amount": 65},
    {"sku": 202, "amount": 40},
    {"sku": 203, "amount": 25}
  ],
  "origin": "SQS_QUEUE",
  "occurredAt": "2024-05-20T16:00:00Z"
}'

aws sqs send-message \
  --queue-url "$QUEUE_URL" \
  --message-body "$MESSAGE_HIGH" \
  --endpoint-url "$SQS_ENDPOINT" \
  --region "$REGION" \
  2>/dev/null || echo "⚠️  Sem credenciais AWS configuradas"

echo "✅ Mensagem HIGH enviada"

# ============================================
# Teste 4: Mensagem com erro (JSON inválido)
# ============================================
echo ""
echo "📨 Teste 4: Enviando mensagem com erro..."

MESSAGE_ERROR='{"zipCode": "invalid json}'

aws sqs send-message \
  --queue-url "$QUEUE_URL" \
  --message-body "$MESSAGE_ERROR" \
  --endpoint-url "$SQS_ENDPOINT" \
  --region "$REGION" \
  2>/dev/null || echo "⚠️  Sem credenciais AWS configuradas"

echo "✅ Mensagem com erro enviada"

# ============================================
# Verificar fila
# ============================================
echo ""
echo "📊 Verificando fila..."

QUEUE_ATTRIBUTES=$(aws sqs get-queue-attributes \
  --queue-url "$QUEUE_URL" \
  --attribute-names ApproximateNumberOfMessages \
  --endpoint-url "$SQS_ENDPOINT" \
  --region "$REGION" \
  2>/dev/null || echo '{}')

if [ "$QUEUE_ATTRIBUTES" != "{}" ]; then
    echo "📈 Mensagens na fila: $QUEUE_ATTRIBUTES"
else
    echo "⚠️  Não foi possível conectar ao SQS"
fi

# ============================================
# Aguardar processamento
# ============================================
echo ""
echo "⏳ Aguardando processamento (5 segundos)..."
sleep 5

echo ""
echo "✨ Testes concluídos!"
echo ""
echo "📝 Próximos passos:"
echo "1. Acesse http://localhost:8080/h2-console"
echo "2. Execute: SELECT * FROM AUDIT_MESSAGES;"
echo "3. Verifique os registros com severidades corretas"
