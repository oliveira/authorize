(ns authorize.accounts
  (:require [clojure.core.match :refer [match]]
            [authorize.database :as db]))

(defn get-account
  []
  (db/search-by-table :account))

(defn already-initialized
  [account-data]
  (str {:account account-data, :violations ["account-already-initialized"]}))

(defn create-new-account
  [account-data]
  (db/add :account account-data []))

(defn creating-rules
  [account-data previous-account]
  (match [previous-account]
    [(previous-account :guard #(empty? %))] (create-new-account account-data)
    :else (already-initialized previous-account)
   ))

(defn create-account
  [new-account]
  (let [previous-account (get-account)
       {account-data :account} new-account]
    (creating-rules account-data previous-account)))
