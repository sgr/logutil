(defproject logutil "0.2.3"
  :description "A Clojure library designed to use Java Logging API easily."
  :url "https://github.com/sgr/logutil"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main logutil
  :dependencies [[org.clojure/clojure "[1.5,)"]
                 [org.clojure/tools.logging "[0.2,)"]]
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :test-paths ["src/test/clojure"]
  :test-selectors {:default (complement :regression)
                   :regression :regression
                   :all (constantly true)}
  :aot :all
  :plugins [[codox "0.6.4"]]
  :codox {:sources ["src/main/clojure"]
          :exclude [logutil.formatter logutil.handler]
          :output-dir "doc"
          :src-dir-uri "https://github.com/sgr/logutil/blob/master"
          :src-linenum-anchor-prefix "L"})
