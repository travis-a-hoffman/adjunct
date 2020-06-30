package io.firkin.adjunct;

import io.firkin.adjunct.util.Configuration;
import io.firkin.adjunct.util.Version;

public interface Agent {

  Version version = Version.alpha();
  Configuration config = new Configuration();

  void create(Configuration configuration);

  void update(Configuration changes);

  void start();

  void pause();

  void shutdown();
}