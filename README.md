🏛️ Central de Mandados

Sistema desenvolvido em Java (Maven) com MVC, banco de dados MySQL e interface gráfica Swing, voltado para o Tribunal de Justiça de Parintins/AM.
Permite cadastrar, listar, distribuir e controlar o status de mandados judiciais, além de gerenciar oficiais de justiça e seus respectivos registros.

---

## 📌 Funcionalidades Principais

- 🔐 **Cadastro, exclusão, alteração e login de usuários** (com controle de permissões)  
- 📁 **Cadastro, alteração e exclusão de Mandados**  
- 🔄 **Cadastro e exclusão de número de Processo**  
- 👮 **Cadastro e exclusão de Oficiais de Justiça**  
- 📤 **Distribuição manual dos mandados**  
- 📋 **Listagem completa com filtros (por status, oficial, processo, mandados e data)**  
- 🔎 **Consulta de mandados pendentes para distribuição**    
- 🔗 **Vinculação do mandado ao número do processo e oficial**  
- 📝 **Anotações internas nos mandados**  
- 📅 **Registro da data de cumprimento e distribuição e oficial responsável**

---

## 🛠️ Tecnologias Utilizadas

- Java 17+
- Maven
- NetBeans 15+
- MySQL 8
- JDBC (mysql-connector-j)
- Swing (GUI)

---

## 🗄️ Banco de Dados

O repositório contém o arquivo:

📄 centralmandados.sql

Ele inclui:
- Criação das tabelas
- Relacionamentos
- Inserções iniciais

## 🚀 Como Executar o Projeto

1. Clonar o repositório
  - git clone https://github.com/larissamonsalve/CentralMandados.git

2. Importar no NetBeans
  - Abra File > Open Project
  - Selecione a pasta do projeto recém-clonado
  - Aguarde o NetBeans carregar as dependências do Maven

3. Configurar o banco de dados
   - Crie um schema no MySQL
   - Importe o arquivo centralmandados.sql
   - Atualize as credenciais no arquivo de conexão (ex.: ConnectionFactory.java)

4. Executar
  - Clique no botão Run Project (▶) no NetBeans.

## 🔒 Licença

Este projeto não possui licença pública.
Todo o código está protegido por Copyright – All Rights Reserved.

Isso significa que:
❌ O código não pode ser copiado
❌ O código não pode ser modificado
❌ O código não pode ser redistribuído
❌ O código não pode ser reutilizado

Qualquer uso exige autorização explícita da autora.
