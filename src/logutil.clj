;; -*- coding: utf-8-unix -*-
(ns logutil
  (:gen-class)
  (:require [formatter]
            [handler])
  (:import [java.util.logging Handler Level Logger]))

(defn- clear-handlers [^Logger l]
  (doseq [h (.getHandlers l)]
    (.removeHandler l h)))

(defn init-root-handler [^Handler h]
  (doto (.getParent (Logger/getLogger Logger/GLOBAL_LOGGER_NAME))
    clear-handlers
    (.addHandler h)))
