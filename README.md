# 🚀 Gerenciamento de Pedidos

## 📌 Descrição

Este projeto é um serviço de gerenciamento de pedidos chamado **"order"**, responsável por:

- **Receber pedidos** de um produto externo (**Produto A**).
- **Calcular o valor total** de cada pedido com base nos produtos contidos nele.
- **Armazenar pedidos e produtos** no banco de dados MongoDB.
- **Disponibilizar os pedidos processados** para um outro produto externo (**Produto B**).
- **Oferecer um endpoint de consulta** para verificar o status dos pedidos.

O sistema precisa suportar **150 mil a 200 mil pedidos por dia**, garantindo alta disponibilidade e escalabilidade.

---

## 🛠️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot**
- **MongoDB**
- **RabbitMQ**
- **Docker (Opcional para ambientes locais)**

---

## 🔧 Configuração e Instalação

### Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalados:

- **Java 17**
- **Maven**
- **Docker e Docker Compose** (opcional)
- **MongoDB**
- **RabbitMQ**
- **JaCoCo (para medir a cobertura dos testes unitários)**

### 🚀 Como rodar o projeto

#### Clonando o repositório
```bash
git clone https://https://github.com/jrpbjr/order
cd order

Executando os testes e gerando o relatório de cobertura JaCoCo

Para rodar os testes unitários e gerar o relatório de cobertura com o JaCoCo, utilize o seguinte comando:

mvn clean verify

O relatório será gerado no diretório:
target/site/jacoco/index.html

Para visualizar o relatório, abra o arquivo index.html em seu navegador.

📊 Monitorando a Evolução dos Testes Unitários

A cobertura de testes é uma métrica importante para garantir a qualidade do software. O JaCoCo permite acompanhar a evolução dos testes unitários ao longo do tempo. Algumas boas práticas incluem:

Garantir pelo menos 80% de cobertura nos módulos críticos do sistema.

Monitorar a cobertura de linhas, métodos e classes.

Utilizar ferramentas como SonarQube para integrar a cobertura ao pipeline CI/CD.

Definir um critério mínimo de cobertura para aprovação de PRs no repositório.

Para analisar a cobertura em detalhes, consulte os arquivos gerados no diretório target/site/jacoco/.


📌 Considerações Finais

Este serviço foi projetado para lidar com alto volume de pedidos e garantir escalabilidade utilizando MongoDB e RabbitMQ. 
Para melhorias futuras, podem ser adicionadas estratégias de caching, balanceamento de carga e processamento assíncrono 
otimizado.

A implementação de testes unitários e o uso do JaCoCo ajudarão a garantir a qualidade e a evolução contínua do projeto.