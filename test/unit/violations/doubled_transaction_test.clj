(ns violations.doubled_transaction_test
  (:require [midje.sweet :refer :all]
            [authorize.violations :refer :all]
            [authorize.database :as db]))

(facts "violated doubled-transaction"
  (against-background (db/search-by-table db/transaction-db :transaction)
  => (list
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" },
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:30.000Z" }))

  (fact "doubled transaction"
    (doubled-transaction
      nil
      {:activeCard true}
      {:transaction { :merchant 333 :amount 10 :time "2019-02-13T11:00:00.000Z" }}
       []) => ["doubled-transaction"])

 (fact "already had a violation at list"
   (doubled-transaction
     nil
     {:activeCard true}
     {:transaction { :merchant 333 :amount 10 :time "2019-02-13T11:00:00.000Z" }}
      ["another-violation"]) => ["another-violation" "doubled-transaction"]))

(facts "not violated doubled-transaction"
  (against-background (db/search-by-table db/transaction-db :transaction)
  => (list
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" },
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:30.000Z" }))

  (fact "not doubled transaction"
    (doubled-transaction
      nil
      {:activeCard true}
      {:transaction { :merchant 333 :amount 10 :time "2019-02-13T12:00:00.000Z" }}
       []) => [])

 (fact "already had a violation at list"
   (doubled-transaction
     nil
     {:activeCard true}
     {:transaction { :merchant 333 :amount 10 :time "2019-02-13T12:00:00.000Z" }}
      ["another-violation"]) => ["another-violation"]))
