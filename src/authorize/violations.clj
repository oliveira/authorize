(ns authorize.violations
  (:require [authorize.database :as db]
            [clj-time [core :as time] [local :as local-time]]))

(defn already-initialized
  [account-data]
  (str {:account account-data, :violations ["account-already-initialized"]}))

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

(defn parse-date
  [date]
  (local-time/to-local-date-time date))

(defn get-time-interval
  [end-limit delta]
  (let [base-date (parse-date end-limit)
        start (time/minus base-date (time/minutes delta))
        end (time/plus base-date (time/millis 1))]

    (time/interval start end)))

(defn within-interval?
  [interval date]
  (time/within? interval date))

(defn get-similar-transactions [transactions-list transaction]
  (let [{{merchant :merchant amount :amount} :transaction} transaction]
    (filter (fn [transaction]
              (and (= amount (:amount transaction))
                   (= merchant (:merchant transaction))))
            transactions-list)))

(defn get-transactions-in-time-interval
  [transactions-list transaction delta]
  (let [{{time :time} :transaction} transaction
        interval (get-time-interval time delta)]
    (filter (fn [tx]
              (within-interval? interval (parse-date (:time tx)))) transactions-list)))

(defn transactions-two-minutes-interval
  [transactions-list transaction]
  (get-transactions-in-time-interval transactions-list transaction 2))

(defn doubled-transaction!
  [chain account-state new-transaction violations]
  (let [transactions-list (db/search-by-table db/transaction-db :transaction)
        interval-transactions (transactions-two-minutes-interval transactions-list new-transaction)
        similar-transactions (get-similar-transactions interval-transactions new-transaction)]

    (if (>= (count similar-transactions) 2)
      (continue chain account-state new-transaction (conj violations "doubled-transaction"))
      (continue chain account-state new-transaction violations))))

(defn high-frequency-small-interval!
  [chain account-state new-transaction violations]
  (let [transactions-list (db/search-by-table db/transaction-db :transaction)
        interval-transactions (transactions-two-minutes-interval transactions-list new-transaction)]

    (if (>= (count interval-transactions) 3)
      (continue chain account-state new-transaction (conj violations "high-frequency-small-interval"))
      (continue chain account-state new-transaction violations))))
