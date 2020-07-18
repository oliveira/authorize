(defproject authorize "0.1.0-SNAPSHOT"
  :min-lein-version "2.0.0"
  :uberjar-name "authorize-standalone.jar"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/core.match "1.0.0"]
                 [clj-time "0.15.2"]]
  :profiles {
             :dev {:dependencies [[midje "1.9.9"]]
                   :plugins[[lein-midje "3.2.1"]
                            [lein-cloverage "1.1.2"]]}}
  :bin {:name "authorize"
        :bin-path "bin"}
  :plugins [[lein-bin "0.3.5"]]
  :main authorize.core
  :test-paths ["test/unit"])
