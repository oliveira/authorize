(ns authorize.core
  (:require [authorize.controller :refer [authorizer]])
  (:gen-class))

(defn -main
  [& args]
  (authorizer))
