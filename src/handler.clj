;; -*- coding: utf-8-unix -*-
(ns handler
  (:import [java.io File FileOutputStream]
           [java.util.logging Formatter LogRecord]))

(gen-class
 :name logutil.LazyFileHandler
 :extends java.util.logging.StreamHandler
 :constructors {[String java.util.logging.Formatter] []}
 :exposes-methods {publish publishSuper
                   setFormatter setFormatterSuper
                   setOutputStream setOutputStreamSuper}
 :state state
 :init init
 :post-init post-init
 :prefix "lfh-")

(defn- lfh-init [^String path ^Formatter formatter]
  (let [f (File. path)]
    (if (.exists f)
      [[] (atom {:file f :os (FileOutputStream. f true)})]
      [[] (atom {:file f :os nil})])))

(defn- lfh-post-init [this ^String path ^Formatter formatter]
  (.setFormatterSuper this formatter)
  (when-let [os (:os @(.state this))]
    (.setOutputStreamSuper this os)))

(defn- lfh-publish [this ^LogRecord r]
  (if-let [os (:os @(.state this))]
    (.publishSuper this r)
    (let [os (FileOutputStream. (:file @(.state this)) true)]
      (swap! (.state this) assoc :os os)
      (.setOutputStreamSuper this os)
      (recur this r))))
