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

### 🚀 Como rodar o projeto

#### Clonando o repositório
```bash
git clone https://https://github.com/jrpbjr/order
cd seu-repositorio
