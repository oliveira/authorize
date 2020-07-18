(ns violations.high_frequency_small_interval_test
  (:require [midje.sweet :refer :all]
            [authorize.violations :refer :all]
            [authorize.database :as db]))


(facts "violated high-frequency-small-interval when three or more transactions has been presented at captured list in the last two minutes"
  (against-background (db/search-by-table db/transaction-db :transaction)
  => [{ :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" },
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" },
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" }])

  (fact "high frequency"
    (high-frequency-small-interval nil {:activeCard false} {:transaction { :merchant 333 :amount 10 :time "2019-02-13T11:00:00.000Z" }} []) => ["high-frequency-small-interval"])

  (fact "already had a violation at list"
    (high-frequency-small-interval nil {:activeCard false} {:transaction { :merchant 333 :amount 10 :time "2019-02-13T11:00:00.000Z" }} ["another-violation"]) => ["another-violation" "high-frequency-small-interval"]))

(facts "not violated high-frequency-small-interval"
  (against-background (db/search-by-table db/transaction-db :transaction)
  => [
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" },
      { :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" }])

  (fact "not high-frequency-small-interval"
    (high-frequency-small-interval
      nil
      {:activeCard true}
      {:transaction { :merchant 333 :amount 10 :time "2019-02-13T10:59:00.000Z" }}
       []) => []))
