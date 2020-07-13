(ns authorize.accounts
  (:require [authorize.database :as db]))

(defn hasAccount?
  []
  (->
    (db/search-by-table "account")
    (empty?)
    (not)
))

// n testei
(defn create-new-account
  [account-data]
  (->
      (println "criando uma conta")
      (db/add "account" account-data)
))

// preciso fazer o destructuring aqui
(defn createAccount
  [account-data (:account)]
  (if (hasAccount?)
    (println "tem conta")
    (println "criando uma conta")
  )
)
