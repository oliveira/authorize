(ns authorize.core
  (:require [authorize.controller :refer [authorizer]]
            [clojure.data.json :as json])
  (:gen-class))

(defn -main
  [& args]
  (authorizer))
