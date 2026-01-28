/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.airlift.junit;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstancePreConstructCallback;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ReportStaticTestMethod
        implements TestInstancePreConstructCallback
{
    @Override
    public void preConstructTestInstance(TestInstanceFactoryContext factoryContext, ExtensionContext extensionContext)
    {
        Class<?> testClass = factoryContext.getTestClass();
        reportStaticTestMethod(testClass);
    }

    // visible for testing
    static void reportStaticTestMethod(Class<?> testClass)
    {
        for (Class<?> current = testClass; current != Object.class; current = current.getSuperclass()) {
            List<Method> annotatedStatics = Arrays.stream(current.getDeclaredMethods())
                    .filter(method -> Modifier.isStatic(method.getModifiers()))
                    .filter(ReportStaticTestMethod::isAnnotatedTest)
                    .toList();

            if (!annotatedStatics.isEmpty()) {
                throw new IllegalStateException("The following methods are annotated as tests but static. JUnit ignores static methods even if annotated. " + annotatedStatics);
            }
        }
    }

    private static boolean isAnnotatedTest(Method method)
    {
        if (method.isAnnotationPresent(Test.class)) {
            return true;
        }
        if (method.isAnnotationPresent(RepeatedTest.class)) {
            return true;
        }
        return Stream.of(method.getAnnotations())
                // @ParameterizedTest is not a compile-time dependency
                .anyMatch(annotation -> "org.junit.jupiter.params.ParameterizedTest".equals(annotation.annotationType().getName()));
    }
}
