package com.code_intelligence.jazzer.junit;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import org.junit.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

public class JazzerTestEngineTest {
  @Test
  public void testExamples() {
    EngineTestKit
        .engine(new JazzerTestEngine())
        .selectors(selectClass(ExampleFuzzTests.class))
        .execute()
        .testEvents()
        .assertStatistics(stats -> stats
            .started(2)
            .succeeded(1)
            .failed(1));
  }
}
