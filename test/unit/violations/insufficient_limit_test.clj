(ns violations.insufficient_limit_test
  (:require [midje.sweet :refer :all]
            [authorize.violations :refer :all]))

(facts "insufficient limit scenario"
  (fact "amount > than limit"
    (get-in (insufficient-limit {:chain nil
                                 :account-state {:availableLimit 100}
                                 :new-transaction {:transaction {:amount 120}}
                                 :violations []}) [:violations])
      => ["insufficient-limit"])

  (fact "already had a violation at list"
    (get-in (insufficient-limit {:chain nil
                                 :account-state {:availableLimit 100}
                                 :new-transaction {:transaction {:amount 120}}
                                 :violations ["another-violation"]}) [:violations])
      => ["another-violation" "insufficient-limit"]))

(facts "sufficient limit scenario"
  (fact "amount < than limit"
  (get-in (insufficient-limit {:chain nil
                               :account-state {:availableLimit 100}
                               :new-transaction {:transaction {:amount 50}}
                               :violations []}) [:violations]) => [])

  (fact "amount equal to limit"
  (get-in (insufficient-limit {:chain nil
                               :account-state {:availableLimit 100}
                               :new-transaction {:transaction {:amount 100}}
                               :violations []}) [:violations]) => [])

  (fact "amount zero"
  (get-in (insufficient-limit {:chain nil
                               :account-state {:availableLimit 100}
                               :new-transaction {:transaction {:amount 0}}
                               :violations []}) [:violations]) => [])

  (fact "negative amount"
  (get-in (insufficient-limit {:chain nil
                               :account-state {:availableLimit 100}
                               :new-transaction {:transaction {:amount -50}}
                               :violations []}) [:violations]) => [])

  (fact "with violation in list"
  (get-in (insufficient-limit {:chain nil
                               :account-state {:availableLimit 100}
                               :new-transaction {:transaction {:amount 20}}
                               :violations ["another-violation"]}) [:violations]) => ["another-violation"]))
