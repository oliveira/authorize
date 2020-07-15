(ns authorize.rules
  (:require [authorize.accounts :as account]))

(defn continue [chain acc-state new-transaction violations]
  (if chain
    (let [next-one (first chain)]
      (next-one (rest chain) acc-state new-transaction violations))))

(defn insufficient-limit
  [chain acc-state new-transaction violations]
  (let [{available-limit :availableLimit} acc-state
        {{amount :amount} :transaction} new-transaction]

  (if (> amount available-limit)
    (continue chain acc-state new-transaction (conj violations "insufficient-limit"))
    (continue chain acc-state new-transaction violations))))

(defn card-not-active
  [chain acc-state new-transaction violations]
  (let [{active-card :activeCard} acc-state
        {{merchant :merchant} :transaction} new-transaction]

  (if (false? active-card)
    (continue chain acc-state new-transaction (conj violations "card-not-active"))
    (continue chain acc-state new-transaction violations))))

(defn capture
  [acc-state new-transaction violations]
  (let [{available-limit :availableLimit active-card :activeCard} acc-state
        {{amount :amount} :transaction} new-transaction
        amount (- available-limit amount)]

  (if (= 0 (count violations))
    (account/save {:activeCard active-card :availableLimit amount})
    (str {:accountx acc-state, :violations violations}))))
