(defproject logutil "0.2.4"
  :description "A Clojure library designed to use Java Logging API easily."
  :url "https://github.com/sgr/logutil"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main logutil
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.apache.commons/commons-lang3 "3.3.2"]]
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :test-paths ["src/test/clojure"]
  :test-selectors {:default (complement :regression)
                   :regression :regression
                   :all (constantly true)}
  :aot :all
  :plugins [[codox "0.8.10"]]
  :codox {:sources ["src/main/clojure"]
          :exclude [logutil.formatter logutil.handler]
          :output-dir "doc"
          :src-dir-uri "https://github.com/sgr/logutil/blob/master/"
          :src-linenum-anchor-prefix "L"})
