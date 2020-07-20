(ns authorize.controller
  (:require [authorize.accounts :refer :all]
            [authorize.transactions :refer :all]
            [clojure.core.match :refer [match]]
            [clojure.data.json :as json]))

(defn map-to-json [line]
  (-> line (json/write-str)
      (println)))

(defn process-events
  [event]
  (let [event-mapped (json/read-str event :key-fn keyword)]

  (map-to-json
    (match [event-mapped]
      [{:account _}] (create-account event-mapped)
      [{:transaction _}] (create-transaction event-mapped)))))

(defn create-events
  [events]
  (->> events (map process-events)
       doall))

(defn get-events
 []
 (line-seq (java.io.BufferedReader. *in*)))

(defn authorizer
  []
  (-> (get-events)
      (create-events)))
