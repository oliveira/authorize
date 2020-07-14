(ns authorize.core
  (:require
            [clojure.tools.cli :refer [cli]]
            [clojure.data.json :as json]
            [clojure.core.match :refer [match]]
            [authorize.events :as events]
            [authorize.accounts :as accounts]
            [authorize.transactions :as transactions]))

(defn elseee
  [evnt]
  (-> evnt))

(defn validations
  [event]
  (let [event-mapped (json/read-str event :key-fn keyword)]
  (println
    (match [event-mapped]
      [{:account _}] (accounts/create-account event-mapped)
      [{:transaction _}] (println "ewww")
       :else (elseee event-mapped)
     )
  )
))

(defn validations-apply
  [events]
  (->> events
       (map validations)
       doall))

(defn authorizer
  []
  (-> (events/parse-events)
      (validations-apply)))

(defn -main
  [& args]
  (authorizer))
