(ns authorize.core
  (:require
            [clojure.tools.cli :refer [cli]]
            [clojure.data.json :as json]
            [clojure.core.match :refer [match]]
            [authorize.events :as events]
            [authorize.accounts :as accounts]
            [authorize.transactions :as transactions]))

(defn validations
  [event]
  (let [event-mapped (json/read-str event :key-fn keyword)]
  (println
    (match [event-mapped]
      [{:account _}] (accounts/createAccount)
      [{:transaction _}] (println event-mapped)
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
