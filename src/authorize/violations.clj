(ns authorize.violations
  (:require [authorize.accounts :as account]))

(defn continue [chain account-state new-transaction violations]
  (if chain
    (let [next-one (first chain)]
      (next-one (rest chain) account-state new-transaction violations))))

(defn insufficient-limit
  [chain account-state new-transaction violations]
  (let [{available-limit :availableLimit} account-state
        {{amount :amount} :transaction} new-transaction]

    (if (> amount available-limit)
      (continue chain account-state new-transaction (conj violations "insufficient-limit"))
      (continue chain account-state new-transaction violations))))

(defn card-not-active
  [chain account-state new-transaction violations]
  (let [{active-card :activeCard} account-state
        {{merchant :merchant} :transaction} new-transaction]

    (if (false? active-card)
      (continue chain account-state new-transaction (conj violations "card-not-active"))
      (continue chain account-state new-transaction violations))))

(defn high-frequency-small-interval
  [chain account-state new-transaction violations])
