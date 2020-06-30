package io.firkin.adjunct.c9s;

public interface LogBuffer {

  char[] emptyLine = new char[0];

  default boolean isFull() { return false; }

  default boolean isEmpty() { return true; }

  default void push(char[] line) { }

  default char[] pop() { return emptyLine; }

  default void deallocate() { }

}
