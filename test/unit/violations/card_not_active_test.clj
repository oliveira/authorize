(ns violations.card_not_active_test
  (:require [midje.sweet :refer :all]
            [authorize.service.violations :refer :all]))

(facts "card not active scenario"
  (fact "should return a violation list with 'card-not-active' message"
    (get-in (card-not-active {:chain nil
                              :account-state {:activeCard false}
                              :new-transaction {:transaction {:amount 120}}
                              :violations []}) [:violations])
      => ["card-not-active"])

  (fact "should keep previous violations and append 'card-not-active'"
    (get-in (card-not-active {:chain nil
                              :account-state {:activeCard false}
                              :new-transaction {:transaction {:amount 120}}
                              :violations ["another-violation"]}) [:violations])
      => ["another-violation" "card-not-active"]))

(facts "card active scenario"
  (fact "card active"
    (get-in (card-not-active {:chain nil
                      :account-state {:activeCard true}
                      :new-transaction {:transaction {:amount 120}}
                      :violations []}) [:violations])
       => [])

 (fact "card active and a violation"
   (get-in (card-not-active {:chain nil
                     :account-state {:activeCard true}
                     :new-transaction {:transaction {:amount 120}}
                     :violations ["another-violation"]}) [:violations])
      => ["another-violation"]))
