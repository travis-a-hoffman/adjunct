package io.firkin.adjunct.logger;

public interface BootstrapLogger {

  default void log(String line) {
    write(line);
  }

  default void trace(String line) {
    write("[TRACE]", line);
  }

  default void info(String line) {
    write("[INFO ]", line);
  }

  default void warn(String line) {
    write("[WARN ]", line);
  }

  default void error(String line) {
    write("[ERROR]", line);
  }

  default void fatal(String line) {
    write("[FATAL]", line);
  }

  default void write(String line) {
    write(null, line);
  }

  void write(String prefix, String line);
}
