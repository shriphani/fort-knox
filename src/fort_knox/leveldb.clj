(ns fort-knox.leveldb
  (:require [byte-streams]
            [clojure.core.cache :refer :all]
            [clj-leveldb :as leveldb]
            [clj-named-leveldb.core :as named-leveldb]
            [me.raynes.fs :as fs])
  (:import [clj_leveldb LevelDB]))

(defn cache->leveldb
  ([cache]
   (cache->leveldb cache
                   ""))

  ([cache name]
   (let [cache* (into {} cache)

         db     (leveldb/->LevelDB (:db cache*)
                                   (:key-decoder cache*)
                                   (:key-encoder cache*)
                                   (:val-decoder cache*)
                                   (:val-encoder cache*))]
     (named-leveldb/make-named-db db name))))

(defn fixed-to-string
  [x]
  (if-not (nil? x)
    (byte-streams/to-string x)))

(defcache LevelDBCache
  [cache]
  CacheProtocol
  (lookup
   [_ item]
   (fixed-to-string
    (named-leveldb/get (cache->leveldb cache)
                       (name item))))

  (lookup
   [_ item not-found]
   (or (fixed-to-string
        (named-leveldb/get (cache->leveldb cache)
                           (name item)))
       not-found))

  (has?
   [_ item]
   (-> (named-leveldb/get (cache->leveldb cache)
                          (name item))
       nil?
       not))

  (hit
   [cache item]
   cache)

  (miss
   [_ item result]
   (named-leveldb/put (cache->leveldb cache)
                      (name item)
                      result)
   (LevelDBCache. cache))
  
  (evict
   [_ k]
   (named-leveldb/delete (cache->leveldb cache)
                         (name k))
   (LevelDBCache. cache))

  (seed
   [_ base]
   (LevelDBCache. base)))

(defn make-cache
  "Args:
   location: /path/to/location/to/persist/cache - should be a directory"
  [location]

  ;; first make the directory if it doesn't exist
  (when-not (fs/exists? location)
    (fs/mkdir location))

  ;; set up the database
  (LevelDBCache.
   (leveldb/create-db
    location
    {})))

(defn make-cache-from-db
  "Args:
   db: An existing lmdb instance"
  [db]
  (LevelDBCache. db))

(def make-db leveldb/create-db)
