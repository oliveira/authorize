(ns authorize.service.accounts
  (:require [authorize.repository.accounts :as repository-account]
            [authorize.service.violations :as violations]
            [clojure.core.match :refer [match]]))

(defn creating-rules
  [account previous-state]
  (if (empty? previous-state)
    (repository-account/save account)
    (violations/already-initialized previous-state)))

(defn create
  [new-account]
  (let [previous-state (repository-account/find-state)
       {account-data :account} new-account]
    (creating-rules account-data previous-state)))