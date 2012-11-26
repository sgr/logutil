;; -*- coding: utf-8-unix -*-
(ns logutil
  (:require [formatter]
            [handler])
  (:gen-class)
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream InputStream]
           [java.util.logging LogManager]))

(defn ^InputStream config-stream
  "Translate property map to an InputStream of Properties."
  [m]
  (let [baos (ByteArrayOutputStream.)
        props (java.util.Properties.)]
    (doseq [[k v] m] (.setProperty props k v))
    (.store props baos "store to pipe")
    (ByteArrayInputStream. (.toByteArray baos))))

(defn configure-logging
  "Reinitialize logging configuration from a property map."
  [m]
  (doto (LogManager/getLogManager)
    (.readConfiguration (config-stream m))))
