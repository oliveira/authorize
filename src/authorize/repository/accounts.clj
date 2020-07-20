(ns authorize.repository.accounts
  (:require [authorize.database :as db]))

(defn save
  [account-data]
  (db/add db/account-db :account account-data []))

(defn find-state
  []
  (db/search-by-table db/account-db :account))
