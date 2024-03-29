(ns transactions.transactions_test
  (:require [authorize.repository.accounts :as repository-account]
            [authorize.service.transactions :as service-transaction]
            [midje.sweet :refer :all]))

(facts "transactions"
  (against-background (repository-account/save {:activeCard true :availableLimit 90})
    => {:account {:activeCard true :availableLimit 80} :violations []})

  (fact "should create a transaction"
    (service-transaction/create {:transaction {:merchant 333 :amount 10 :time "2019-02-13T03:50:00.000Z"}})
      => {:account {:activeCard true :availableLimit 80} :violations []}))
