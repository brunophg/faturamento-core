# Faturamento-Core API

Uma API RESTful corporativa para gestão de faturamento, empresas e emissão de notas fiscais. O sistema é responsável por processar vendas, gerenciar um catálogo de produtos e aplicar regras de negócio financeiras (como o cálculo automático de impostos de 23% e valores líquidos) de forma segura, garantindo a integridade dos dados no banco.

## 🛠️ Tecnologias

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3
* **Banco de Dados:** PostgreSQL
* **ORM:** Spring Data JPA / Hibernate
* **Segurança:** Spring Security com Autenticação via Token JWT
* **Documentação:** Swagger / Springdoc OpenAPI (v2.8.0)
* **Gerenciamento:** Maven
* **Qualidade:** JUnit e Mockito para testes unitários automatizados

## ⚙️ Pré-requisitos

Antes de iniciar, certifique-se de possuir em seu ambiente:
* Java Development Kit (JDK) 21
* Apache Maven
* PostgreSQL rodando localmente ou via container Docker

## 🚀 Instalação e Execução

**1. Clone o repositório**

```bash
git clone [https://github.com/seu-usuario/faturamento-core.git](https://github.com/seu-usuario/faturamento-core.git)
cd faturamento-core
```

**2. Configuração de Banco de Dados**

Crie um banco de dados no PostgreSQL. Em seguida, atualize o arquivo `src/main/resources/application.properties` com as suas credenciais:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/nome_do_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

**3. Build e Execução**

```bash
mvn clean install
mvn spring-boot:run
```

## 📖 Uso e Endpoints Principais

A documentação interativa do Swagger foi configurada para contornar o bloqueio inicial do Spring Security. Com a aplicação em execução, acesse pelo navegador:
`http://localhost:8080/api/swagger-ui/index.html`

**Principais Operações:**

* **Catálogo de Produtos:** Suporta criação (`POST /produtos`), atualização segura de dados (`PUT /produtos/{id}`), exclusão lógica (`DELETE /produtos/{id}`), reativação (`PATCH /produtos/{id}/reativar`) e busca rápida nativa por SKU (`GET /produtos/codigo/{codigo}`). As listagens utilizam paginação (`Pageable`) e filtram automaticamente para exibir apenas produtos ativos. As exceções lançam códigos semânticos, como HTTP 404 (Não Encontrado) e HTTP 409 (Duplicado).
* **Emissão de Notas Fiscais:** A criação de notas exige apenas a identificação da empresa, a relação de IDs de produtos e quantidades. A API intercepta a requisição, busca o valor monetário real diretamente da tabela de Produto no banco de dados e gera o `ItemNota` como dependente em cascata, eliminando qualquer risco de manipulação de preços via front-end. O sistema valida o formato de CNPJ, previne notas duplicadas e calcula os impostos automaticamente.