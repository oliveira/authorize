(ns authorize.accounts
  (:require [clojure.core.match :refer [match]]
            [authorize.database :as db]))

(defn create-account
  [new-account]
  (let [previous-state (find-account)
       {account-data :account} new-account]
    (creating-rules account-data previous-state)))

(defn find-account
  []
  (db/search-by-table :account))

(defn creating-rules
  [account previous-state]
  (match [previous-state]
    [(previous-state :guard #(empty? %))] (save account)
    [(previous-state :guard #(not (empty? %)))] (already-initialized previous-state)))

(defn save
  [account-data]
  (db/add :account account-data []))

(defn already-initialized
  [account-data]
  (str {:account account-data, :violations ["account-already-initialized"]}))
