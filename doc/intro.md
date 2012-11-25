# Introduction to logutil

## Log4JLikeFormatter

It extends java.util.logging.Formatter.
To use it, You may call Handler#setFormatter.

```Clojure
  (doto (FileHandler. "%t/foo_%g.log" true)
    (.setFormatter (logutil.Log4JLikeFormatter.)))
```

## LazyFileHandler

It extends java.util.logging.StreamHandler.
It doesn't create log file until published LogRecord, so that it is used with MemoryHandler.

```Clojure
  (let [lh (logutil.LazyFileHandler. "tmp.log" (logutil.Log4JLikeFormatter.))
        mh (doto (MemoryHandler. lh 1000 Level/SEVERE)
             (.setLevel Level/ALL))]
    (init-root-handler mh))
```

"init-root-handler" is a utility function to set your handler to root handler.
