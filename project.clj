(defproject fort-knox "0.2.0"
  :description "A disk-backed core.cache implementation based on LMDB"
  :url "https://github.com/shriphani/fort-knox"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.cache "0.6.5"]
                 [byte-streams "0.2.2"]
                 [clj-lmdb "0.2.4"]
                 [factual/clj-leveldb "0.1.1"]
                 [clj-named-leveldb "0.1.0"]
                 [me.raynes/fs "1.4.6"]])
