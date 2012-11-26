;; -*- coding: utf-8-unix -*-
(ns handler
  (:import [java.io File FileOutputStream]
           [java.util.logging Formatter Level LogManager LogRecord SimpleFormatter]))

(gen-class
 :name logutil.LazyFileHandler
 :extends java.util.logging.StreamHandler
 :constructors {[String java.util.logging.Formatter] []
                [] []}
 :exposes-methods {publish publishSuper
                   setFormatter setFormatterSuper
                   setLevel setLevelSuper
                   setOutputStream setOutputStreamSuper}
 :state state
 :init init
 :post-init post-init
 :methods [[setPath [String] void]]
 :prefix "lfh-")

(defn- lfh-init
  ([]
     [[] (atom {:file nil :opened false})])
  ([^String path ^Formatter formatter]
     [[] (atom {:file (File. path) :opened false})]))

(defn- lfh-setPath [this ^String path]
  (swap! (.state this) assoc :file (File. path)))

(defn- lfh-setFormatter [this ^Formatter formatter]
  (.setFormatterSuper this (if formatter formatter (SimpleFormatter.))))

(defn- lfh-init-with-prop [this]
  (let [lmgr (LogManager/getLogManager)
        path (.getProperty lmgr "logutil.LazyFileHandler.path")
        fmtr (.getProperty lmgr "logutil.LazyFileHandler.formatter")]
    (when path
      (lfh-setPath this path))
    (.setFormatterSuper this (if fmtr
                               (.newInstance (Class/forName fmtr))
                               (SimpleFormatter.)))))

(defn- lfh-post-init
  ([this]
     (lfh-init-with-prop this))
  ([this ^String path ^Formatter formatter]
     (.setLevelSuper this Level/ALL)
     (lfh-setFormatter this formatter)))

(defn- lfh-publish [this ^LogRecord r]
  (when-not (:opened @(.state this))
    (let [f (:file @(.state this))]
      (.setOutputStreamSuper this (FileOutputStream. f (.exists f))))
    (swap! (.state this) assoc :opened true))
  (.publishSuper this r))
