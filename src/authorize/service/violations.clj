(ns authorize.service.violations
  (:require [authorize.repository.transactions :as repository-transaction]
            [clj-time [core :as time] [local :as local-time]]))

(defn already-initialized
  [account-data]
  {:account account-data :violations ["account-already-initialized"]})

(defn continue
  [context]
  (let [chain (:chain context)]
    (if chain
      (let [next-function (first chain)]
        (next-function (assoc context :chain (rest chain))))
      (dissoc context :chain))))

(defn insufficient-limit
  [context]
  (let [available-limit (get-in context [:account-state :availableLimit])
        amount (get-in context [:new-transaction :transaction :amount])]

    (if (> amount available-limit)
      (continue (update-in context [:violations] conj "insufficient-limit"))
      (continue context))))

(defn card-not-active
  [context]
  (let [active-card (get-in context [:account-state :activeCard])]

    (if (false? active-card)
      (continue (update-in context [:violations] conj "card-not-active"))
      (continue context))))

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

(defn get-similar-transactions
  [transactions-list transaction]
  (let [{{merchant :merchant amount :amount} :transaction} transaction]

    (filter (fn [trx]
              (and (= amount (:amount trx))
                   (= merchant (:merchant trx))))
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
  [context]
  (let [transactions-list (repository-transaction/find-all)
        new-transaction (get-in context [:new-transaction])
        interval-transactions (transactions-two-minutes-interval transactions-list new-transaction)
        similar-transactions (get-similar-transactions interval-transactions new-transaction)]

    (if (>= (count similar-transactions) 2)
      (continue (update-in context [:violations] conj "doubled-transaction"))
      (continue context))))

(defn high-frequency-small-interval!
  [context]
  (let [transactions-list (repository-transaction/find-all)
        new-transaction (get-in context [:new-transaction])
        interval-transactions (transactions-two-minutes-interval transactions-list new-transaction)]

    (if (>= (count interval-transactions) 3)
      (continue (update-in context [:violations] conj "high-frequency-small-interval"))
      (continue context))))
