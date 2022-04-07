package com.code_intelligence.jazzer.junit;

import java.lang.reflect.Method;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

public class JazzerFuzzTestDescriptor extends AbstractTestDescriptor {

  private final Method method;

  public JazzerFuzzTestDescriptor(UniqueId uniqueId, Method method) {
    super(uniqueId.append("method", method.getClass().getName() + "." + method.getName()), method.getName());
    this.method = method;
  }

  @Override
  public Type getType() {
    return Type.TEST;
  }
}
