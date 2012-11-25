;; -*- coding: utf-8-unix -*-
(ns example
  (:use [clojure.tools.logging]
        [logutil :only [init-root-handler]])
  (:import [java.util.logging Handler Level Logger]
           [java.util.logging FileHandler MemoryHandler StreamHandler]))

(defn -main []
  (let [sh (doto (StreamHandler. System/err (logutil.Log4JLikeFormatter.))
             (.setEncoding "utf-8"))
;;        fh (doto (FileHandler. "tmp.log" true)
;;             (.setFormatter (logutil.Log4JLikeFormatter.)))
        lh (logutil.LazyFileHandler. "tmp.log" (logutil.Log4JLikeFormatter.))
        mh (doto (MemoryHandler. lh 1000 Level/SEVERE)
             (.setLevel Level/ALL))]
    (init-root-handler mh))
  (trace "trace")
  (debug "debug!")
  (info  "info!!")
  (warn  "warn!!!")
  (error "error!!!!")
  (fatal "fatal!!!!!")
  )
