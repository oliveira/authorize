(ns violations.insufficient_limit_test
  (:require [midje.sweet :refer :all]
            [authorize.violations :refer :all]))

(facts "violated insufficient-limit"
  (fact "amount > than limit"
    (insufficient-limit nil {:availableLimit 100} {:transaction {:amount 120}} []) => ["insufficient-limit"])

  (fact "already had a violation at list"
    (insufficient-limit nil {:availableLimit 100} {:transaction {:amount 120}} ["another-violation"]) => ["another-violation" "insufficient-limit"]))

(facts "not violated insufficient-limit"
  (fact "amount < than limit"
    (insufficient-limit nil {:availableLimit 100} {:transaction {:amount 50}} []) => [])

  (fact "amount equal to limit"
    (insufficient-limit nil {:availableLimit 100} {:transaction {:amount 100}} []) => [])

  (fact "amount zero"
    (insufficient-limit nil {:availableLimit 100} {:transaction {:amount 0}} []) => [])

  (fact "negative amount"
    (insufficient-limit nil {:availableLimit 100} {:transaction {:amount -50}} []) => [])

  (fact "already had a violation at list"
    (insufficient-limit nil {:availableLimit 100} {:transaction {:amount 20}} ["another-violation"]) => ["another-violation"]))
