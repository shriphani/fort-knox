(ns fort-knox.core
  (:require [clojure.core.cache :refer :all]
            [clj-lmdb.simple :refer :all]
            [me.raynes.fs :as fs]))

(defcache LMDBCache
  [cache]
  CacheProtocol
  (lookup
   [_ item]
   (with-txn [rtxn (read-txn cache)]
     (get! cache
           rtxn
           (name item))))

  (lookup
   [_ item not-found]
   (with-txn [rtxn (read-txn cache)]
     (or (get! cache
               rtxn
               (name item))
         not-found)))

  (has?
   [_ item]
   (with-txn [rtxn (read-txn cache)]
     (-> (get! cache
               rtxn
               (name item))
         nil?
         not)))

  (hit
   [cache item]
   cache)

  (miss
   [_ item result]
   (with-txn [wtxn (write-txn cache)]
     (put! cache
           wtxn
           (name item)
           result))
   (LMDBCache. cache))
  
  (evict
   [_ k]
   (with-txn [wtxn (write-txn cache)]
     (delete! cache wtxn (name k)))
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
