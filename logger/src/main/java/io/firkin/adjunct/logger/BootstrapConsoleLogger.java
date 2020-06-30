package io.firkin.adjunct.logger;

public class BootstrapConsoleLogger implements BootstrapLogger {

  /*---------------------------------------<  Class   >-----------------------------------------<*/

  private static BootstrapConsoleLogger instance;

  public static BootstrapLogger create(String agentName) {
    if (instance == null) {
      instance = new BootstrapConsoleLogger(agentName);
    }
    return instance;
  }

  /*---------------------------------------< Instance >-----------------------------------------<*/

  private final String name;

  private BootstrapConsoleLogger(String agentName) {
    this.name = agentName;
  }

  public void write(String level, String line) {
    StringBuffer sb = new StringBuffer();
    if (name != null && !name.isEmpty()) sb.append(name).append(' ');
    if (level != null && !level.isEmpty()) sb.append(level).append(' ');
    System.out.println(sb.append(line).toString());
  }
}
