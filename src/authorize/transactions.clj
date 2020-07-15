(ns authorize.transactions
  (:require [authorize.accounts :as account]
            [authorize.violations :as violations]))

(defn capture [chain account-state new-transaction violations]
  (let [{available-limit :availableLimit active-card :activeCard} account-state
        {{amount :amount} :transaction} new-transaction
        newAmount (- available-limit amount)]

    (if (= 0 (count violations))
      (account/save {:activeCard active-card :availableLimit newAmount})
      (str {:accountx account-state, :violations violations}))))

(defn create-transaction [new-transaction]
  (let
    [chain
      [violations/insufficient-limit
       violations/card-not-active
       capture]
     account-state (account/find-account)]
     (violations/continue chain account-state new-transaction [])))
