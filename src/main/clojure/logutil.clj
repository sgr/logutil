;; -*- coding: utf-8-unix -*-
(ns logutil
  (:require [logutil.formatter]
            [logutil.handler]
            [clojure.tools.logging :as log]
            [seesaw.core :as sc]
            [seesaw.bind :as sb]
            [seesaw.mig :as sm])
  (:import [java.awt Dialog$ModalityType Dimension Toolkit]
           [java.awt.datatransfer StringSelection]
           [java.io ByteArrayInputStream ByteArrayOutputStream InputStream]
           [java.util.logging Level Logger LogManager]
           [javax.swing JDialog JOptionPane JScrollPane JTable ListSelectionModel WindowConstants]
           [javax.swing.table JTableHeader TableColumn]
           [com.github.sgr.logging LogRecordRenderer LogRecordRow TableModelHandler]
           [com.github.sgr.swing MultiLineTable]))

(def ^{:private true} PMAP-GUI-EXCLUDE
  {"java.level" "INFO"
   "javax.level" "INFO"
   "sun.level" "INFO"})

(defn ^InputStream config-stream
  "Translate property map to an InputStream of Properties."
  [m]
  (let [baos (ByteArrayOutputStream.)
        props (java.util.Properties.)]
    (doseq [[k v] m] (.setProperty props k v))
    (.store props baos "store to pipe")
    (ByteArrayInputStream. (.toByteArray baos))))

