(ns authorize.repository.transactions
  (:require [authorize.database :as db]))

(defn save-transaction
  [new-transaction]
  (db/push db/transaction-db :transaction (:transaction new-transaction)))
