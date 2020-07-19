(ns authorize.transactions
  (:require [authorize.accounts :as account]
            [authorize.violations :as violations]
            [authorize.database :as db]))

(defn persist-data
  [account-state new-transaction violations]
  (let [{available-limit :availableLimit active-card :activeCard} account-state
        {{amount :amount} :transaction} new-transaction
        newAmount (- available-limit amount)]

  (db/push db/transaction-db :transaction (:transaction new-transaction))
  (account/save-account {:activeCard active-card :availableLimit newAmount})))

(defn capture
  [chain account-state new-transaction violations]
  (if (= 0 (count violations))
    (persist-data account-state new-transaction violations)
    (str {:account account-state, :violations violations})))

(defn create-transaction
  [new-transaction]
  (let [chain
         [violations/insufficient-limit
          violations/card-not-active
          violations/doubled-transaction!
          violations/high-frequency-small-interval!
          capture]
        account-state (account/find-account)]
    (violations/continue chain account-state new-transaction [])))
