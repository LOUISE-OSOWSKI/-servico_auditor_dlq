# ServiÃ§o de Auditoria e Triagem de DLQ

## ðŸ“ Contexto do Projeto

Em arquiteturas orientadas a eventos, a **DLQ (Dead Letter Queue)** atua como uma rede de seguranÃ§a para mensagens que falharam repetidamente no fluxo principal. Contudo, deixar essas mensagens acumuladas na fila gera problemas operacionais: elas possuem tempo de vida limitado (TTL) e expiram, alÃ©m de ser inviÃ¡vel realizar anÃ¡lises de causa raiz diretamente em filas de mensagens brutas.

Outro fator crÃ­tico Ã© a falta de priorizaÃ§Ã£o: a fila trata todas as falhas com o mesmo peso. No entanto, um erro em um pedido de 1.000 itens possui um impacto financeiro muito maior do que uma falha em um pedido de apenas 1 item.

Para resolver isso, este serviÃ§o de apoio foi desenvolvido de forma independente. Ele consome as mensagens da DLQ, aplica uma **regra de triagem de severidade** com base no volume de itens afetados e persiste esses dados estruturados em um banco de dados, permitindo que a equipe de desenvolvimento analise e catalogue os erros com eficiÃªncia.

---

## ðŸ›ï¸ DecisÃ£o Arquitetural: Arquitetura Hexagonal

A escolha da **Arquitetura Hexagonal (Ports & Adapters)** para este microsserviÃ§o baseou-se em boas prÃ¡ticas de design de software, priorizando o isolamento do negÃ³cio e a testabilidade.

### 1. Isolamento das Regras de NegÃ³cio (DomÃ­nio Puro)
O nÃºcleo do sistema Ã© a lÃ³gica que calcula a severidade do erro (`HIGH`, `MEDIUM` ou `LOW`) com base na quantidade total de produtos. Utilizando o padrÃ£o hexagonal, essa inteligÃªncia fica centralizada no pacote `domain`. 
Essas classes sÃ£o Java puro (POJOs), totalmente livres de anotaÃ§Ãµes do Spring Boot, Hibernate ou dependÃªncias da AWS. Isso garante que a regra de triagem possa ser testada unitariamente de forma instantÃ¢nea, sem a necessidade de subir bancos de dados ou simular infraestrutura de mensageria.

### 2. Desacoplamento e Substituibilidade de Tecnologias
Como este serviÃ§o atua essencialmente como um integrador (consome dados de uma ponta e persiste na outra), os detalhes tecnolÃ³gicos externos podem mudar ao longo do tempo. Se no futuro for necessÃ¡rio substituir o **AWS SQS** pelo **Apache Kafka**, ou migrar o banco de dados **H2** para um repositÃ³rio NoSQL como o **MongoDB**, o nÃºcleo do negÃ³cio (a triagem) permanecerÃ¡ intacto. BastarÃ¡ apenas implementar novos adaptadores.

### 3. OrganizaÃ§Ã£o dos Pacotes
O projeto foi estruturado seguindo rigorosamente a separaÃ§Ã£o de responsabilidades:

* `domain/`: ContÃ©m os modelos de negÃ³cio (`AuditLog`, `Severity`) e o serviÃ§o que orquestra as regras de triagem.
* `ports/`: Define as interfaces (contratos de entrada e saÃ­da) que determinam como o mundo externo interage com o domÃ­nio e como o domÃ­nio exporta dados para a persistÃªncia.
* `adapters/inbound/sqs/`: Camada de infraestrutura responsÃ¡vel por escutar a fila da AWS (`@SqsListener`), realizar a desserializaÃ§Ã£o do JSON e direcionar os dados para o porto de entrada.
* `adapters/outbound/persistence/`: ResponsÃ¡vel por traduzir os modelos de domÃ­nio em entidades JPA e realizar a persistÃªncia no banco de dados relacional.

### 4. ResiliÃªncia e Garantia de Entrega
O processamento da fila foi desenhado para garantir que nenhuma mensagem seja perdida em caso de falha de infraestrutura. A confirmaÃ§Ã£o de leitura (Acknowledge) para o SQS sÃ³ ocorre apÃ³s o banco de dados confirmar o salvamento seguro do registro. Se o banco estiver indisponÃ­vel, uma exceÃ§Ã£o Ã© lanÃ§ada, forÃ§ando o Spring Cloud AWS a manter a mensagem original protegida dentro da DLQ para uma nova tentativa automÃ¡tica.

---

## ðŸ” Como manter o projeto seguro para commit

Este projeto nÃ£o deve armazenar chaves secretas em arquivos versionados.

### OpÃ§Ã£o recomendada: variÃ¡veis de ambiente
Defina estas variÃ¡veis antes de rodar o serviÃ§o:

```powershell
$env:AWS_ACCESS_KEY_ID="YOUR_AWS_ACCESS_KEY_ID"
$env:AWS_SECRET_ACCESS_KEY="YOUR_AWS_SECRET_ACCESS_KEY"
$env:AWS_REGION="us-east-1"
```

ou em CMD:

```cmd
set AWS_ACCESS_KEY_ID=YOUR_AWS_ACCESS_KEY_ID
set AWS_SECRET_ACCESS_KEY=YOUR_AWS_SECRET_ACCESS_KEY
set AWS_REGION=us-east-1
```

### OpÃ§Ã£o alternativa: arquivo local ignorado pelo Git
Copie o arquivo de exemplo `application-secrets.example.yml` para `application-secrets.yml` e mantenha-o fora do controle de versÃ£o.

O arquivo `application.yml` jÃ¡ estÃ¡ configurado para importar `application-secrets.yml` opcionalmente.

---
Desenvolvido por Louise
