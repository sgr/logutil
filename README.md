# logutil

A Clojure library designed to use Java Logging API easily.
It provides several utility classes and functions.

## Install

logutil is available in [Clojars.org](https://clojars.org/logutil).
Your leiningen project.clj:

   [logutil "0.2.2"]

## Usage

See [doc/example.clj](https://github.com/sgr/logutil/blob/master/example.clj).

## Documentation

See [API Document](http://sgr.github.io/logutil/).

## Classes

### logutil.Log4JLikeFormatter

It extends java.util.logging.Formatter.

### logutil.LazyFileHandler

It extends java.util.logging.StreamHandler.
It doesn't create log file until published LogRecord, so that it is used with MemoryHandler.

* logutil.LazyFileHandler.level specifies the default level for the handler (defaults to Level/ALL).
* logutil.LazyFileHandler.formatter specifies the name of a formatter class to use (defaults to java.util.logging.SimpleFormatter).
* logutil.LazyFileHandler.path specifies the name of log file.

## License

Copyright (C) Shigeru Fujiwara All Rights Reserved.

Distributed under the Eclipse Public License, the same as Clojure.
