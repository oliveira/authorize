(ns authorize.database)

(def account-db (atom {}))
(def transaction-db (atom {}))

(defn add
  [db table doc violations]
  (swap! db assoc table doc :violations violations))

(defn push
  [db table doc]
  (swap! db update-in [table] conj doc))

(defn search-by-name
  [db table name]
  (filter #(= name (:name %)) (get @db table)))

(defn search-by-table
  [db table]
  (get @db table))
