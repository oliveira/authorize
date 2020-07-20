(ns authorize.core
  (:require [clojure.data.json :as json]
            [authorize.controller :refer :all])
  (:gen-class))

(defn -main
  [& args]
  (authorizer))
