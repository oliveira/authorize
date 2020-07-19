(ns authorize.accounts
  (:require [clojure.core.match :refer [match]]
            [clojure.data.json :as json]
            [authorize.database :as db]
            [authorize.violations :as violations]))

(defn save-account
  [account-data]
  (db/add db/account-db :account account-data []))

(defn creating-rules
  [account previous-state]
  (json/write-str
    (match [previous-state]
      [(previous-state :guard #(empty? %))] (save-account account)
      [(previous-state :guard #(not (empty? %)))] (violations/already-initialized previous-state))))

(defn find-account
  []
  (db/search-by-table db/account-db :account))

(defn create-account
  [new-account]
  (let [previous-state (find-account)
       {account-data :account} new-account]
    (creating-rules account-data previous-state)))
