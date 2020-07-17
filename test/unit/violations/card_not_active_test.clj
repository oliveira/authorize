(ns violations.card_not_active_test
  (:require [midje.sweet :refer :all]
            [authorize.violations :refer :all]))

(facts "violated card-not-active"
  (fact "card not active"
    (card-not-active nil {:activeCard false} {:transaction {:amount 120}} []) => ["card-not-active"])

  (fact "already had a violation at list"
    (card-not-active nil {:activeCard false} {:transaction {:amount 120}} ["another-violation"]) => ["another-violation" "card-not-active"]))

(facts "not violated card-not-active"
  (fact "card active"
    (card-not-active nil {:activeCard true} {:transaction {:amount 120}} []) => [])

  (fact "already had a violation at list"
    (card-not-active nil {:activeCard true} {:transaction {:amount 120}} ["another-violation"]) => ["another-violation"]))
