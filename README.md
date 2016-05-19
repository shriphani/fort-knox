# fort-knox

An implementation of `core.cache` that saves entries to disk.
This implementation uses the fantastic LMDB.

[![Circle CI](https://circleci.com/gh/shriphani/fort-knox.svg?style=shield&circle-token=d6290287207f1a753a288eb4363c9ce8a1d0f3d9)](https://circleci.com/gh/shriphani/fort-knox)


[![Clojars Project](https://img.shields.io/clojars/v/fort-knox.svg)](https://clojars.org/fort-knox)


**DISCLAIMER** typical `core.cache` implementations use persistent
and associative data structures. Since a disk-backed key-value
store doesn't have any such concept, this implementation might
not conform to expected behavior.

## Usage

Import this implementation:

```clojure
user> (use 'fort-knox.core :reload-all)
nil
```

Create a cache:

```clojure
user> (def cache (make-cache "/tmp"))
#'user/cache
```

Everything else is from `core.cache`:

```clojure
user> (cache/has? cache "foo")
false
user> (cache/miss cache "foo" "bar")
{:env #object[org.fusesource.lmdbjni.Env 0x6e09d885 "org.fusesource.lmdbjni.Env@6e09d885"], :db #object[org.fusesource.lmdbjni.Database 0x65807b47 "org.fusesource.lmdbjni.Database@65807b47"]}
user> (cache/has? cache "foo")
true
```

## License

Copyright © 2016 Shriphani Palakodety

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
