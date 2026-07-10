# 🚀 Arquitetura de Microsserviços com Spring Cloud & JWT

Este repositório contém uma solução robusta e escalável baseada na **Arquitetura de Microsserviços**, utilizando o ecossistema **Spring Cloud** para governança, roteamento distribuído e descoberta de serviços. A segurança da solução é implementada de ponta a ponta através de tokens **JWT (JSON Web Token)**, validados diretamente na camada de periferia (API Gateway), garantindo um ambiente corporativo resiliente e *stateless*.

---

## 🏗️ Visão Geral da Arquitetura

O ecossistema é projetado de forma modular, separando rigidamente as responsabilidades de infraestrutura das regras de negócio. O fluxo de comunicação e gerenciamento segue o padrão estabelecido pelos componentes do Spring Cloud:

```
                  +-----------------------------------+
                  |          Cliente / Web            |
                  +-----------------------------------+
                                    |
                                    v (HTTP / WebSockets)
                  +-----------------------------------+
                  |         gateway-service           | <--- Valida JWT via Auth
                  +-----------------------------------+
                                    |
            +-----------------------+-----------------------+
            | (Load Balancing via Eureka Service Registry)  |
            v                                               v
+-----------------------+                       +-----------------------+
|    product-service    |                       |     order-service     |
+-----------------------+                       +-----------------------+
|   currency-service    |                       |   greeting-service    |
+-----------------------+                       +-----------------------+
            ^                                               ^
            +-----------------------+-----------------------+
                                    | (Fetch Configs)
                        +-----------------------+
                        |    config-service     | <--- Diretório ./configs
                        +-----------------------+
```

---

## 🛡️ Estrutura do Projeto e Componentes

Com base no mapeamento do ecossistema, o projeto é estruturado em duas grandes categorias de microsserviços:

### 1. Serviços Core e Infraestrutura
Estes serviços formam a espinha dorsal da arquitetura, provendo os requisitos não-funcionais essenciais (governança, segurança, configuração e descoberta):

*   **`discovery-service`:** Servidor de *Service Discovery* baseado no **Netflix Eureka**. Permite o registro dinâmico de todas as instâncias de microsserviços e a localização automatizada entre eles, eliminando o acoplamento por IPs ou portas rígidas.
*   **`gateway-service`:** O API Gateway centralizado da aplicação construído com **Spring Cloud Gateway**. É responsável pelo roteamento inteligente das requisições para as instâncias corretas usando balanceamento de carga dinâmico (`lb://`). Ele executa filtros customizados para validação periférica de tokens JWT antes de repassar a chamada.
*   **`config-service`:** Servidor de configuração centralizado (**Spring Cloud Config Server**). Ele distribui propriedades e parâmetros de ambiente em tempo de execução para todos os demais microsserviços, facilitando a gerência de ambientes (Dev, Staging, Prod).
*   **`auth-service`:** Serviço responsável por gerenciar a autenticação e autorização do ecossistema. Ele valida as credenciais dos usuários e emite os tokens JWT que protegem as rotas internas.
*   **`configs`:** Diretório dedicado ao armazenamento centralizado dos arquivos de configuração (ex: `.yml` ou `.properties`) consumidos diretamente pelo `config-service`.

### 2. Serviços de Negócio (Business Services)
Microsserviços especializados e isolados de forma lógica e física, responsáveis por domínios de negócio específicos da aplicação:

*   **`product-service`:** Responsável pela gestão completa e catálogo de produtos, expondo tanto endpoints REST clássicos quanto canais WebSockets para atualizações em tempo real.
*   **`order-service`:** Controla todo o fluxo de criação, processamento e ciclo de vida de pedidos (*orders*), utilizando conexões WebSockets para interações síncronas/assíncronas de status de compra.
*   **`currency-service`:** Trata das operações de conversão monetária, dados cambiais e cotações de moedas em tempo real.
*   **`greeting-service`:** Serviço utilitário de saudações utilizado principalmente para testes de ambiente, validações iniciais de comunicação e testes de integridade (*health-checks*).

---

## 🔀 Mapeamento de Rotas (API Gateway)

O `gateway-service` utiliza a API programática do `RouteLocator` para interceptar as requisições externas e redirecioná-las para as instâncias de destino corretas por meio de balanceamento de carga (`lb://`). O tráfego e os caminhos estão distribuídos conforme a tabela abaixo:

