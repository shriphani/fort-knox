(ns fort-knox.lmdb
  (:require [clojure.core.cache :refer :all]
            [clj-lmdb.simple :refer :all]
            [clj-lmdb.core :as lmdb]
            [me.raynes.fs :as fs]))

(defn cache->lmdb
  [cache]
  (let [cache* (into {} cache)]
    (lmdb/->DB (:env cache*)
               (:db cache*))))

(defcache LMDBCache
  [cache]
  CacheProtocol
  (lookup
   [_ item]
   (with-txn [rtxn (read-txn (cache->lmdb cache))]
     (get! (cache->lmdb cache)
           rtxn
           (name item))))

  (lookup
   [_ item not-found]
   (with-txn [rtxn (read-txn (cache->lmdb cache))]
     (or (get! (cache->lmdb cache)
               rtxn
               (name item))
         not-found)))

  (has?
   [_ item]
   (with-txn [rtxn (read-txn (cache->lmdb cache))]
     (-> (get! (cache->lmdb cache)
               rtxn
               (name item))
         nil?
         not)))

  (hit
   [cache item]
   cache)

  (miss
   [_ item result]
   (with-txn [wtxn (write-txn (cache->lmdb cache))]
     (put! (cache->lmdb cache)
           wtxn
           (name item)
           result))
   (LMDBCache. cache))
  
  (evict
   [_ k]
   (with-txn [wtxn (write-txn (cache->lmdb cache))]
     (delete! (cache->lmdb cache) wtxn (name k)))
   (LMDBCache. cache))

  (seed
   [_ base]
   (LMDBCache. base)))

(defn make-cache
  "Args:
   location: /path/to/location/to/persist/cache - should be a directory"
  [location]

  ;; first make the directory if it doesn't exist
  (when-not (fs/exists? location)
    (fs/mkdir location))

  ;; set up the database
  (LMDBCache.
   (make-db location)))

(defn make-cache-from-db
  "Args:
   db: An existing lmdb instance"
  [db]
  (LMDBCache. db))
