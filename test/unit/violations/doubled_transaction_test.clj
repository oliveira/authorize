(ns violations.doubled_transaction_test
  (:require [authorize.database :as db]
            [authorize.service.violations :refer [doubled-transaction!]]
            [midje.sweet :refer :all]))

(facts "doubled transaction scenario"
  (against-background (db/search-by-table db/transaction-db :transaction)
  => [{ :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" }
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:30.000Z" }])

  (fact "doubled transaction"
    (get-in (doubled-transaction! {:chain nil
                                   :account-state {:activeCard true}
                                   :new-transaction {:transaction { :merchant 333 :amount 10 :time "2019-02-13T11:00:00.000Z" }}
                                   :violations []}) [:violations])
      => ["doubled-transaction"])

 (fact "already had a violation at list"
    (get-in (doubled-transaction! {:chain nil
                                   :account-state {:activeCard true}
                                   :new-transaction {:transaction { :merchant 333 :amount 10 :time "2019-02-13T11:00:00.000Z" }}
                                   :violations ["another-violation"]}) [:violations])
      => ["another-violation" "doubled-transaction"]))

(facts "not doubled transaction scenario"
  (against-background (db/search-by-table db/transaction-db :transaction)
  => [{ :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" }
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:30.000Z" }])

  (fact "not doubled transaction"
    (get-in (doubled-transaction! {:chain nil
                                   :account-state {:activeCard true}
                                   :new-transaction {:transaction { :merchant 333 :amount 10 :time "2019-02-13T12:00:00.000Z" }}
                                   :violations []}) [:violations])
      => [])

  (fact "already had a violation at list"
    (get-in (doubled-transaction! {:chain nil
                                   :account-state {:activeCard true}
                                   :new-transaction {:transaction { :merchant 333 :amount 10 :time "2019-02-13T12:00:00.000Z" }}
                                   :violations ["another-violation"]}) [:violations])
      => ["another-violation"]))
