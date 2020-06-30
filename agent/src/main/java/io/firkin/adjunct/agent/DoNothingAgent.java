package io.firkin.adjunct.agent;

import io.firkin.adjunct.Agent;
import io.firkin.adjunct.util.Configuration;

/**
 * The Default Agent when Adjunct Agent can't find anything
 */
public class DoNothingAgent implements Agent {

  @Override
  public void create(Configuration configuration) {

  }

  @Override
  public void update(Configuration changes) {

  }

  @Override
  public void start() {

  }

  @Override
  public void pause() {

  }

  @Override
  public void shutdown() {

  }
}
