(ns authorize.service.transactions
  (:require [authorize.repository.accounts :as repository-account]
            [authorize.repository.transactions :as repository-transaction]
            [authorize.service.violations :as service-violations]))

(defn persist-data
  [account-state new-transaction]
  (let [available-limit (get-in account-state [:availableLimit])
        amount (get-in new-transaction [:transaction :amount])]

    (repository-transaction/save new-transaction)
    (repository-account/save (assoc
                              account-state
                              :availableLimit (- available-limit amount)))))

(defn capture
  [context]
  (let [account-state (get-in context [:account-state])
        new-transaction (get-in context [:new-transaction])
        violations (get-in context [:violations])]

    (if (empty? violations)
      (persist-data account-state new-transaction)
      {:account account-state :violations violations})))

(defn creating-rules
  [new-transaction]
  (let [context {:chain [service-violations/insufficient-limit
                         service-violations/card-not-active
                         service-violations/doubled-transaction!
                         service-violations/high-frequency-small-interval!
                         capture]
                 :account-state (repository-account/find-state)
                 :new-transaction new-transaction
                 :violations []}]

    (service-violations/continue context)))

(defn create
  [new-transaction]
  (creating-rules new-transaction))
