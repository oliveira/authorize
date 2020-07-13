(ns authorize.events)

(defn parse-events
    []
    (line-seq (java.io.BufferedReader. *in*)))