| Caminho da Requisição (Path) | Destino (URI) | Descrição / Contexto do Fluxo |
| :--- | :--- | :--- |
| `/get` | `http://httpbin.org` | Rota externa usada para testes de requisições HTTP e conectividade básica. |
| `/products/**` | `lb://product-service` | Endpoints REST padrão para consulta e gerenciamento do catálogo de produtos. |
| `/ws/products/**` | `lb://product-service` | Endpoints específicos via WebSockets para comunicação em tempo real de produtos. |
| `/currency/**` | `lb://currency-service` | Endpoints REST para cotações cambiais e conversões monetárias. |
| `/ws/currency/**` | `lb://currency-service` | Endpoints específicos via WebSockets para streaming de dados monetários. |
| `/auth/**` | `lb://auth-service` | Endpoints expostos para login, cadastro de novos usuários e validação de credenciais. |
| `/ws/orders/**` | `lb://order-service` | Endpoints específicos via WebSockets para o gerenciamento dinâmico de pedidos. |

---

## 🛠️ Tecnologias Utilizadas

A stack de tecnologias escolhida visa alta performance, escalabilidade horizontal e facilidade de manutenção no modelo Cloud Native:

*   **Java & Spring Boot:** Framework base para o desenvolvimento de todos os microsserviços.
*   **Spring Cloud Netflix Eureka:** Mecanismo robusto de descoberta e registro de serviços.
*   **Spring Cloud Config:** Centralização e distribuição de configurações de ambiente em tempo de execução.
*   **Spring Cloud Gateway:** Roteamento dinâmico reativo, gerenciamento de rotas e segurança periférica.
*   **JWT (JSON Web Token):** Mecanismo de autenticação e autorização seguro e inteiramente *stateless*.
*   **Docker & Docker Compose:** Containerização completa dos serviços, garantindo paridade entre os ambientes de desenvolvimento e produção.

---

## ⚙️ Como Executar o Ambiente Local

### Pré-requisitos
Antes de iniciar, certifique-se de possuir instalado em sua máquina de desenvolvimento:
*   [Docker](https://docs.docker.com/get-docker/)
*   [Docker Compose](https://docs.docker.com/compose/install/)

### Passo a Passo

1. **Clonar o Repositório:**
   ```bash
   git clone <url-do-seu-repositorio>
   cd <diretorio-do-projeto>
   ```

2. **Subir a Orquestração dos Containers:**
   Navegue até o diretório raiz do projeto (onde se encontra o arquivo `docker-compose.yml`) e execute o comando para baixar as imagens e iniciar os containers em segundo plano (*detached mode*):
   ```bash
   docker-compose up -d
   ```

3. **Ciclo de Inicialização e Monitoramento:**
   Devido à arquitetura distribuída, os microsserviços de negócio dependem da infraestrutura core para funcionar corretamente. É vital monitorar os logs para garantir que o **`config-service`** e o **`discovery-service` (Eureka)** estejam totalmente operantes e saudáveis antes que os serviços de negócio tentem se registrar.
   
   Você pode inspecionar os logs de inicialização rodando:
   ```bash
   # Para acompanhar todos os serviços simultaneamente
   docker-compose logs -f

   # Para acompanhar um serviço core específico
   docker-compose logs -f discovery-service
   ```

---

## 🔒 Fluxo de Segurança (JWT Periférico)

1. **Geração do Token:** O cliente realiza o login enviando suas credenciais para a rota `/auth/login`. O `auth-service` as autentica e devolve um token JWT criptografado e assinado.
2. **Requisição Autorizada:** O cliente anexa esse token no cabeçalho das requisições subsequentes (`Authorization: Bearer <token>`).
3. **Intercepção e Filtro:** Ao tentar acessar rotas protegidas (como `/products/**` ou `/ws/orders/**`), o `gateway-service` intercepta a chamada através de um filtro customizado, decodifica o JWT e valida sua assinatura e prazo de validade.
4. **Encaminhamento Seguro:** Se o token for válido, o Gateway delega a requisição de forma transparente para o microsserviço de destino por meio da rede interna segura. Caso contrário, barra o acesso de imediato devolvendo um status HTTP `401 Unauthorized`.
