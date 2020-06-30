package io.firkin.adjunct.logger;

import io.firkin.adjunct.c9s.CharArrayBuffer;
import io.firkin.adjunct.c9s.LogBuffer;

/**
 * A specialized logger that writes to an in-memory buffer, and then may be flushed to a more
 * standard (full-featured) logger once that's available.
 */
public final class BootstrapInMemoryLogger implements BootstrapLogger {

  /*---------------------------------------<  Class   >-----------------------------------------<*/

  private static BootstrapInMemoryLogger instance;

  public static BootstrapLogger create(String name) {
    return create(null, 1024, 256);
  }

  public static BootstrapLogger create(String name, int nLines) {
    return create(name, nLines, 256);
  }

  public static BootstrapLogger create(String name, int nLines, int nCharsPerLine) {
    // TODO make the choice of backing Buffer Configurable.
    if (instance == null) {
      instance = new BootstrapInMemoryLogger(CharArrayBuffer.create(nLines, nCharsPerLine), name);
    }
    return instance;
  }

  /*---------------------------------------< Instance >-----------------------------------------<*/

  private final LogBuffer buffer;
  private final String name;

  private BootstrapInMemoryLogger(LogBuffer buffer) {
    this.buffer = buffer;
    this.name = null;
  }

  private BootstrapInMemoryLogger(LogBuffer buffer, String agentName) {
    this.buffer = buffer;
    this.name = agentName;
  }

  public void write(String level, String line) {
    if (buffer.isFull()) return;
    StringBuffer sb = new StringBuffer();
    if (name != null && !name.isEmpty()) sb.append(name).append(' ');
    if (level != null && !level.isEmpty()) sb.append(level).append(' ');

    buffer.push(sb.append(line).toString().toCharArray());
  }

}