(defproject logutil "0.2.2"
  :description "A Clojure library designed to use Java Logging API easily."
  :url "https://github.com/sgr/logutil"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main logutil
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [org.clojure/tools.logging "[0.2,)"]]
  :plugins [[codox "0.6.4"]]
  :codox {:exclude [formatter handler]
          :src-dir-uri "https://github.com/sgr/logutil/blob/master"
          :src-linenum-anchor-prefix "L"})
