# 🍔 Menu Master

## 📄 Descrição:

MenuMaster é uma **API REST** para controle de pedidos e gerenciamento de lanchonetes de pequeno porte.

Permite gerenciar clientes, fornecedores, ingredientes, produtos e pedidos, com autenticação e autorização via **JWT**.

## 🛠 Tecnologias:

- **Java 21**
- **Spring Boot 3**
- **Spring Security 6 (com autenticação JWT)**
- **Spring Data JPA**
- **MapStruct**
- **PostgreSQL**
- **SpringDoc / Swagger UI**

## 💾 Instalação:

1. Clone o repositório:
   ```bash
   git clone https://github.com/HenriqueRodriguesDeFreitas/MenuMaster.git
   ```
2. Crie o banco de dados **MenuMaster** no PostgreSQL e execute o script `CriarTabelas.sql` que está na raiz do projeto.
3. Insira os dados iniciais rodando o script `InserirDadosTabela.sql` (também na raiz do projeto).
4. Configure as variáveis de ambiente JWT no IntelliJ antes de rodar o projeto:
    - Abra **Run > Edit Configurations**.
    - Selecione a configuração de execução do projeto.
    - Na seção **Environment variables**, adicione:
      ```
      CHAVE_PUBLICA=caminho/para/app.pub
      CHAVE_PRIVADA=caminho/para/app.key
      ```
5. Execute o projeto pelo IntelliJ. ✅

## 🔑 Gerando suas chaves JWT

Para rodar o projeto, você precisa de um par de chaves **privada** e **pública**.

1. Abra o terminal no diretório do projeto.
2. Gere a **chave privada** (`app.key`):
   ```bash
   openssl genpkey -algorithm RSA -out app.key -pkeyopt rsa_keygen_bits:2048
   ```  
3. Gere a **chave pública** (`app.pub`) a partir da privada:
   ```bash
   openssl rsa -pubout -in app.key -out app.pub
   ```  
   
4. Coloque os arquivos gerados na pasta resources
5. Configure as variáveis de ambiente no IntelliJ como mostrado na seção de instalação.

> Agora o endpoint de autenticação `/autenticacao/login` conseguirá gerar tokens JWT válidos usando sua chave privada, e o Spring Security conseguirá validar usando a chave pública.

## 📝 Instruções de uso:

- Acesse a documentação dos endpoints via Swagger:
  ```
  http://localhost:8080/swagger-ui.html
  ```
- Todos os endpoints de autenticação estão disponíveis em:
  ```
  /autenticacao/*
  ```
- Outros endpoints estão protegidos e requerem token JWT válido.
