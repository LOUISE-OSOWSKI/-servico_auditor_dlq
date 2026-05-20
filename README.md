Serviço de Auditoria e Triagem de DLQ

Contexto do Projeto

Em arquiteturas orientadas a eventos, a **DLQ (Dead Letter Queue)** atua como uma rede de segurança para mensagens que falharam repetidamente no fluxo principal. Contudo, deixar essas mensagens acumuladas na fila gera problemas operacionais: elas possuem tempo de vida limitado (TTL) e expiram, além de ser inviável realizar análises de causa raiz diretamente em filas de mensagens brutas.

Outro fator crítico é a falta de priorização: a fila trata todas as falhas com o mesmo peso. No entanto, um erro em um pedido de 1.000 itens possui um impacto financeiro muito maior do que uma falha em um pedido de apenas 1 item.

Para resolver isso, este serviço de apoio foi desenvolvido de forma independente. Ele consome as mensagens da DLQ, aplica uma **regra de triagem de severidade** com base no volume de itens afetados e persiste esses dados estruturados em um banco de dados, permitindo que a equipe de desenvolvimento analise e catalogue os erros com eficiência.

---

Decisão Arquitetural: Arquitetura Hexagonal

A escolha da **Arquitetura Hexagonal (Ports & Adapters)** para este microsserviço baseou-se em boas práticas de design de software, priorizando o isolamento do negócio e a testabilidade.

### 1. Isolamento das Regras de Negócio (Domínio Puro)
O núcleo do sistema é a lógica que calcula a severidade do erro (`HIGH`, `MEDIUM` ou `LOW`) com base na quantidade total de produtos. Utilizando o padrão hexagonal, essa inteligência fica centralizada no pacote `domain`. 
Essas classes são Java puro (POJOs), totalmente livres de anotações do Spring Boot, Hibernate ou dependências da AWS. Isso garante que a regra de triagem possa ser testada unitariamente de forma instantânea, sem a necessidade de subir bancos de dados ou simular infraestrutura de mensageria.

### 2. Desacoplamento e Substituibilidade de Tecnologias
Como este serviço atua essencialmente como um integrador (consome dados de uma ponta e persiste na outra), os detalhes tecnológicos externos podem mudar ao longo do tempo. Se no futuro for necessário substituir o **AWS SQS** pelo **Apache Kafka**, ou migrar o banco de dados **H2** para um repositório NoSQL como o **MongoDB**, o núcleo do negócio (a triagem) permanecerá intacto. Bastará apenas implementar novos adaptadores.

### 3. Organização dos Pacotes
O projeto foi estruturado seguindo rigorosamente a separação de responsabilidades:

* `domain/`: Contém os modelos de negócio (`AuditLog`, `Severity`) e o serviço que orquestra as regras de triagem.
* `ports/`: Define as interfaces (contratos de entrada e saída) que determinam como o mundo externo interage com o domínio e como o domínio exporta dados para a persistência.
* `adapters/inbound/sqs/`: Camada de infraestrutura responsável por escutar a fila da AWS (`@SqsListener`), realizar a desserialização do JSON e direcionar os dados para o porto de entrada.
* `adapters/outbound/persistence/`: Responsável por traduzir os modelos de domínio em entidades JPA e realizar a persistência no banco de dados relacional.

