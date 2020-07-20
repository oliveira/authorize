(ns authorize.repository.transactions
  (:require [authorize.database :as db]))

(defn save
  [new-transaction]
  (db/push db/transaction-db :transaction
    (:transaction new-transaction)))

(defn find-all
  []
  (db/search-by-table db/transaction-db :transaction))
