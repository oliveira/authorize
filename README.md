<img src="https://avatars2.githubusercontent.com/u/4257275?s=200&v=4&s=200" width="127px" height="127px" align="left"/>

# authorize

Simple authorization handler created for Nubank

## Table of Contents

- [Arquitetura](#arquitetura)
- [Dependencies](#dependencies)
- [Setup](#setup)
- [Run](#run)
- [API](#api)
  - [POST /transactions](#post-/transactions)
  - [GET /transactions](#get-/transactions)
  - [GET /payables](#get-/payables)
- [Development](#development)

## Arquitetura

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
java -jar target/authorize-standalone.jar < operation
```

### Binary: build and usage 

This script uses the same [environment variables](https://www.postgresql.org/docs/9.1/static/libpq-envars.html) as libpq to connect to a PostgreSQL server.

```bash
lein bin
```

```bash
bin/authorize < operations
```

## API

- [POST /transactions](#post-/transactions)
- [GET /transactions](#get-/transactions)
- [GET /payables](#get-/payables)

### POST /transactions

Create a new transaction.

#### Request body params

| Name                 | Type   | Required | Description                                                      |
| -------------------- | ------ | -------- | ---------------------------------------------------------------- |
| amount           | number | true     | The transaction amount value                                     |
| description      | string | false    | The description of the transaction                               |
| paymentMethod    | string | true     | The payment method. Possible values: _CREDIT_CARD_, _DEBIT_CARD_ |
| cardNumber       | string | true     | The card number                                                  |
| expirationDate   | string | true     | The card expiration date. Format: _MM/YY_                        |
| verificationCode | string | true     | The card verification value                                      |

##### Request body example

> POST /boletos

```json
{
  "amount": 300.75,
  "paymentMethod": "DEBIT_CARD",
  "cardNumber": "4984238052310065",
  "cardOwner": "Eduardo G S Pereira",
  "expirationDate": "03/21",
  "verificationCode": "102"
}
```

#### Request example

```bash
curl --request POST \
--url 'http://localhost:3000/transactions' \
--header 'content-type: application/json' \
--data '{"amount": 300.75,"paymentMethod": "DEBIT_CARD",
         "cardNumber": "4984238052310065","cardOwner": "Eduardo G S Pereira",
         "expirationDate": "03/21","verificationCode": "102"}' \
--include
```

#### Success response example

```bash
HTTP/1.1 201 Created
Access-Control-Allow-Origin: *
X-DNS-Prefetch-Control: off
X-Frame-Options: SAMEORIGIN
Strict-Transport-Security: max-age=15552000; includeSubDomains
X-Download-Options: noopen
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Content-Type: application/json; charset=utf-8
Content-Length: 2
ETag: W/"2-vyGp6PvFo4RvsFtPoIWeCReyIC8"
Vary: Accept-Encoding
Date: Tue, 02 Jul 2019 09:13:37 GMT
Connection: keep-alive

{}
```
#### Bad request response example

```bash
HTTP/1.1 400 Bad Request
Access-Control-Allow-Origin: *
X-DNS-Prefetch-Control: off
X-Frame-Options: SAMEORIGIN
Strict-Transport-Security: max-age=15552000; includeSubDomains
X-Download-Options: noopen
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Content-Type: application/json; charset=utf-8
Content-Length: 2
ETag: W/"2-vyGp6PvFo4RvsFtPoIWeCReyIC8"
Vary: Accept-Encoding
Date: Tue, 02 Jul 2019 09:13:37 GMT
Connection: keep-alive

{
  "errors": [
    {
      "location": "body",
      "param": "email",
      "value": "",
      "msg": "Invalid value"
    }
  ]
}
```


### GET /transactions

List available transactions.

#### Response body params

| Name                   | Type   | Description                               |
| ---------------------- | ------ | ----------------------------------------- |
| transactionId      | string | The ID for the transaction                |
| amount             | number | The transaction amount value              |
| description        | string | The description of the transaction        |
| paymentMethod      | string | The payment method                        |
| cardLastFourDigits | string | The last four digits from the card number |
| expirationDate     | string | The card expiration date                  |
| verificationCode   | string | The card verification value               |

##### Response body example

> GET /transactions

```json
[
  {
    "transactionId": "b65a5674-f9c2-4caf-86fa-0aaaa6398726",
    "amount": 300.13,
    "description": null,
    "paymentMethod": "DEBIT_CARD",
    "cardLastFourDigits": "0065",
    "cardOwner": "Eduardo G S Pereira",
    "expirationDate": "03/2021",
    "verificationCode": "102"
  },
  {
    "transactionId": "5c479b89-ae9c-434c-851c-22b48de1c374",
    "amount": 1050.79,
    "description": "Smartband XYZ 3.0",
    "paymentMethod": "CREDIT_CARD",
    "cardLastFourDigits": "1578",
    "cardOwner": "Indiana Jones",
    "expirationDate": "03/2021",
    "verificationCode": "102"
  }
]
```

#### Request example

```bash
curl --url 'http://localhost:3000/transactions' --include
```

#### Response example

```bash
HTTP/1.1 200 OK
Access-Control-Allow-Origin: *
X-DNS-Prefetch-Control: off
X-Frame-Options: SAMEORIGIN
Strict-Transport-Security: max-age=15552000; includeSubDomains
X-Download-Options: noopen
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Content-Type: application/json; charset=utf-8
Content-Length: 2097
ETag: W/"831-JIMUzB9LV54mD8GqTscE32TTzIo"
Vary: Accept-Encoding
Date: Tue, 02 Jul 2019 09:38:04 GMT
Connection: keep-alive

[{"transactionId":"b65a5674-f9c2-4caf-86fa-0aaaa6398726",
  "amount":300,"description":null,"paymentMethod":"DEBIT_CARD",
  "cardLastFourDigits":"0065","cardOwner":"Eduardo G S Pereira",
  "expirationDate":"03/2021","verificationCode":"102"}]
```

### GET /payables

List the available payable.

#### Response body params

| Name             | Type   | Description                             |
| ---------------- | ------ | --------------------------------------- |
| available    | number | The total amount available for the user |
| waitingFunds | number | The amount that are waiting funds       |

##### Request body example

> GET /payables

```json
{
  "available": 2417.1,
  "waitingFunds": 28.62
}
```

#### Request example

```bash
curl --url 'http://localhost:3000/payables' --include
```

#### Response example

```bash
HTTP/1.1 200 OK
Access-Control-Allow-Origin: *
X-DNS-Prefetch-Control: off
X-Frame-Options: SAMEORIGIN
Strict-Transport-Security: max-age=15552000; includeSubDomains
X-Download-Options: noopen
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Content-Type: application/json; charset=utf-8
Content-Length: 41
ETag: W/"29-zMxJefaVfxTPUE50GOPHJdaSjMI"
Vary: Accept-Encoding
Date: Tue, 02 Jul 2019 09:59:15 GMT
Connection: keep-alive

{"available":2417.1,"waitingFunds":28.62}
```

## Development

- [Run tests](#run-tests)
- [Create and undo migrations](#create-and-undo-migrations)

### Run tests

Full tests and test coverage

Please, set environment variables to a test database.

```bash
yarn test
```

Unit tests and watch for changes

```bash
yarn run unit-test
```
