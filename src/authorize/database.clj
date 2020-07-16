(ns authorize.database)

(def account-db (atom {}))

(defn add [db table doc violations] (swap! db assoc :violations violations table doc))

(defn search-by-name [db table name]
  (filter #(= name (:name %)) (get @db table)))

(defn search-by-table [db table]
  (get @db table))


; (def transaction-db (atom {}))
