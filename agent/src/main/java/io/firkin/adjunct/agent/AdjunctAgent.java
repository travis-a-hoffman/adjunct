package io.firkin.adjunct.agent;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

import io.firkin.adjunct.Agent;
import io.firkin.adjunct.logger.BootstrapInMemoryLogger;
import io.firkin.adjunct.logger.BootstrapLogger;

public class AdjunctAgent implements Instrumentation {

  /*---------------------------------------<  Class   >-----------------------------------------<*/

  private static BootstrapLogger boot = BootstrapInMemoryLogger.create("AdjunctAgent", 1024, 256);
  private static Agent agent;

  // The primary entry point for Java Agents.
  public void premain(String args, Instrumentation instrumentation) {
    logBanner();

    // TODO Load Configurations

    // TODO Setup a real logger;

    // Find and load the defined Agent
    agent = new DoNothingAgent();

    // Find and load ClassTransformer

    logExit();
  }

  public static void logExit() {
    boot.log("AdjunctAgent v0.0.1 exiting.");
  }

  private static void logBanner() {
    for (String s: banner)
      boot.log(s);
  }

  // TODO Write a maven plugin to call out to this website, regenerate and spit out the code as an array or string.
  // Text: "Adjunct Agent v0.0.1" generated with: http://patorjk.com/software/taag/#p=display&f=Standard&t=Adjunct%20Agent%20v0.0.1
  private static final String banner[] = {
    "     _       _  _                  _        _                    _            ___   ___   _",
    "    / \\   __| |(_)_   _ _ __   ___| |_     / \\   __ _  ___ _ __ | |_  __   __/ _ \\ / _ \\ / |",
    "   / _ \\ / _` || | | | | '_ \\ / __| __|   / _ \\ / _` |/ _ \\ '_ \\| __| \\ \\ / / | | | | | || |",
    "  / ___ \\ (_| || | |_| | | | | (__| |_   / ___ \\ (_| |  __/ | | | |_   \\ V /| |_| | |_| || |",
    " /_/   \\_\\__,_|/ |\\__,_|_| |_|\\___|\\__| /_/   \\_\\__, |\\___|_| |_|\\__|   \\_/  \\___(_)___(_)_|",
    "             |__/                               |___/"
  };

  /*---------------------------------------< Instance >-----------------------------------------<*/

  /**
   * Registers the supplied transformer. All future class definitions
   * will be seen by the transformer, except definitions of classes upon which any
   * registered transformer is dependent.
   * The transformer is called when classes are loaded, when they are
   * {@linkplain #redefineClasses redefined}. and if <code>canRetransform</code> is true,
   * when they are {@linkplain #retransformClasses retransformed}.
   * See {@link ClassFileTransformer#transform
   * ClassFileTransformer.transform} for the order
   * of transform calls.
   * If a transformer throws
   * an exception during execution, the JVM will still call the other registered
   * transformers in order. The same transformer may be added more than once,
   * but it is strongly discouraged -- avoid this by creating a new instance of
   * transformer class.
   * <p>
   * This method is intended for use in instrumentation, as described in the
   * {@linkplain Instrumentation class specification}.
   *
   * @param transformer    the transformer to register
   * @param canRetransform can this transformer's transformations be retransformed
   * @throws NullPointerException          if passed a <code>null</code> transformer
   * @throws UnsupportedOperationException if <code>canRetransform</code>
   *                                       is true and the current configuration of the JVM does not allow
   *                                       retransformation ({@link #isRetransformClassesSupported} is false)
   * @since 1.6
   */
  @Override
  public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {

  }

  /**
   * Registers the supplied transformer.
   * <p>
   * Same as <code>addTransformer(transformer, false)</code>.
   *
   * @param transformer the transformer to register
   * @throws NullPointerException if passed a <code>null</code> transformer
   * @see #addTransformer(ClassFileTransformer, boolean)
   */
  @Override
  public void addTransformer(ClassFileTransformer transformer) {

  }

  /**
   * Unregisters the supplied transformer. Future class definitions will
   * not be shown to the transformer. Removes the most-recently-added matching
   * instance of the transformer. Due to the multi-threaded nature of
   * class loading, it is possible for a transformer to receive calls
   * after it has been removed. Transformers should be written defensively
   * to expect this situation.
   *
   * @param transformer the transformer to unregister
   * @return true if the transformer was found and removed, false if the
   * transformer was not found
   * @throws NullPointerException if passed a <code>null</code> transformer
   */
  @Override
  public boolean removeTransformer(ClassFileTransformer transformer) {
    return false;
  }

