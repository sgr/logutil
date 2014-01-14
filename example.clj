;; -*- coding: utf-8-unix -*-
(ns example
  (:require [logutil :as lu]
            [clojure.tools.logging :as log]))

(def ^{:private true} PMAP
   {"handlers" "java.util.logging.MemoryHandler"
    "java.util.logging.MemoryHandler.target" "logutil.LazyFileHandler"
    "logutil.LazyFileHandler.path" "tmp.log"
    "logutil.LazyFileHandler.level" "ALL"
    "logutil.LazyFileHandler.formatter" "logutil.Log4JLikeFormatter"
    ".level" "INFO"
    "example.level" "ALL"})

(defn- log-all-level [i]
  (doseq [level [:info :debug :trace :warn :error :fatal]]
    (log/logf level "logged [%d] as %s" i (name level))))

(defn example-logging []
  (lu/configure-logging PMAP)
  (doseq [i (range 20)] (log-all-level i)))
