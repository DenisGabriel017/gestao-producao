# Sistema de Gestão de Produção

## Visão Geral

Este é um sistema web completo para gestão de produção, desenvolvido com Java e Spring Boot. A aplicação permite o controle detalhado de produtos, registros de produção, consumo via comandas e a geração de relatórios de performance, fornecendo uma visão clara da operação.

---

## Funcionalidades Principais

- **Dashboard Analítico:** Tela inicial com KPIs (Key Performance Indicators) que mostram os principais dados da operação (Produção, Vendas, Descarte, GAP), com filtros por ano e mês.
- **Gestão de Produtos:**
  - Cadastro, Edição, Listagem e Exclusão de produtos.
  - Filtros dinâmicos por palavra-chave, setor e unidade.
  - Importação de catálogo de produtos em lote via planilhas Excel.
- **Gestão de Produção:**
  - Lançamento de registros de produção diária.
  - Importação de dados de produção em lote via planilhas Excel.
- **Lançamento de Comandas (Consumo):**
  - Lançamento de consumo de produtos, associado ao usuário logado.
  - Histórico de consumo exibido dinamicamente ao selecionar um produto.
  - Importação de comandas em lote via planilhas Excel.
- **Relatório de Performance:**
  - Tabela detalhada com a performance de cada produto, comparando produção, vendas, e outros tipos de consumo.
  - Filtros por período de datas.
  - Funcionalidade para ocultar/exibir colunas.
- **Segurança:**
  - Sistema de login e autenticação de usuários.
  - Acesso às páginas restrito a usuários autenticados.

---

## Tecnologias Utilizadas

- **Backend:**
  - Java 21
  - Spring Boot 3
  - Spring Data JPA (Persistência de Dados)
  - Spring Security (Autenticação e Autorização)
  - Maven (Gerenciador de Dependências)
- **Frontend:**
  - Thymeleaf (Motor de Templates)
  - HTML5 & CSS3
  - JavaScript & jQuery (Dinamismo e AJAX)
  - AdminLTE (Template de UI)
  - Chart.js (Gráficos no Dashboard)
- **Banco de Dados:**
  - PostgreSQL
- **Outras Bibliotecas:**
  - Apache POI (Manipulação de arquivos Excel)
  - Lombok (Redução de código boilerplate)

---

## Como Executar o Projeto

### Pré-requisitos

- JDK 21 ou superior.
- Apache Maven 3.8 ou superior.
- Uma instância do PostgreSQL em execução.

### 1. Configuração do Banco de Dados

1. Crie um novo banco de dados no seu servidor PostgreSQL (ex: `gestao_producao_db`).
2. Abra o arquivo `src/main/resources/application.properties`.
3. Modifique as seguintes propriedades para apontar para o seu banco de dados:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/gestao_producao_db
   spring.datasource.username=seu_usuario_postgres
   spring.datasource.password=sua_senha_postgres
   ```
4. A propriedade `spring.jpa.hibernate.ddl-auto=update` fará com que o Spring Boot crie ou atualize as tabelas automaticamente na primeira vez que a aplicação for executada.

### 2. Compilar e Executar

1. Abra um terminal na raiz do projeto.
2. Execute o seguinte comando Maven para compilar o projeto e iniciar a aplicação:

   ```bash
   mvn spring-boot:run
   ```
3. Após a inicialização, a aplicação estará disponível em `http://localhost:8080`.

---

## Estrutura do Projeto

O código-fonte segue uma arquitetura em camadas padrão para aplicações Spring Boot, visando a separação de responsabilidades:

- `src/main/java/br/com/dnsoftware/gestao_producao`
  - `controller/`: Responsável por receber as requisições web e interagir com a camada de serviço.
  - `service/`: Contém a lógica de negócio da aplicação.
  - `repository/`: Define as interfaces de acesso aos dados (Spring Data JPA).
  - `model/`: Contém as entidades JPA que representam as tabelas do banco de dados.
  - `dto/`: (Data Transfer Objects) Objetos para transferir dados entre as camadas.
  - `projections/`: Interfaces para receber resultados de consultas customizadas do JPA.
  - `security/`: Configurações do Spring Security.
- `src/main/resources/`
  - `templates/`: Contém os arquivos HTML do frontend (Thymeleaf).
  - `static/`: Arquivos estáticos como CSS e JS customizados (se houver).
  - `application.properties`: Arquivo principal de configuração do Spring Boot.