  /**
   * Returns whether or not the current JVM configuration supports retransformation
   * of classes.
   * The ability to retransform an already loaded class is an optional capability
   * of a JVM.
   * Retransformation will only be supported if the
   * <code>Can-Retransform-Classes</code> manifest attribute is set to
   * <code>true</code> in the agent JAR file (as described in the
   * {@linkplain java.lang.instrument package specification}) and the JVM supports
   * this capability.
   * During a single instantiation of a single JVM, multiple calls to this
   * method will always return the same answer.
   *
   * @return true if the current JVM configuration supports retransformation of
   * classes, false if not.
   * @see #retransformClasses
   * @since 1.6
   */
  @Override
  public boolean isRetransformClassesSupported() {
    return false;
  }

  /**
   * Retransform the supplied set of classes.
   *
   * <p>
   * This function facilitates the instrumentation
   * of already loaded classes.
   * When classes are initially loaded or when they are
   * {@linkplain #redefineClasses redefined},
   * the initial class file bytes can be transformed with the
   * {@link ClassFileTransformer ClassFileTransformer}.
   * This function reruns the transformation process
   * (whether or not a transformation has previously occurred).
   * This retransformation follows these steps:
   *  <ul>
   *    <li>starting from the initial class file bytes
   *    </li>
   *    <li>for each transformer that was added with <code>canRetransform</code>
   *      false, the bytes returned by
   *      {@link ClassFileTransformer#transform transform}
   *      during the last class load or redefine are
   *      reused as the output of the transformation; note that this is
   *      equivalent to reapplying the previous transformation, unaltered;
   *      except that
   *      {@link ClassFileTransformer#transform transform}
   *      is not called
   *    </li>
   *    <li>for each transformer that was added with <code>canRetransform</code>
   *      true, the
   *      {@link ClassFileTransformer#transform transform}
   *      method is called in these transformers
   *    </li>
   *    <li>the transformed class file bytes are installed as the new
   *      definition of the class
   *    </li>
   *  </ul>
   * <p>
   * <p>
   * The order of transformation is described in the
   * {@link ClassFileTransformer#transform transform} method.
   * This same order is used in the automatic reapplication of retransformation
   * incapable transforms.
   * <p>
   * <p>
   * The initial class file bytes represent the bytes passed to
   * // TODO Refer to javadocs? {link ClassLoader#defineClass ClassLoader.defineClass} or
   * {@link #redefineClasses redefineClasses}
   * (before any transformations
   *  were applied), however they might not exactly match them.
   *  The constant pool might not have the same layout or contents.
   *  The constant pool may have more or fewer entries.
   *  Constant pool entries may be in a different order; however,
   *  constant pool indices in the bytecodes of methods will correspond.
   *  Some attributes may not be present.
   *  Where order is not meaningful, for example the order of methods,
   *  order might not be preserved.
   *
   * <p>
   * This method operates on
   * a set in order to allow interdependent changes to more than one class at the same time
   * (a retransformation of class A can require a retransformation of class B).
   *
   * <p>
   * If a retransformed method has active stack frames, those active frames continue to
   * run the bytecodes of the original method.
   * The retransformed method will be used on new invokes.
   *
   * <p>
   * This method does not cause any initialization except that which would occur
   * under the customary JVM semantics. In other words, redefining a class
   * does not cause its initializers to be run. The values of static variables
   * will remain as they were prior to the call.
   *
   * <p>
   * Instances of the retransformed class are not affected.
   *
   * <p>
   * The retransformation may change method bodies, the constant pool and attributes.
   * The retransformation must not add, remove or rename fields or methods, change the
   * signatures of methods, or change inheritance.  These restrictions maybe be
   * lifted in future versions.  The class file bytes are not checked, verified and installed
   * until after the transformations have been applied, if the resultant bytes are in
   * error this method will throw an exception.
   *
   * <p>
   * If this method throws an exception, no classes have been retransformed.
   * <p>
   * This method is intended for use in instrumentation, as described in the
   * {@linkplain Instrumentation class specification}.
   *
   * @param classes array of classes to retransform;
   *                a zero-length array is allowed, in this case, this method does nothing
   * @throws UnmodifiableClassException    if a specified class cannot be modified
   *                                       ({@link #isModifiableClass} would return <code>false</code>)
   * @throws UnsupportedOperationException if the current configuration of the JVM does not allow
   *                                       retransformation ({@link #isRetransformClassesSupported} is false) or the retransformation attempted
   *                                       to make unsupported changes
   * @throws ClassFormatError              if the data did not contain a valid class
   * @throws NoClassDefFoundError          if the name in the class file is not equal to the name of the class
   * @throws UnsupportedClassVersionError  if the class file version numbers are not supported
   * @throws ClassCircularityError         if the new classes contain a circularity
   * @throws LinkageError                  if a linkage error occurs
   * @throws NullPointerException          if the supplied classes  array or any of its components
   *                                       is <code>null</code>.
   * @see #isRetransformClassesSupported
   * @see #addTransformer
   * @see ClassFileTransformer
   * @since 1.6
   */
  @Override
  public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {

  }

