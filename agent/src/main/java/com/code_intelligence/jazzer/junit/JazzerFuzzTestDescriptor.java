package com.code_intelligence.jazzer.junit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

public class JazzerFuzzTestDescriptor extends AbstractTestDescriptor {

  private final Method method;

  public JazzerFuzzTestDescriptor(UniqueId uniqueId, Method method) {
    super(uniqueId.append("method", method.getClass().getName() + "." + method.getName()),
        method.getDeclaringClass().getSimpleName() + "." + method.getName());
    this.method = method;
  }

  @Override
  public Type getType() {
    return Type.TEST;
  }

  public void execute()
      throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
    method.setAccessible(true);
    byte[] data;
    if (ThreadLocalRandom.current().nextBoolean()) {
      data = new byte[]{0};
    } else {
      data = new byte[]{};
    }
    if (Modifier.isStatic(method.getModifiers())) {
      method.invoke(null, (Object) data);
    } else {
      Constructor<?> defaultConstructor = method.getDeclaringClass().getDeclaredConstructor();
      defaultConstructor.setAccessible(true);
      method.invoke(defaultConstructor.newInstance(), (Object) data);
    }
  }
}
