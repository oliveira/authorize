(ns transactions.transactions_test
  (:require [midje.sweet :refer :all]
            [authorize.transactions :as transactions]))

(fact "Uma transação sem valor não é válida"
  (+ 1 1) => 2)
