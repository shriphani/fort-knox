(ns fort-knox.core
  (:require [clojure.core.cache :refer :all]
            [me.raynes.fs :as fs]
            [fort-knox.leveldb :as leveldb]
            [fort-knox.lmdb :as lmdb])
  (:import [fort_knox.leveldb LevelDBCache]
           [fort_knox.lmdb LMDBCache]
           [java.lang UnsupportedOperationException]))

(defn make-cache
  "Args:
   location: /path/to/location/to/persist/cache - should be a directory"
  ([location]
   (make-cache location :type :leveldb))

  ([location & options]
   ;; first make the directory if it doesn't exist
   (let [options-dict (into {}
                            [(into []
                                   options)])]
     
     (when-not (fs/exists? location)
       (fs/mkdir location))
     
     ;; set up the database
     (cond (-> options-dict
               :type
               (= :leveldb))
           (LevelDBCache.
            (leveldb/make-cache location))

           (-> options-dict
               :type
               (= :lmdb))
           (LMDBCache.
            (lmdb/make-cache location))

           :else
           (UnsupportedOperationException. "This DB is not supported (yet).")))))

(defn make-cache-from-db
  "Args:
   db: An existing lmdb instance"
  [db & options]
  (let [options-dict (->> options
                          (into [])
                          vector
                          (into {}))]
    (cond (-> options-dict
              :type
              (= :lmdb))
          (LMDBCache. db)

          (-> options-dict
              :type
              (= :leveldb))
          (LevelDBCache. db)

          :else
          (UnsupportedOperationException. "This DB is not supported (yet)."))))
