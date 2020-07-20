<img src="https://avatars2.githubusercontent.com/u/4257275?s=200&v=4&s=200" width="127px" height="127px" align="left"/>

# authorize

Simple authorization handler created for Nubank

## Table of Contents

- [Architecture](#architecture)
- [Dependencies](#dependencies)
- [Setup](#setup)
- [Violations](#violations)
  - [Already initialized](#already-initialized)
  - [Card not active](#card-not-active)
  - [Doubled transaction](#doubled-transaction)
  - [High frequency at small interval](#high-frequency-at-small-interval)
  - [Insufficient limit](#insufficient-limit)
- [Development](#development)
  - [Run tests](#run-tests)

## Architecture
Essa proposta de arquitetura para um handler de autorizações foi baseada em algumas premissas. Vamos a elas:
* Ser expansivel
* Ter fácil manutenção
* Isolamento da regra de negócio para que houvesse uma boa cobertura de testes

Para alcançar essas premissas, imaginei que pudesse implementar o Repository Pattern para isolar a lógica de acesso aos dados. Já o domínio ficaria concentrado numa camada chamada Service. Tentei aplicar o conceito de bounded context (DDD) para isolar os domínios e acredito ter chegado num resultado razoável.

De maneira geral foi interessante assumir o desafio de desenvolver o projeto em clojure. Não tenho domínio da linguagem e ainda assim percebi seus benefícios. A agilidade no desenvolvimento e a facilidade em refatorar com frequência foram notórios. Pontos esses só alcançados pela garantia que os testes unitários trouxeram. Dessa forma foi possível manter 90%> cobertura de código durante todo o desenvolvimento.

## Dependencies

- [Clojure = 1.10.1](https://clojure.org/guides/getting_started#_clojure_installer_and_cli_tools)
- [Leiningen = ^2.9.3](https://leiningen.org/#install)
- [Docker](https://docs.docker.com/get-docker)

## Setup

- [Docker: build and usage](#docker-build-and-usage)
- [Lein usage](#lein-usage)
- [Uberjar: build and usage](#uberjar-build-and-usage)
- [Binary: build and usage](#binary-build-and-usage)

### Docker: build and usage

This script uses the same [environment variables](https://www.postgresql.org/docs/9.1/static/libpq-envars.html) as libpq to connect to a PostgreSQL server.

```bash
docker build -t authorizer .
```

```bash
docker run -i authorizer < operations
```

### Lein usage

This script uses the same [environment variables](https://www.postgresql.org/docs/9.1/static/libpq-envars.html) as libpq to connect to a PostgreSQL server.

```bash
lein run < operations
```

### Uberjar: build and usage

This script uses the same [environment variables](https://www.postgresql.org/docs/9.1/static/libpq-envars.html) as libpq to connect to a PostgreSQL server.

```bash
lein uberjar
```

```bash
java -jar target/authorize-standalone.jar < operations
```

### Binary: build and usage

This script uses the same [environment variables](https://www.postgresql.org/docs/9.1/static/libpq-envars.html) as libpq to connect to a PostgreSQL server.

```bash
lein bin
```

```bash
bin/authorize < operations
```

## Violations
Are a set of rules that transactions must satisfy to be captured.


### Already initialized

Check if account was previously initialized. Return "account-already-initialized" if true.

##### Payload events example

```json
{ "account": { "activeCard": true, "availableLimit": 100 } }
{ "account": { "activeCard": true, "availableLimit": 100 } }
{ "transaction": { "merchant": "333", "amount": 10, "time": "2019-02-13T11:00:00.000Z" } }
```

```bash
docker run -i authorizer < operations
```

#### Output

```
{"account":{"activeCard":true,"availableLimit":100},"violations":[]}
{"account":{"activeCard":true,"availableLimit":100},"violations":["account-already-initialized"]}
{"account":{"activeCard":true,"availableLimit":90},"violations":[]}
```
---
### Card not active

Check if card is active at account entity. Return "card-not-active" if true and and block the transaction from been captured.

##### Payload events example

```json
{ "account": { "activeCard": false, "availableLimit": 100 } }
{ "transaction": { "merchant": "333", "amount": 10, "time": "2019-02-13T11:00:00.000Z" } }
```

```bash
docker run -i authorizer < operations
```

#### Output

```
{"account":{"activeCard":false,"availableLimit":100},"violations":[]}
{"account":{"activeCard":false,"availableLimit":100},"violations":["card-not-active"]}
```
---
### Doubled transaction

Check if there were two or more transactions with the same merchant and amount in a time window (2 minutes). Return "doubled-transaction" if true and block the transaction from been captured.

##### Payload events example

```json
{ "account": { "activeCard": true, "availableLimit": 100 } }
{ "transaction": { "merchant": "333", "amount": 10, "time": "2019-02-13T11:00:00.000Z" } }
{ "transaction": { "merchant": "333", "amount": 10, "time": "2019-02-13T11:00:00.000Z" } }
{ "transaction": { "merchant": "333", "amount": 10, "time": "2019-02-13T11:00:00.000Z" } }
```

```bash
docker run -i authorizer < operations
```

#### Output

```
{"account":{"activeCard":true,"availableLimit":100},"violations":[]}
{"account":{"activeCard":true,"availableLimit":90},"violations":[]}
{"account":{"activeCard":true,"availableLimit":80},"violations":[]}
{"account":{"activeCard":true,"availableLimit":80},"violations":["doubled-transaction"]}
```
---
### High frequency at small interval

Check if there were three or more transactions in a time window (2 minutes). Return "high-frequency-small-interval" if true and block the transaction from been captured.

##### Payload events example

```json
{ "account": { "activeCard": true, "availableLimit": 100 } }
{ "transaction": { "merchant": "111", "amount": 10, "time": "2019-02-13T11:00:10.000Z" } }
{ "transaction": { "merchant": "111", "amount": 10, "time": "2019-02-13T11:00:43.000Z" } }
{ "transaction": { "merchant": "333", "amount": 10, "time": "2019-02-13T11:00:55.000Z" } }
{ "transaction": { "merchant": "444", "amount": 10, "time": "2019-02-13T11:00:59.000Z" } }
{ "transaction": { "merchant": "555", "amount": 10, "time": "2019-02-13T11:01:11.000Z" } }
{ "transaction": { "merchant": "555", "amount": 10, "time": "2019-02-13T11:05:11.000Z" } }
```

```bash
docker run -i authorizer < operations
```

#### Output

```
{"account":{"activeCard":true,"availableLimit":100},"violations":[]}
{"account":{"activeCard":true,"availableLimit":90},"violations":[]}
{"account":{"activeCard":true,"availableLimit":80},"violations":[]}
{"account":{"activeCard":true,"availableLimit":70},"violations":[]}
{"account":{"activeCard":true,"availableLimit":70},"violations":["high-frequency-small-interval"]}
{"account":{"activeCard":true,"availableLimit":70},"violations":["high-frequency-small-interval"]}
{"account":{"activeCard":true,"availableLimit":60},"violations":[]}

```
---
### Insufficient limit

Check if available amount is bigger than the transaction amount. Return "insufficient-limit" if true and block the transaction from been captured.

##### Payload events example

```json
{ "account": { "activeCard": true, "availableLimit": 60 } }
{ "transaction": { "merchant": "111", "amount": 50, "time": "2019-02-13T11:00:10.000Z" } }
{ "transaction": { "merchant": "111", "amount": 13, "time": "2019-02-13T11:00:43.000Z" } }

```

```bash
docker run -i authorizer < operations
```
#### Output

```
{"account":{"activeCard":true,"availableLimit":60},"violations":[]}
{"account":{"activeCard":true,"availableLimit":10},"violations":[]}
{"account":{"activeCard":true,"availableLimit":10},"violations":["insufficient-limit"]}
```
---
## Development
### Run tests

Run all unit tests (and :violations, :transactions or :accounts to run specific scenarios)

```bash
lein midje
```
