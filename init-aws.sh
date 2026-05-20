#!/bin/bash

# Inicializar fila DLQ no LocalStack
echo "Criando fila DLQ no SQS..."

awslocal sqs create-queue --queue-name servico-auditor-dlq-queue --region us-east-1

echo "Fila criada com sucesso!"
