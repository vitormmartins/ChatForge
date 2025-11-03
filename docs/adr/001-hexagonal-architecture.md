# ADR 001: Adoção de Arquitetura Hexagonal com DDD

## Status
Aceito

## Contexto
Precisamos de uma arquitetura que seja:
- Testável independentemente de frameworks
- Desacoplada de infraestrutura
- Focada no domínio de negócio
- Escalável e manutenível

## Decisão
Adotar Arquitetura Hexagonal (Ports & Adapters) com princípios DDD:
- Domain Layer: Entidades, Value Objects, Domain Services
- Application Layer: Use Cases, Commands, Queries, Ports
- Infrastructure Layer: Adapters, JPA, Security, etc.
- Presentation Layer: Controllers, REST API

## Consequências
### Positivas
- Domínio isolado e testável
- Fácil trocar implementações de infraestrutura
- Código mais limpo e organizado

### Negativas
- Mais camadas = mais complexidade inicial
- Curva de aprendizado para novos desenvolvedores