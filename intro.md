# Introduction to logutil

## Log4JLikeFormatter

It extends java.util.logging.Formatter.

## LazyFileHandler

It extends java.util.logging.StreamHandler.
It doesn't create log file until published LogRecord, so that it is used with MemoryHandler.

* logutil.LazyFileHandler.level specifies the default level for the handler (defaults to Level/ALL).
* logutil.LazyFileHandler.formatter specifies the name of a formatter class to use (defaults to java.util.logging.SimpleFormatter).
* logutil.LazyFileHandler.path specifies the name of log file.

## configure-logging

It is a utility function to reinitialize logging configuration from a property map.

