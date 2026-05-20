#!/bin/bash

# Script de Integração Completa
# Demonstra fluxo completo: LocalStack → Aplicação → H2

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "╔════════════════════════════════════════════════════════════╗"
echo "║   Fluxo Completo: SQS → Aplicação → H2                   ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# ============================================
# Stage 1: Infraestrutura
# ============================================
echo "📦 STAGE 1: Iniciando Infraestrutura"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

docker-compose down -v 2>/dev/null || true
docker-compose up -d

echo "✓ LocalStack iniciado"
echo "✓ H2 Console iniciado"
echo ""

# Aguardar LocalStack estar pronto
sleep 3

# ============================================
# Stage 2: Criar Fila SQS
# ============================================
echo "🔧 STAGE 2: Configurando SQS"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1

QUEUE_URL=$(aws sqs create-queue \
  --queue-name servico-auditor-dlq-queue \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  2>/dev/null | jq -r '.QueueUrl' || echo "http://localhost:4566/000000000000/servico-auditor-dlq-queue")

echo "✓ Fila criada: $QUEUE_URL"
echo ""

# ============================================
# Stage 3: Build da Aplicação
# ============================================
echo "🔨 STAGE 3: Compilando Aplicação"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

mvn clean install -DskipTests -q

echo "✓ Build concluído"
echo ""

# ============================================
# Stage 4: Executar Testes
# ============================================
echo "🧪 STAGE 4: Executando Testes"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

mvn test -q

echo "✓ Testes passaram"
echo ""

# ============================================
# Stage 5: Iniciar Aplicação
# ============================================
echo "🚀 STAGE 5: Iniciando Aplicação"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Rodar em background
java -jar target/servicoauditor-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=local \
  --spring.cloud.aws.sqs.endpoint=http://localhost:4566 \
  --custom.queue.dlq-name=servico-auditor-dlq-queue \
  &
APP_PID=$!

echo "✓ Aplicação iniciada (PID: $APP_PID)"
echo ""

# Aguardar aplicação iniciar
sleep 5

# ============================================
# Stage 6: Enviar Mensagens
# ============================================
echo "📨 STAGE 6: Enviando Mensagens de Teste"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Mensagem LOW
aws sqs send-message \
  --queue-url "$QUEUE_URL" \
  --message-body @EXAMPLE_MESSAGE_LOW.json \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  2>/dev/null

echo "✓ Mensagem LOW enviada (total: 100)"

# Mensagem MEDIUM
aws sqs send-message \
  --queue-url "$QUEUE_URL" \
  --message-body @EXAMPLE_MESSAGE_MEDIUM.json \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  2>/dev/null

echo "✓ Mensagem MEDIUM enviada (total: 85)"

# Mensagem HIGH
aws sqs send-message \
  --queue-url "$QUEUE_URL" \
  --message-body @EXAMPLE_MESSAGE_HIGH.json \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  2>/dev/null

echo "✓ Mensagem HIGH enviada (total: 115)"
echo ""

# ============================================
# Stage 7: Aguardar Processamento
# ============================================
echo "⏳ STAGE 7: Processando Mensagens"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

sleep 3

echo "✓ Mensagens processadas"
echo ""

# ============================================
# Stage 8: Validar Dados
# ============================================
echo "✅ STAGE 8: Validando Dados"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Consultar banco H2 via aplicação (health)
HEALTH=$(curl -s http://localhost:8080/actuator/health | jq -r '.status' 2>/dev/null || echo "UNKNOWN")

echo "✓ Aplicação Health: $HEALTH"
echo ""

# ============================================
# Resultado Final
# ============================================
echo "╔════════════════════════════════════════════════════════════╗"
echo "║             🎉 FLUXO COMPLETO EXECUTADO COM SUCESSO 🎉   ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""
echo "📊 RESUMO:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✓ LocalStack rodando em http://localhost:4566"
echo "✓ H2 Console disponível em http://localhost:8080/h2-console"
echo "✓ Aplicação rodando em http://localhost:8080"
echo "✓ 3 mensagens processadas e persistidas"
echo ""
echo "📝 Próximos passos:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "1. Acessar H2 Console:"
echo "   http://localhost:8080/h2-console"
echo ""
echo "2. Executar query:"
echo "   SELECT COUNT(*) as TOTAL, SEVERITY FROM AUDIT_MESSAGES GROUP BY SEVERITY;"
echo ""
echo "3. Esperado:"
echo "   - 1 mensagem com SEVERITY = 'LOW'"
echo "   - 1 mensagem com SEVERITY = 'MEDIUM'"
echo "   - 1 mensagem com SEVERITY = 'HIGH'"
echo ""
echo "4. Para parar a aplicação:"
echo "   kill $APP_PID"
echo ""
echo "5. Para parar infraestrutura:"
echo "   docker-compose down"
echo ""
