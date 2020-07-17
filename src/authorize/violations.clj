(ns authorize.violations
  (:require [authorize.accounts :as account]
            [authorize.database :as db]
            [clj-time [core :as time] [local :as local-time]]))

(defn continue [chain account-state new-transaction violations]
  (if chain
    (let [next-one (first chain)]
      (next-one (rest chain) account-state new-transaction violations))
    (account-state new-transaction violations)))

(defn insufficient-limit
  [chain account-state new-transaction violations]
  (let [{available-limit :availableLimit} account-state
        {{amount :amount} :transaction} new-transaction]

    (if (> amount available-limit)
      (continue chain account-state new-transaction (conj violations "insufficient-limit"))
      (continue chain account-state new-transaction violations))))

(defn card-not-active
  [chain account-state new-transaction violations]
  (let [{active-card :activeCard} account-state]

    (if (false? active-card)
      (continue chain account-state new-transaction (conj violations "card-not-active"))
      (continue chain account-state new-transaction violations))))

; usar a mesma query do tx-history para
; doubled-transaction e high-frequency-small-interval
(defn parse-date
  [dt]
  (local-time/to-local-date-time dt))

(defn get-time-interval
  [end-limit delta]
  (let [base-date (parse-date end-limit)
        start (time/minus base-date (time/minutes delta))
        end (time/plus base-date (time/millis 1))]

    (time/interval start end)))

(defn within-interval?
  [interval dt]
  (time/within? interval dt))

(defn get-similar-transactions [tx-history transaction]
  (let [{{merchant :merchant amount :amount} :transaction} transaction]
    (filter (fn [tx]
              (and (= amount (:amount tx))
                   (= merchant (:merchant tx))))
            tx-history)))

(defn get-transactions-in-time-interval
  [transactions-list transaction delta]
  (let [{{time :time} :transaction} transaction
        interval (get-time-interval time delta)]
    (filter (fn [tx]
              (within-interval? interval (parse-date (:time tx)))) transactions-list)))

(defn transactions-two-minutes-interval
  [transactions-list transaction]
  (get-transactions-in-time-interval transactions-list transaction 2))

(defn doubled-transaction
  [chain account-state new-transaction violations]
  (let [transactions-list (db/search-by-table db/transaction-db :transaction)
        listinha-sux (transactions-two-minutes-interval transactions-list new-transaction)
        similar-transactions (get-similar-transactions listinha-sux new-transaction)]

    (if (>= (count similar-transactions) 2)
      (continue chain account-state new-transaction (conj violations "doubled-transaction"))
      (continue chain account-state new-transaction violations))))

(defn high-frequency-small-interval
  [chain account-state new-transaction violations]
  (let [transactions-list (db/search-by-table db/transaction-db :transaction)
        listinha-sux (transactions-two-minutes-interval transactions-list new-transaction)]

    (if (>= (count listinha-sux) 3)
      (continue chain account-state new-transaction (conj violations "high-frequency-small-interval"))
      (continue chain account-state new-transaction violations))))
