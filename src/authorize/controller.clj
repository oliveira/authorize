(ns authorize.controller
  (:require [authorize.service.accounts :as service-account]
            [authorize.service.transactions :as service-transaction]
            [clojure.core.match :refer [match]]
            [clojure.data.json :as json]))

(defn map-to-json
  [line]
  (-> line
      (json/write-str)
      (println)))

(defn process-events
  [event]
  (let [event-mapped (json/read-str event :key-fn keyword)]

  (map-to-json
    (match [event-mapped]
      [{:account _}] (service-account/create event-mapped)
      [{:transaction _}] (service-transaction/create event-mapped)))))

(defn create-events
  [events]
  (->> events
       (map process-events)
       doall))

(defn get-events
 []
 (line-seq (java.io.BufferedReader. *in*)))

(defn authorizer
  []
  (-> (get-events)
      (create-events)))
