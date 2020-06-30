package io.firkin.adjunct.asm;

import io.firkin.adjunct.agent.AdjunctAgent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class AdjunctClassFileTransformer implements ClassFileTransformer {

  protected AdjunctAgent agent;

  @Override
  public byte[] transform(ClassLoader loader,
                          String className,
                          Class<?> classBeingRedefined,
                          ProtectionDomain protectionDomain,
                          byte[] classfileBuffer)
      throws IllegalClassFormatException {
    return classfileBuffer;
  }
}