  /**
   * Returns whether or not the current JVM configuration supports redefinition
   * of classes.
   * The ability to redefine an already loaded class is an optional capability
   * of a JVM.
   * Redefinition will only be supported if the
   * <code>Can-Redefine-Classes</code> manifest attribute is set to
   * <code>true</code> in the agent JAR file (as described in the
   * {@linkplain java.lang.instrument package specification}) and the JVM supports
   * this capability.
   * During a single instantiation of a single JVM, multiple calls to this
   * method will always return the same answer.
   *
   * @return true if the current JVM configuration supports redefinition of classes,
   * false if not.
   * @see #redefineClasses
   */
  @Override
  public boolean isRedefineClassesSupported() {
    return false;
  }

  /**
   * Redefine the supplied set of classes using the supplied class files.
   *
   * <p>
   * This method is used to replace the definition of a class without reference
   * to the existing class file bytes, as one might do when recompiling from source
   * for fix-and-continue debugging.
   * Where the existing class file bytes are to be transformed (for
   * example in bytecode instrumentation)
   * {@link #retransformClasses retransformClasses}
   * should be used.
   *
   * <p>
   * This method operates on
   * a set in order to allow interdependent changes to more than one class at the same time
   * (a redefinition of class A can require a redefinition of class B).
   *
   * <p>
   * If a redefined method has active stack frames, those active frames continue to
   * run the bytecodes of the original method.
   * The redefined method will be used on new invokes.
   *
   * <p>
   * This method does not cause any initialization except that which would occur
   * under the customary JVM semantics. In other words, redefining a class
   * does not cause its initializers to be run. The values of static variables
   * will remain as they were prior to the call.
   *
   * <p>
   * Instances of the redefined class are not affected.
   *
   * <p>
   * The redefinition may change method bodies, the constant pool and attributes.
   * The redefinition must not add, remove or rename fields or methods, change the
   * signatures of methods, or change inheritance.  These restrictions maybe be
   * lifted in future versions.  The class file bytes are not checked, verified and installed
   * until after the transformations have been applied, if the resultant bytes are in
   * error this method will throw an exception.
   *
   * <p>
   * If this method throws an exception, no classes have been redefined.
   * <p>
   * This method is intended for use in instrumentation, as described in the
   * {@linkplain Instrumentation class specification}.
   *
   * @param definitions array of classes to redefine with corresponding definitions;
   *                    a zero-length array is allowed, in this case, this method does nothing
   * @throws UnmodifiableClassException    if a specified class cannot be modified
   *                                       ({@link #isModifiableClass} would return <code>false</code>)
   * @throws UnsupportedOperationException if the current configuration of the JVM does not allow
   *                                       redefinition ({@link #isRedefineClassesSupported} is false) or the redefinition attempted
   *                                       to make unsupported changes
   * @throws ClassFormatError              if the data did not contain a valid class
   * @throws NoClassDefFoundError          if the name in the class file is not equal to the name of the class
   * @throws UnsupportedClassVersionError  if the class file version numbers are not supported
   * @throws ClassCircularityError         if the new classes contain a circularity
   * @throws LinkageError                  if a linkage error occurs
   * @throws NullPointerException          if the supplied definitions array or any of its components
   *                                       is <code>null</code>
   * @throws ClassNotFoundException        Can never be thrown (present for compatibility reasons only)
   * @see #isRedefineClassesSupported
   * @see #addTransformer
   * @see ClassFileTransformer
   */
  @Override
  public void redefineClasses(ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException {

  }

  /**
   * Determines whether a class is modifiable by
   * {@linkplain #retransformClasses retransformation}
   * or {@linkplain #redefineClasses redefinition}.
   * If a class is modifiable then this method returns <code>true</code>.
   * If a class is not modifiable then this method returns <code>false</code>.
   * <p>
   * For a class to be retransformed, {@link #isRetransformClassesSupported} must also be true.
   * But the value of <code>isRetransformClassesSupported()</code> does not influence the value
   * returned by this function.
   * For a class to be redefined, {@link #isRedefineClassesSupported} must also be true.
   * But the value of <code>isRedefineClassesSupported()</code> does not influence the value
   * returned by this function.
   * <p>
   * Primitive classes (for example, <code>java.lang.Integer.TYPE</code>)
   * and array classes are never modifiable.
   *
   * @param theClass the class to check for being modifiable
   * @return whether or not the argument class is modifiable
   * @throws NullPointerException if the specified class is <code>null</code>.
   * @see #retransformClasses
   * @see #isRetransformClassesSupported
   * @see #redefineClasses
   * @see #isRedefineClassesSupported
   * @since 1.6
   */
  @Override
  public boolean isModifiableClass(Class<?> theClass) {
    return false;
  }

  /**
   * Returns an array of all classes currently loaded by the JVM.
   *
   * @return an array containing all the classes loaded by the JVM, zero-length if there are none
   */
  @Override
  public Class[] getAllLoadedClasses() {
    return new Class[0];
  }

  /**
   * Returns an array of all classes for which <code>loader</code> is an initiating loader.
   * If the supplied loader is <code>null</code>, classes initiated by the bootstrap class
   * loader are returned.
   *
   * @param loader the loader whose initiated class list will be returned
   * @return an array containing all the classes for which loader is an initiating loader,
   * zero-length if there are none
   */
  @Override
  public Class[] getInitiatedClasses(ClassLoader loader) {
    return new Class[0];
  }

  /**
   * Returns an implementation-specific approximation of the amount of storage consumed by
   * the specified object. The result may include some or all of the object's overhead,
   * and thus is useful for comparison within an implementation but not between implementations.
   * <p>
   * The estimate may change during a single invocation of the JVM.
   *
   * @param objectToSize the object to size
   * @return an implementation-specific approximation of the amount of storage consumed by the specified object
   * @throws NullPointerException if the supplied Object is <code>null</code>.
   */
  @Override
  public long getObjectSize(Object objectToSize) {
    return 0;
  }

  /**
   * Specifies a JAR file with instrumentation classes to be defined by the
   * bootstrap class loader.
   *
   * <p> When the virtual machine's built-in class loader, known as the "bootstrap
   * class loader", unsuccessfully searches for a class, the entries in the {@link
   * JarFile JAR file} will be searched as well.
   *
   * <p> This method may be used multiple times to add multiple JAR files to be
   * searched in the order that this method was invoked.
   *
   * <p> The agent should take care to ensure that the JAR does not contain any
   * classes or resources other than those to be defined by the bootstrap
   * class loader for the purpose of instrumentation.
   * Failure to observe this warning could result in unexpected
   * behavior that is difficult to diagnose. For example, suppose there is a
   * loader L, and L's parent for delegation is the bootstrap class loader.
   * Furthermore, a method in class C, a class defined by L, makes reference to
   * a non-public accessor class C$1. If the JAR file contains a class C$1 then
   * the delegation to the bootstrap class loader will cause C$1 to be defined
   * by the bootstrap class loader. In this example an <code>IllegalAccessError</code>
   * will be thrown that may cause the application to fail. One approach to
   * avoiding these types of issues, is to use a unique package name for the
   * instrumentation classes.
   *
   * <p>
   * <cite>The Java&trade; Virtual Machine Specification</cite>
   * specifies that a subsequent attempt to resolve a symbolic
   * reference that the Java virtual machine has previously unsuccessfully attempted
   * to resolve always fails with the same error that was thrown as a result of the
   * initial resolution attempt. Consequently, if the JAR file contains an entry
   * that corresponds to a class for which the Java virtual machine has
   * unsuccessfully attempted to resolve a reference, then subsequent attempts to
   * resolve that reference will fail with the same error as the initial attempt.
   *
   * @param jarfile The JAR file to be searched when the bootstrap class loader
   *                unsuccessfully searches for a class.
   * @throws NullPointerException If <code>jarfile</code> is <code>null</code>.
   * @see #appendToSystemClassLoaderSearch
   * @see ClassLoader
   * @see JarFile
   * @since 1.6
   */
  @Override
  public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {

  }

  /**
   * Specifies a JAR file with instrumentation classes to be defined by the
   * system class loader.
   * <p>
   * When the system class loader for delegation (see
   * {@link ClassLoader#getSystemClassLoader getSystemClassLoader()})
   * unsuccessfully searches for a class, the entries in the {@link
   * JarFile JarFile} will be searched as well.
   *
   * <p> This method may be used multiple times to add multiple JAR files to be
   * searched in the order that this method was invoked.
   *
   * <p> The agent should take care to ensure that the JAR does not contain any
   * classes or resources other than those to be defined by the system class
   * loader for the purpose of instrumentation.
   * Failure to observe this warning could result in unexpected
   * behavior that is difficult to diagnose (see
   * {@link #appendToBootstrapClassLoaderSearch
   * appendToBootstrapClassLoaderSearch}).
   *
   * <p> The system class loader supports adding a JAR file to be searched if
   * it implements a method named <code>appendToClassPathForInstrumentation</code>
   * which takes a single parameter of type <code>java.lang.String</code>. The
   * method is not required to have <code>public</code> access. The name of
   * the JAR file is obtained by invoking the {@link ZipFile#getName
   * getName()} method on the <code>jarfile</code> and this is provided as the
   * parameter to the <code>appendToClassPathForInstrumentation</code> method.
   *
   * <p>
   * <cite>The Java&trade; Virtual Machine Specification</cite>
   * specifies that a subsequent attempt to resolve a symbolic
   * reference that the Java virtual machine has previously unsuccessfully attempted
   * to resolve always fails with the same error that was thrown as a result of the
   * initial resolution attempt. Consequently, if the JAR file contains an entry
   * that corresponds to a class for which the Java virtual machine has
   * unsuccessfully attempted to resolve a reference, then subsequent attempts to
   * resolve that reference will fail with the same error as the initial attempt.
   *
   * <p> This method does not change the value of <code>java.class.path</code>
   * {@link System#getProperties system property}.
   *
   * @param jarfile The JAR file to be searched when the system class loader
   *                unsuccessfully searches for a class.
   * @throws UnsupportedOperationException If the system class loader does not support appending a
   *                                       a JAR file to be searched.
   * @throws NullPointerException          If <code>jarfile</code> is <code>null</code>.
   * @see #appendToBootstrapClassLoaderSearch
   * @see ClassLoader#getSystemClassLoader
   * @see JarFile
   * @since 1.6
   */
  @Override
  public void appendToSystemClassLoaderSearch(JarFile jarfile) {

  }

  /**
   * Returns whether the current JVM configuration supports
   * {@linkplain #setNativeMethodPrefix(ClassFileTransformer, String)
   * setting a native method prefix}.
   * The ability to set a native method prefix is an optional
   * capability of a JVM.
   * Setting a native method prefix will only be supported if the
   * <code>Can-Set-Native-Method-Prefix</code> manifest attribute is set to
   * <code>true</code> in the agent JAR file (as described in the
   * {@linkplain java.lang.instrument package specification}) and the JVM supports
   * this capability.
   * During a single instantiation of a single JVM, multiple
   * calls to this method will always return the same answer.
   *
   * @return true if the current JVM configuration supports
   * setting a native method prefix, false if not.
   * @see #setNativeMethodPrefix
   * @since 1.6
   */
  @Override
  public boolean isNativeMethodPrefixSupported() {
    return false;
  }

  /**
   * This method modifies the failure handling of
   * native method resolution by allowing retry
   * with a prefix applied to the name.
   * When used with the
   * {@link ClassFileTransformer ClassFileTransformer},
   * it enables native methods to be
   * instrumented.
   * <p>
   * Since native methods cannot be directly instrumented
   * (they have no bytecodes), they must be wrapped with
   * a non-native method which can be instrumented.
   * For example, if we had:
   * <pre>
   *   native boolean foo(int x);</pre>
   * <p>
   * We could transform the class file (with the
   * ClassFileTransformer during the initial definition
   * of the class) so that this becomes:
   * <pre>
   *   boolean foo(int x) {
   *     <i>... record entry to foo ...</i>
   *     return wrapped_foo(x);
   *   }
   *
   *   native boolean wrapped_foo(int x);</pre>
   * <p>
   * Where <code>foo</code> becomes a wrapper for the actual native
   * method with the appended prefix "wrapped_".  Note that
   * "wrapped_" would be a poor choice of prefix since it
   * might conceivably form the name of an existing method
   * thus something like "$$$MyAgentWrapped$$$_" would be
   * better but would make these examples less readable.
   * <p>
   * The wrapper will allow data to be collected on the native
   * method call, but now the problem becomes linking up the
   * wrapped method with the native implementation.
   * That is, the method <code>wrapped_foo</code> needs to be
   * resolved to the native implementation of <code>foo</code>,
   * which might be:
   * <pre>
   *   Java_somePackage_someClass_foo(JNIEnv* env, jint x)</pre>
   * <p>
   * This function allows the prefix to be specified and the
   * proper resolution to occur.
   * Specifically, when the standard resolution fails, the
   * resolution is retried taking the prefix into consideration.
   * There are two ways that resolution occurs, explicit
   * resolution with the JNI function <code>RegisterNatives</code>
   * and the normal automatic resolution.  For
   * <code>RegisterNatives</code>, the JVM will attempt this
   * association:
   * <pre>{@code
   *   method(foo) -> nativeImplementation(foo)
   * }</pre>
   * <p>
   * When this fails, the resolution will be retried with
   * the specified prefix prepended to the method name,
   * yielding the correct resolution:
   * <pre>{@code
   *   method(wrapped_foo) -> nativeImplementation(foo)
   * }</pre>
   * <p>
   * For automatic resolution, the JVM will attempt:
   * <pre>{@code
   *   method(wrapped_foo) -> nativeImplementation(wrapped_foo)
   * }</pre>
   * <p>
   * When this fails, the resolution will be retried with
   * the specified prefix deleted from the implementation name,
   * yielding the correct resolution:
   * <pre>{@code
   *   method(wrapped_foo) -> nativeImplementation(foo)
   * }</pre>
   * <p>
   * Note that since the prefix is only used when standard
   * resolution fails, native methods can be wrapped selectively.
   * <p>
   * Since each <code>ClassFileTransformer</code>
   * can do its own transformation of the bytecodes, more
   * than one layer of wrappers may be applied. Thus each
   * transformer needs its own prefix.  Since transformations
   * are applied in order, the prefixes, if applied, will
   * be applied in the same order
   * (see {@link #addTransformer(ClassFileTransformer, boolean) addTransformer}).
   * Thus if three transformers applied
   * wrappers, <code>foo</code> might become
   * <code>$trans3_$trans2_$trans1_foo</code>.  But if, say,
   * the second transformer did not apply a wrapper to
   * <code>foo</code> it would be just
   * <code>$trans3_$trans1_foo</code>.  To be able to
   * efficiently determine the sequence of prefixes,
   * an intermediate prefix is only applied if its non-native
   * wrapper exists.  Thus, in the last example, even though
   * <code>$trans1_foo</code> is not a native method, the
   * <code>$trans1_</code> prefix is applied since
   * <code>$trans1_foo</code> exists.
   *
   * @param transformer The ClassFileTransformer which wraps using this prefix.
   * @param prefix      The prefix to apply to wrapped native methods when
   *                    retrying a failed native method resolution. If prefix
   *                    is either <code>null</code> or the empty string, then
   *                    failed native method resolutions are not retried for
   *                    this transformer.
   * @throws NullPointerException          if passed a <code>null</code> transformer.
   * @throws UnsupportedOperationException if the current configuration of
   *                                       the JVM does not allow setting a native method prefix
   *                                       ({@link #isNativeMethodPrefixSupported} is false).
   * @throws IllegalArgumentException      if the transformer is not registered
   *                                       (see {@link #addTransformer(ClassFileTransformer, boolean) addTransformer}).
   * @since 1.6
   */
  @Override
  public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {

  }
}