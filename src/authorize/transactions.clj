(ns authorize.transactions
  (:require [authorize.violations :as violations]
            [authorize.repository.accounts :as repository-account]
            [authorize.repository.transactions :as repository-transaction]))

(defn persist-data
  [account-state new-transaction]
  (let [available-limit (get-in account-state [:availableLimit])
        amount (get-in new-transaction [:transaction :amount])]

    (repository-transaction/save new-transaction)
    (repository-account/save (assoc account-state :availableLimit (- available-limit amount)))))

(defn capture
  [context]
  (let [account-state (get-in context [:account-state])
        new-transaction (get-in context [:new-transaction])
        violations (get-in context [:violations])]

    (if (empty? violations)
      (persist-data account-state new-transaction)
      {:account account-state, :violations violations})))

(defn creating-rules
  [new-transaction]
  (let [context {:chain [violations/insufficient-limit
                         violations/card-not-active
                         violations/doubled-transaction!
                         violations/high-frequency-small-interval!
                         capture]
                 :account-state (repository-account/find-state)
                 :new-transaction new-transaction
                 :violations []}]

    (violations/continue context)))

(defn create-transaction
  [new-transaction]
  (creating-rules new-transaction))
