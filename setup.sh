#!/bin/bash

# Script de Setup Completo
# Executa todas as etapas necessárias para rodar a aplicação

set -e

echo "🚀 Setup Completo - servico-auditor-dlq"
echo "========================================"
echo ""

# ============================================
# 1. Verificar Pré-requisitos
# ============================================
echo "✓ Verificando pré-requisitos..."

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado. Instale Java 21+"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1)
echo "   Java: $JAVA_VERSION"

# Verificar Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não encontrado. Instale Maven 3.9+"
    exit 1
fi

MVN_VERSION=$(mvn -v | head -1)
echo "   Maven: $MVN_VERSION"

# Verificar Docker
if ! command -v docker &> /dev/null; then
    echo "⚠️  Docker não encontrado. Pule infraestrutura local."
else
    echo "   Docker: ✓"
fi

echo ""

# ============================================
# 2. Iniciar Infraestrutura (LocalStack)
# ============================================
echo "🐳 Iniciando infraestrutura..."

if command -v docker &> /dev/null; then
    docker-compose up -d
    echo "   LocalStack: iniciado em http://localhost:4566"
    echo "   H2 Console: iniciado em http://localhost:8082"
    sleep 3
else
    echo "   ⚠️  Docker não disponível. Pule para testes."
fi

echo ""

# ============================================
# 3. Build do Projeto
# ============================================
echo "🔨 Compilando projeto..."

mvn clean install -DskipTests

echo "   ✓ Build concluído"
echo ""

# ============================================
# 4. Executar Testes
# ============================================
echo "🧪 Executando testes..."

mvn test

echo "   ✓ Testes passaram"
echo ""

# ============================================
# 5. Resumo Final
# ============================================
echo "✨ Setup Completo!"
echo "========================================"
echo ""
echo "📝 Próximos passos:"
echo ""
echo "1️⃣  Iniciar aplicação:"
echo "    mvn spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=local'"
echo ""
echo "2️⃣  Em outro terminal, enviar mensagens de teste:"
echo "    ./test-messages.sh"
echo ""
echo "3️⃣  Acessar H2 Console:"
echo "    http://localhost:8080/h2-console"
echo ""
echo "4️⃣  Consultar dados:"
echo "    SELECT * FROM AUDIT_MESSAGES ORDER BY TIMESTAMP DESC;"
echo ""
echo "📚 Documentação:"
echo "    - README.md (visão geral)"
echo "    - ARCHITECTURE.md (arquitetura hexagonal)"
echo "    - TESTING.md (guia de testes)"
echo "    - SQL_QUERIES.md (queries úteis)"
echo ""
