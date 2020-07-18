(ns transactions.transactions_test
  (:require [midje.sweet :refer :all]
            [authorize.transactions :refer :all]
            [authorize.accounts :refer :all]
            [authorize.database :as db]))

(facts "transactions"
  (against-background (save-account {:activeCard true :availableLimit 90})
=> {:account {:activeCard true :availableLimit 80} :violations []})

  (fact "should create a transaction"
    (create-transaction {:transaction {:merchant 333, :amount 10, :time "2019-02-13T03:50:00.000Z"}}) => {:account {:activeCard true :availableLimit 80} :violations []}))
