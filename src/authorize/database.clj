(ns authorize.database)

(def my-db (atom {}))

(defn add [table doc] (swap! my-db update-in [table] conj doc))

(defn search-by-name [table name]
  (filter #(= name (:name %)) (get @my-db table)))

(defn search-by-table [table]
  (get @my-db table))
