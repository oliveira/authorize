(ns violations.high_frequency_small_interval_test
  (:require [midje.sweet :refer :all]
            [authorize.violations :refer :all]
            [authorize.database :as db]))


(facts "violated high-frequency-small-interval when three or more transactions has been captured in last two minutes"
  (against-background (db/search-by-table db/transaction-db :transaction)
  => [{ :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" },
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" },
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" }])

  (fact "high frequency transaction creation"
    (get-in (high-frequency-small-interval {:chain nil
                                             :account-state {:activeCard false}
                                             :new-transaction {:transaction { :merchant 333 :amount 10 :time "2019-02-13T11:00:00.000Z" }}
                                             :violations []}) [:violations])
      => ["high-frequency-small-interval"])

  (fact "already had a violation at list"
    (get-in (high-frequency-small-interval {:chain nil
                                             :account-state {:activeCard false}
                                             :new-transaction {:transaction { :merchant 333 :amount 10 :time "2019-02-13T11:00:00.000Z" }}
                                             :violations ["another-violation"]}) [:violations])
      => ["another-violation" "high-frequency-small-interval"]))

(facts "not violated high-frequency-small-interval"
  (against-background (db/search-by-table db/transaction-db :transaction)
  => [
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" },
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" }])

  (fact "not high-frequency-small-interva"
   (get-in (high-frequency-small-interval {:chain nil
                                            :account-state {:activeCard false}
                                            :new-transaction {:transaction { :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" }}
                                            :violations []}) [:violations])
     => []))
