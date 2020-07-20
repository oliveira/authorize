(ns violations.already_initialized_test
  (:require [midje.sweet :refer :all]
            [authorize.service.accounts :refer :all]))

(facts "violated already-initialized"
  (fact "when account already initialized"
    (creating-rules {:activeCard true :availableLimit 222}
                    {:activeCard true, :availableLimit 100})
      => {:account {:activeCard true, :availableLimit 100}, :violations ["account-already-initialized"]}))

(facts "not violated already-initialized"
  (fact "when account is been initialized"
    (creating-rules {:activeCard true, :availableLimit 100}
                     nil)
      => {:violations [], :account {:activeCard true, :availableLimit 100}}))
