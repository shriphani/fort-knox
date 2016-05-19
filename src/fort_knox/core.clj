(ns fort-knox.core
  (:require [clojure.core.cache :refer :all]
            [clj-lmdb.simple :refer :all]))

(defcache PersistentCache
  [cache]
  CacheProtocol
  (lookup
   [_ item]
   (get! cache item))

  (lookup
   [_ item not-found]
   (or (get! cache item)
       not-found))

  (has?
   [_ item]
   (-> item
       (get! cache)
       nil?
       not))

  (hit
   [cache item]
   cache)

  (miss
   [cache item result]
   (put! cache item result)
   (PersistentCache. cache))

  (evict
   [_ k]
   (delete! cache k)
   (PersistentCache. cache))

  (seed
   [_ base]
   (PersistentCache. base)))
