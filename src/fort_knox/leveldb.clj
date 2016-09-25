(ns fort-knox.leveldb
  (:require [clojure.core.cache :refer :all]
            [clj-leveldb :as leveldb]
            [me.raynes.fs :as fs]))

(defcache LevelDBCache
  [cache]
  CacheProtocol
  (lookup
   [_ item]
   (leveldb/get cache
                (name item)))

  (lookup
   [_ item not-found]
   (or (leveldb/get cache
                    (name item))
       not-found))

  (has?
   [_ item]
   (-> (leveldb/get cache
                    (name item))
       nil?
       not))

  (hit
   [cache item]
   cache)

  (miss
   [_ item result]
   (leveldb/put cache
                (name item)
                result)
   (LevelDBCache. cache))
  
  (evict
   [_ k]
   (leveldb/delete cache (name k))
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
   (leveldb/create-db location {})))

(defn make-cache-from-db
  "Args:
   db: An existing lmdb instance"
  [db]
  (LevelDBCache. db))

(def make-db leveldb/create-db)
