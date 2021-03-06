(ns fort-knox.core-test
  (:require [clojure.test :refer :all]
            [fort-knox.core :refer :all]
            [clojure.core.cache :as cache]
            [me.raynes.fs :as fs]))

(deftest lmdb-cache-test
  (testing "Start with an empty cache, add a bunch of things ^.^"

    (let [path (fs/temp-name "tmp-cache")
          the-cache (make-cache path :type :lmdb)]
      (is
       (not
        (cache/has? the-cache
                    "foo")))


      (cache/miss the-cache
                  "foo"
                  "bar")
      (is
       (cache/has? the-cache
                   "foo"))

      (is
       (= "bar"
          (cache/lookup the-cache
                        "foo")))

      (fs/delete-dir path))))

(deftest leveldb-cache-test
  (testing "Start with an empty cache, add a bunch of things ^.^"

    (let [path (fs/temp-name "tmp-cache")
          the-cache (make-cache path :type :leveldb)]
      (is
       (not
        (cache/has? the-cache
                    "foo")))


      (cache/miss the-cache
                  "foo"
                  "bar")
      (is
       (cache/has? the-cache
                   "foo"))

      (is
       (= "bar"
          (cache/lookup the-cache
                        "foo")))

      (fs/delete-dir path))))
