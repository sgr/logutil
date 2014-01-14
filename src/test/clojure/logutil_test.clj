(ns logutil-test
  (:require [clojure.test :refer :all]
            [logutil :refer :all]
            [clojure.tools.logging :as log]))

(defn- log-all-level [i]
  (doseq [level [:info :debug :trace :warn :error :fatal]]
    (log/logf level "logged [%d] as %s" i (name level))))

(def ^{:private true} PMAP
  {"handlers" "java.util.logging.ConsoleHandler"
   "java.util.logging.ConsoleHandler.level" "ALL"
   "java.util.logging.ConsoleHandler.formatter" "logutil.Log4JLikeFormatter"
   ".level" "INFO"
   "logutil-test.level" "FINE"})

(deftest ^:integration console-log-test
  (testing "initialize logging"
    (configure-logging PMAP))
  (testing "logging"
    (doseq [i (range 10)] (log-all-level i))))
