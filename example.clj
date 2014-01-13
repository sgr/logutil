;; -*- coding: utf-8-unix -*-
(ns example
  (:require [logutil :as lu]
            [clojure.tools.logging :as log]
            [seesaw.core :as sc])
  (:import [java.awt GraphicsEnvironment]))

(def ^{:private true} PMAP-CLI
   {"handlers" "java.util.logging.MemoryHandler"
    "java.util.logging.MemoryHandler.target" "logutil.LazyFileHandler"
    "logutil.LazyFileHandler.path" "tmp.log"
    "logutil.LazyFileHandler.level" "ALL"
    "logutil.LazyFileHandler.formatter" "logutil.Log4JLikeFormatter"
    ".level" "INFO"
    "example.level" "ALL"})

(def ^{:private true} PMAP-GUI
  {".level" "ALL"
   "exapmle.level" "ALL"})

(defn- log-all-level [i]
  (doseq [level [:info :debug :trace :warn :error :fatal]]
    (log/logf level "logged [%d] as %s" i (name level))))

(defn example-cli []
  (lu/configure-logging PMAP-CLI)
  (doseq [i (range 20)] (log-all-level i)))

(defn example-gui []
  (lu/configure-logging-swing 100 PMAP-GUI)
  (doseq [i (range 20)] (log-all-level i)))
  (let [e (GraphicsEnvironment/getLocalGraphicsEnvironment)
        p (.getCenterPoint e)
        width 300
        height 200
        btn (sc/button :text "display log dialog")
        frame (sc/frame :title "test"
                        :content btn
                        :size [width :by height]
                        :on-close :dispose)]
    (sc/listen btn :action
               (fn [_]
                 (doto (log-dlg frame)
                   (.setVisible true))))
    (-> frame
      (sc/move! :to [(- (.x p) (int (/ width 2))) (- (.y p) (int (/ height 2)))])
      sc/show!)))
