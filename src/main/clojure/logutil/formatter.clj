;; -*- coding: utf-8-unix -*-
(ns logutil.formatter
  (:import [java.text SimpleDateFormat]
           [java.util Date]
           [java.util.logging Formatter LogRecord]))

(def ^{:private true} SEPARATOR (System/getProperty "line.separator"))

(defn- with-throwable [^String msg ^Throwable thrown]
  (loop [s msg t thrown]
    (let [ss (reduce #(str %1 %2)
                     (str s (format "Caused by %s: %s%s" (.getName (class t))
                                    (if-let [m (.getMessage t)] m "") SEPARATOR))
                     (map #(format "        at %s%s" % SEPARATOR) (.getStackTrace t)))]
      (if-let [c (.getCause t)] (recur ss c) ss))))

(gen-class
 :name logutil.Log4JLikeFormatter
 :extends java.util.logging.Formatter
 :prefix "l4f-")

(defn- l4f-format [this ^LogRecord r]
  (let [msg (format "%s %-7s %s [%d] %s%s"
                    (.format (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss SSS") (Date. (.getMillis r)))
                    (.getName (.getLevel r))
                    (.getLoggerName r)
                    (.getThreadID r)
                    (.getMessage r)
                    SEPARATOR)]
    (if-let [e (.getThrown r)] (with-throwable msg e) msg)))
