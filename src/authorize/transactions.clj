(ns authorize.transactions
  (:require [authorize.accounts :as account]
            [authorize.rules :as rule]))

; (defn doubled-transaction [chain acc-state new-transaction violation]
;   (let [{active-card :activeCard} acc-state
;       {{merchant :merchant amount :amount} :transaction} new-transaction]
;
;     (rule/doubled-transaction))

(defn capture [chain acc-state new-transaction violations]
  (rule/capture acc-state new-transaction violations))

(defn card-not-active [chain acc-state new-transaction violations]
  (rule/card-not-active chain acc-state new-transaction violations))

(defn insufficient-limit [chain acc-state new-transaction violations]
  (rule/insufficient-limit chain acc-state new-transaction violations))

(defn create-transaction [new-transaction]
  (let
    [chain
      [insufficient-limit
       card-not-active
       doubled-transaction
       capture]
     acc-state (account/find-account)]
    (rule/continue chain acc-state new-transaction [])))
