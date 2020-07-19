(ns authorize.transactions
  (:require [authorize.accounts :as account]
            [clojure.data.json :as json]
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
  [context]
  (let [account-state (get-in context [:account-state])
        new-transaction (get-in context [:new-transaction])
        violations (get-in context [:violations])]

    (json/write-str(if (empty? violations)
                      (persist-data account-state new-transaction violations)
                      {:account account-state, :violations violations}))))

(defn create-transaction
  [new-transaction]
  (let [context {:chain [violations/insufficient-limit
                         violations/card-not-active
                         violations/doubled-transaction!
                         violations/high-frequency-small-interval!
                         capture]
                 :account-state (account/find-account)
                 :new-transaction new-transaction
                 :violations []}]

    (violations/continue context)))
