(ns authorize.database)

(def my-db (atom {}))

(defn add [table doc violations] (swap! my-db assoc :violations violations table doc))

(defn search-by-name [table name]
  (filter #(= name (:name %)) (get @my-db table)))

(defn search-by-table [table]
  (get @my-db table))
