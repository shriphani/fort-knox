(defproject fort-knox "0.1.1"
  :description "A disk-backed core.cache implementation based on LMDB"
  :url "https://github.com/shriphani/fort-knox"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.cache "0.6.5"]
                 [clj-lmdb "0.2.4"]
                 [com.sleepycat/je "5.0.73"]
                 [me.raynes/fs "1.4.6"]])
