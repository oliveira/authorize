(ns violations.insufficient_limit_test
  (:require [midje.sweet :refer :all]
            [authorize.violations :as violations]))

(fact "Amount maior que o limite"
  (violations/insufficient-limit [] {:availableLimit 100} {:amount 120} []) => 2)
