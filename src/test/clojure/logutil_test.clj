(ns logutil-test
  (:require [clojure.test :refer :all]
            [logutil :refer :all]
            [clojure.tools.logging :as log]
            [seesaw.core :as sc])
  (:import [java.awt GraphicsEnvironment]))

(defn- test-frame []
  (let [e (GraphicsEnvironment/getLocalGraphicsEnvironment)
        p (.getCenterPoint e)
        WIDTH 300
        HEIGHT 200
        btn (sc/button :text "display log dialog")
        frame (sc/frame :title "test"
                        :content btn
                        :size [WIDTH :by HEIGHT]
                        :on-close :dispose)]
    (sc/listen btn :action
               (fn [_]
                 (doto (log-dlg frame)
                   (.setVisible true))))
    (doto frame
      (sc/move! :to [(- (.x p) (int (/ WIDTH 2))) (- (.y p) (int (/ HEIGHT 2)))]))))

(defn- log-all-level [i]
  (let [msg (format "logging %d" i)]
    (doseq [level [:info :debug :trace :warn :error :fatal]] (log/log level msg))))

(def ^{:private true} PMAP-CLI
  {"handlers" "java.util.logging.ConsoleHandler"
   "java.util.logging.ConsoleHandler.level" "ALL"
   "java.util.logging.ConsoleHandler.formatter" "logutil.Log4JLikeFormatter"
   ".level" "INFO"
   "logutil-test.level" "FINE"})

(def ^{:private true} PMAP-GUI
  {".level" "ALL"
   "logutil-test.level" "ALL"})

(deftest ^:cli console-log-test
  (testing "initialize logging CLI"
    (configure-logging PMAP-CLI))
  (testing "logging CLI"
    (doseq [i (range 10)] (log-all-level i))))

(deftest ^:gui gui-log-test
  (testing "logging GUI"
    (configure-logging-swing 10 PMAP-GUI)
    (doseq [i (range 20)] (log-all-level i))
    (sc/invoke-now
     (sc/show! (test-frame)))
    (Thread/sleep 30000)))
