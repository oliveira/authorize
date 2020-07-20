(ns accounts.accounts_test
  (:require [midje.sweet :refer :all]
            [authorize.service.accounts :as service-account]
            [authorize.repository.accounts :as repository-account]))

(facts "accounts operations"
  (fact "creating new account"
    (service-account/create {:account {:activeCard true, :availableLimit 100}})
      => {:account {:activeCard true, :availableLimit 100}, :violations ["account-already-initialized"]})

  (fact "retrieving account"
    (repository-account/find-state)
      => {:activeCard true :availableLimit 100})

  (fact "creating account without previous account"
    (service-account/creating-rules {:activeCard true, :availableLimit 100} nil)
      => {:account {:activeCard true, :availableLimit 100}, :violations []})

  (fact "creating account with previous account"
    (service-account/creating-rules {:activeCard true, :availableLimit 100} {:activeCard true, :availableLimit 100})
      => {:account {:activeCard true, :availableLimit 100}, :violations ["account-already-initialized"]})

  (fact "save account data"
    (repository-account/save {:activeCard true :availableLimit 100})
      => {:account {:activeCard true :availableLimit 100} :violations []}))
