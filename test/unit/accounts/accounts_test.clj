(ns accounts.accounts_test
  (:require [midje.sweet :refer :all]
            [authorize.accounts :as accounts]))

(facts "accounts operations"
  (fact "creating new account"
    (accounts/create-account {:account {:activeCard true, :availableLimit 100}})
      => (str {:account {:activeCard true, :availableLimit 100}, :violations ["account-already-initialized"]}))

  (fact "retrieving account"
    (accounts/find-account) => {:activeCard true :availableLimit 100})

  (fact "creating account without previous account"
    (accounts/creating-rules {:activeCard true, :availableLimit 100} nil)
      => {:account {:activeCard true, :availableLimit 100}, :violations []}))

  (fact "creating account with previous account"
    (accounts/creating-rules {:activeCard true, :availableLimit 100} {:activeCard true, :availableLimit 100})
      => (str {:account {:activeCard true, :availableLimit 100}, :violations ["account-already-initialized"]})

  (fact "save account data"
    (accounts/save-account {:activeCard true :availableLimit 100}) => {:account {:activeCard true :availableLimit 100} :violations []}))
