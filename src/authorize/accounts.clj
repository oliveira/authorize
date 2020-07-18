(ns authorize.accounts
  (:require [clojure.core.match :refer [match]]
            [authorize.database :as db]
            [authorize.violations :as violations]))

(defn save
  [account-data]
  (db/add db/account-db :account account-data []))

(defn creating-rules
  [account previous-state]
  (match [previous-state]
    [(previous-state :guard #(empty? %))] (save account)
    [(previous-state :guard #(not (empty? %)))] (violations/already-initialized previous-state)))

(defn find-account
  []
  (db/search-by-table db/account-db :account))

(defn create-account
  [new-account]
  (let [previous-state (find-account)
       {account-data :account} new-account]
    (creating-rules account-data previous-state)))
