;; -*- coding: utf-8-unix -*-
(ns example
  (:use [logutil :only [configure-logging]]
        [clojure.tools.logging]))

(defn -main [& args]
  (configure-logging
   {"handlers" "java.util.logging.MemoryHandler"
    "java.util.logging.MemoryHandler.target" "logutil.LazyFileHandler"
    "logutil.LazyFileHandler.path" "tmp.log"
    "logutil.LazyFileHandler.level" "ALL"
    "logutil.LazyFileHandler.formatter" "logutil.Log4JLikeFormatter"
    ".level" "INFO"
    "exam.level" "ALL"})
  (trace "trace")
  (debug "debug!")
  (info  "info!!")
  (warn  "warn!!!")
  (error "error!!!!")
  (fatal "fatal!!!!!"))