(defn configure-logging
  "Initialize logging configuration from a logging property map:

     {\"handlers\" \"java.util.logging.MemoryHandler\"
      \"java.util.logging.MemoryHandler.target\" \"logutil.LazyFileHandler\"
      \"logutil.LazyFileHandler.path\" \"tmp.log\"
      \"logutil.LazyFileHandler.level\" \"ALL\"
      \"logutil.LazyFileHandler.formatter\" \"logutil.Log4JLikeFormatter\"
      \".level\" \"INFO\"
      \"example.level\" \"ALL\"}"
  [property-map]
  (doto (LogManager/getLogManager)
    (.readConfiguration (config-stream property-map))))

(defn- ^TableColumn table-column [idx width identifier display-name renderer resizable]
  (doto (TableColumn.)
    (.setModelIndex idx)
    (.setPreferredWidth width)
    (.setIdentifier identifier)
    (.setHeaderValue display-name)
    (.setCellRenderer renderer)
    (.setResizable resizable)))

(let [handler-severe  (atom nil)
      handler-warning (atom nil)
      handler-info    (atom nil)
      handler-fine    (atom nil)
      handler-finer   (atom nil)
      handler-finest  (atom nil)
      current-handler (atom nil)
      formatter (logutil.Log4JLikeFormatter.)]
  (defn configure-logging-swing
  "Initialize logging configuration for Swing GUI.
   The format of property-map is similar to configure-logging."
    [capacity property-map]
    (let [root-logger (Logger/getLogger "")]
      (doseq [h (.getHandlers root-logger)] (.removeHandler root-logger h))
      (doto (LogManager/getLogManager) (.reset))
      (configure-logging (merge property-map PMAP-GUI-EXCLUDE))
      (reset! handler-severe  (doto (TableModelHandler. capacity) (.setLevel Level/SEVERE)))
      (reset! handler-warning (doto (TableModelHandler. capacity) (.setLevel Level/WARNING)))
      (reset! handler-info    (doto (TableModelHandler. capacity) (.setLevel Level/INFO)))
      (reset! handler-fine    (doto (TableModelHandler. capacity) (.setLevel Level/FINE)))
      (reset! handler-finer   (doto (TableModelHandler. capacity) (.setLevel Level/FINER)))
      (reset! handler-finest  (doto (TableModelHandler. capacity) (.setLevel Level/FINEST)))
      (doto root-logger
        (.addHandler @handler-severe)
        (.addHandler @handler-warning)
        (.addHandler @handler-info)
        (.addHandler @handler-fine)
        (.addHandler @handler-finer)
        (.addHandler @handler-finest))))

  (defn copy-log-to-clipboard
    "Copy log message to system clipboard.
     \"level\" is either of :severe :warning :info :fine :finer :finest.
     The number of log records is specified to configure-logging-swing as \"capacity\"."
    ([^clojure.lang.Keyword level]
       (if-let [tk (Toolkit/getDefaultToolkit)]
         (if-let [cboard (.getSystemClipboard tk)]
           (let [hdr (condp = level
                       :severe  handler-severe
                       :warning handler-warning
                       :info    handler-info
                       :fine    handler-fine
                       :finer   handler-finer
                       :finest  handler-finest)
                 log-text (if (instance? TableModelHandler @hdr)
                            (clojure.string/join (map #(.format formatter %) (.records @hdr)))
                            (format "Log handler (%s) is nil" (name level)))
                 ss (StringSelection. log-text)]
             (.setContents cboard ss ss))
           (log/error "couldn't get system clipboard"))
         (log/error "couldn't get default toolkit")))
    ([] (copy-log-to-clipboard :fine)))

  (defn- log-panel []
    (letfn [(reset-model [tbl hdr cols]
              (reset! current-handler @hdr)
              (.setModel tbl (.getModel @hdr))
              (-> tbl .getTableHeader (.setReorderingAllowed false))
              (-> tbl .getColumnModel (.setColumnMargin 0))
              (doseq [col cols] (.addColumn tbl col)))]
              
      (let [rdr (LogRecordRenderer.)
            cols [(table-column 0 180 "date"    "Date"    rdr true)
                  (table-column 1 50  "thread"  "Thread"  rdr true)
                  (table-column 2 50  "level"   "Level"   rdr true)
                  (table-column 3 150 "logger"  "Logger"  rdr true)
                  ;; (table-column 4 150 "class"   "Class"   rdr true)
                  ;; (table-column 5 80  "method"  "Method"  rdr true)
                  (table-column 6 450 "message" "Message" rdr true)]
            tbl (doto (MultiLineTable.)
                  (.setPreferredScrollableViewportSize (Dimension. 850 400))
                  (.setSelectionMode ListSelectionModel/SINGLE_SELECTION)
                  (.setAutoResizeMode JTable/AUTO_RESIZE_NEXT_COLUMN))
            levels ["SEVERE" "WARNING" "INFO" "FINE" "FINER" "FINEST"]
            combo (sc/combobox :model levels)
            cbtn (sc/button :text "Copy log to clipboard")]

        (reset-model tbl handler-info cols)
        (.setSelectedItem combo "INFO")

        (sc/listen combo :selection
                   (fn [_]
                     (when-let [hdr (condp = (nth levels (.getSelectedIndex combo))
                                      "SEVERE"  handler-severe
                                      "WARNING" handler-warning
                                      "INFO"    handler-info
                                      "FINE"    handler-fine
                                      "FINER"   handler-finer
                                      "FINEST"  handler-finest)] 
                       (reset-model tbl hdr cols))))

        (sc/listen cbtn :action (fn [_] (copy-log-to-clipboard current-handler)))

        (sc/border-panel :north (sm/mig-panel
                                 :constraints ["wrap 3, ins 5 10 5 10" "[:450:][:150:][:200:]" ""]
                                 :items [[(sc/label "Log level") "align right"] [combo "grow"] [cbtn "align right"]])
                         :center (sc/scrollable tbl)))))

  (defn ^JDialog log-dlg
    "Display a dialog for viewing logs.
     \"parent\" is a parent frame."
    [parent]
    (when-not @handler-info (configure-logging-swing 100 {}))
    (let [op (doto (JOptionPane.)
               (.setOptionType JOptionPane/DEFAULT_OPTION)
               (.setMessage (log-panel)))]
      (if parent
        (doto (.createDialog op parent "Application Log")
          (.setDefaultCloseOperation WindowConstants/DISPOSE_ON_CLOSE)
          (.setLocationRelativeTo parent)
          (.setModalityType Dialog$ModalityType/MODELESS)
          (.pack))
        (doto (.createDialog op "Application Log")
          (.setDefaultCloseOperation WindowConstants/DISPOSE_ON_CLOSE)
          (.setModalityType Dialog$ModalityType/MODELESS)
          (.pack))))))

