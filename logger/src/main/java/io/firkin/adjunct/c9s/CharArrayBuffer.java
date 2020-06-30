package io.firkin.adjunct.c9s;

/**
 * A dead-simple (primative char only) fixed line size, fixed number of lines ring buffer which
 * trim lines to the maximum length. Thus, the buffer is pre-allocated to the specified size,
 * and never exceeds the allocated memory. A very nice quality in an agent that wants to limit
 * memory utilization at startup.
 *
 * Additionally, this is written to avoid loading any classes; in an Agent, we want to minimize
 * the set of classes which are loaded before the Agent gets a chance to register a class
 * transformer. And if any classes do need to be loaded, this gives us the best chance to get
 * them loaded in the same order.
 *
 * Why do we want this at all? When writing an Agent, logging is is much simpler to allow the
 * agent to use the default logging framework configured by the Client App's. That way, the
 * agent log messages are logged to the same location as the Client App's logs.
 *
 * The downside is that Agents load before any Client code, including the logging classes.
 * To allow the Agent to capture its logging as soon as possible, we need a temporary buffer
 * during initialization that we can flush to log, and then write to the "real log" once it
 * has been loaded.
 *
 * Configuration Parameters:
 *   {@code io.firkin.adjunct.log.buffer.line.size}   The maximimum line size (in chars).
 *   {@code io.firkin.adjunct.log.buffer.line.count}  The maximum number of lines to store.
 */
public class CharArrayBuffer implements LogBuffer {

  /*---------------------------------------<  Class   >-----------------------------------------<*/

  public static CharArrayBuffer instance;

  private CharArrayBuffer(int nLines, int nCharsPerLine) {
    maxCharIdx = nCharsPerLine - 1;
    maxLineIdx = nLines - 1;
    currLine = 0;
    buffer = new char[nLines][nCharsPerLine];
  }

  private CharArrayBuffer() {
    this(1024, 256);
  }

  /**
   * Creates and instantiates the Singleton LogBuffer
   *
   * @param nLines          The maximum number of lines to store.
   * @param nCharsPerLine   The maximum number of characters per line.
   */
  public static LogBuffer create(int nLines, int nCharsPerLine) {
    if (instance == null) {
      instance = new CharArrayBuffer(nLines, nCharsPerLine);
    }
    return instance;
  }

  /**
   * Frees memory (as much as possible)
   */
  public static void release() {
    instance.deallocate();
  }

  /**
   * Write a line to the log buffer.
   *
   * @param line   Can be any length, but each line will be trimmed to maximum
   */
  public static void write(char[] line) {
    instance.push(line);
  }

  /**
   * Read a line from the buffer. Once read, the line will be cleared from the buffer. Use {@code isEmpty()}
   * to decide whether more should be read.
   *
   * @return The line of output or the empty String.
   */
  public static char[] read() {
    return instance.pop();
  }

  private int maxCharIdx;   // Maximum allowed index in a line array (buffer[line].length - 1)
  private int currLine;     // The next line be written to or (when overflowed) read from.
  private int maxLineIdx;   // Maximum allowed line in the buffer (buffer.length - 1)
  private boolean popping;  // The caller can only write once the
  private char[][] buffer;

  /*---------------------------------------<  Class   >-----------------------------------------<*/

  /**
   * Determines whether the buffer is full, meaning additional calls to {@code write(String)} will
   * cause the buffer to start to overwrite lines.
   */
  @Override
  public boolean isFull() {
    return currLine >= maxLineIdx || buffer[currLine][0] != '\u0000';
  }

  /**
   * Determines whether there are more lines to be read.
   *
   * @return {@code true} if there is more to read, {@code false} otherwise.
   */
  @Override
  public boolean isEmpty() {
    return buffer[currLine][0] != '\u0000';
  }

  @Override
  public void push(char[] line) {
    if (popping) return;
    int minLineLength = line.length > buffer[currLine].length? line.length: buffer[currLine].length;
    for (int i = 0; i < minLineLength; i++) {
      buffer[currLine][i] = line[i];
    }

    if (minLineLength < buffer[currLine].length) {
      // No need to clear to end of line; it's the buffer's caller's responsibility to stop at '\u0000' or end of array.
      buffer[currLine][minLineLength + 1] = '\u0000';
    } else if (line.length > buffer[currLine].length) {
      // Replace last char with Horizontal Ellipsis '…' (\u2026) to indicate the line has been trimmed.
      buffer[currLine][maxCharIdx] = '…';
    }

    currLine = (currLine < maxLineIdx)? currLine+1: 0;
  }

  @Override
  public char[] pop() {
    int retLength, retMaxIdx = 0;
    popping = true; // Once you pop, you can't stop.

    // We use '\u0000' to mark the "end of text" because the logged line could contain newline characters.
    while (retMaxIdx < maxLineIdx && buffer[currLine][retMaxIdx] != '\u0000')
      retMaxIdx++;

    retLength = (retMaxIdx <= maxLineIdx)? maxLineIdx: retMaxIdx;

    char[] response = new char[retLength];

    for (int i = 0; i < retLength; i++)
      response[i] = buffer[currLine][i];

    // Mark the line as "read".
    buffer[currLine][0] = '\u0000';

    currLine = (currLine < maxLineIdx)? currLine+1: 0;

    // TODO When the caller has popped the last line, automatically free the space.
    return response;
  }

  @Override
  public void deallocate() {
    buffer = new char[][] {};
    maxLineIdx = -1;
    maxCharIdx = -1;
  }
}
