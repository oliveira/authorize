(ns authorize.accounts
  (:require [authorize.database :as db]))

(defn hasAccount?
  []
  (->
    (db/search-by-table :account)
    (empty?)
    (not)))

(defn create-new-account
  [account-data]
  (db/add :account account-data)
  (println {:account account-data, :violations []}))

(defn already-initialized
  [account-data]
  (println {:account account-data, :violations [" account-already-initialized"]}))

(defn createAccount
  [account]
  (let
    ; [{{active-card :activeCard, available-limit :availableLimit} :account} account]
    [{account-data :account } account]
    (println account-data)
    (if (hasAccount?)
      (already-initialized account-data)
      (create-new-account account-data)
    )
  )
)
