(ns authorize.accounts
  (:require [clojure.core.match :refer [match]]
            [authorize.repository.accounts :as repository]
            [authorize.violations :as violations]))

(defn creating-rules
  [account previous-state]
    (match [previous-state]
      [(previous-state :guard #(empty? %))] (repository/save-account account)
      [(previous-state :guard #(not (empty? %)))] (violations/already-initialized previous-state)))

(defn create-account
  [new-account]
  (let [previous-state (repository/find-account)
       {account-data :account} new-account]
    (creating-rules account-data previous-state)))
