package com.code_intelligence.jazzer.junit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Predicate;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

public class JazzerTestEngine implements TestEngine {

  private static final Predicate<Method> IS_FUZZ_TEST = JazzerTestEngine::isFuzzTest;

  @Override
  public String getId() {
    return "jazzer-junit";
  }

  @Override
  public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
    TestDescriptor engineDescriptor = new EngineDescriptor(uniqueId, "CI Fuzz");

    request.getSelectorsByType(ClassSelector.class).forEach(selector -> appendFuzzTestsInClass(selector.getJavaClass(), engineDescriptor));

    request.getSelectorsByType(MethodSelector.class).forEach(selector -> {
      if (isFuzzTest(selector.getJavaMethod())) {
        engineDescriptor.addChild(new JazzerFuzzTestDescriptor(engineDescriptor.getUniqueId(), selector.getJavaMethod()));
      }
    });

    return engineDescriptor;
  }

  private void appendFuzzTestsInClass(Class<?> javaClass, TestDescriptor engineDescriptor) {
    ReflectionSupport.findMethods(javaClass, IS_FUZZ_TEST, HierarchyTraversalMode.TOP_DOWN)
        .stream()
        .map(method -> new JazzerFuzzTestDescriptor(engineDescriptor.getUniqueId(), method))
        .forEach(engineDescriptor::addChild);
  }

  @Override
  public void execute(ExecutionRequest executionRequest) {
    EngineExecutionListener listener = executionRequest.getEngineExecutionListener();
    executionRequest.getRootTestDescriptor().accept(testDescriptor -> {
      if (!(testDescriptor instanceof JazzerFuzzTestDescriptor)) return;
      listener.executionStarted(testDescriptor);
      Instant start = Instant.now();
      while (Instant.now().isBefore(start.plus(Duration.ofSeconds(1)))) {
      }
      listener.reportingEntryPublished(testDescriptor,
          ReportEntry.from("Fuzzing " + testDescriptor.getDisplayName(), "#1000 exec/s: 100"));
      while (Instant.now().isBefore(start.plus(Duration.ofSeconds(2)))) {
      }
      listener.reportingEntryPublished(testDescriptor,
          ReportEntry.from("Fuzzing " + testDescriptor.getDisplayName(), "#2000 exec/s: 100"));
      while (Instant.now().isBefore(start.plus(Duration.ofSeconds(3)))) {
      }
      listener.reportingEntryPublished(testDescriptor,
          ReportEntry.from("Fuzzing " + testDescriptor.getDisplayName(), "#3000 exec/s: 100"));
      while (Instant.now().isBefore(start.plus(Duration.ofSeconds(4)))) {
      }
      try {
        ((JazzerFuzzTestDescriptor) testDescriptor).execute();
        listener.executionFinished(testDescriptor, TestExecutionResult.successful());
      } catch (Throwable t) {
        listener.executionFinished(testDescriptor, TestExecutionResult.failed(t));
      }
    });

  }

  @Override
  public Optional<String> getGroupId() {
    return Optional.of("com.code-intelligence");
  }

  @Override
  public Optional<String> getArtifactId() {
    return Optional.of("jazzer");
  }

  @Override
  public Optional<String> getVersion() {
    return Optional.of("1.0");
  }

  private static boolean isFuzzTest(Method candidate) {
    if (Modifier.isPrivate(candidate.getModifiers())) {
      return false;
    }
    if (AnnotationSupport.isAnnotated(candidate, FuzzTest.class)) {
      return true;
    }
    return candidate.getName().equals("fuzzerTestOneInput");
  }
}
