(defproject authorize "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.2.4"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/core.match "1.0.0"]]
  :bin {:name "authorize"}
  :plugins [[lein-bin "0.3.5"]]
  :main authorize.core)
