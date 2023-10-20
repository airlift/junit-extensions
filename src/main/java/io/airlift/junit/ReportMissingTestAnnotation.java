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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportMissingTestAnnotation
        implements TestInstancePreConstructCallback
{
    @Override
    public void preConstructTestInstance(TestInstanceFactoryContext factoryContext, ExtensionContext extensionContext)
            throws MissingAnnotationsException
    {
        reportMissingTestAnnotations(factoryContext.getTestClass());
    }

    // visible for testing
    static void reportMissingTestAnnotations(Class<?> testClass)
            throws MissingAnnotationsException
    {
        Class<?> current = testClass;
        Set<Failure> failures = new HashSet<>();
        while (current.getSuperclass() != null) {
            List<Method> candidates = Arrays.stream(current.getDeclaredMethods())
                    .filter(method -> !Modifier.isStatic(method.getModifiers()))
                    .filter(method -> !method.isBridge())
                    .filter(method -> !method.isSynthetic())
                    .filter(method -> !method.isAnnotationPresent(Test.class) && !method.isAnnotationPresent(RepeatedTest.class))
                    .collect(Collectors.toList());

            for (Method candidate : candidates) {
                Optional<Method> annotatedBase = getOverridden(candidate).stream()
                        .filter(method -> method.isAnnotationPresent(Test.class) || method.isAnnotationPresent(RepeatedTest.class))
                        .findFirst();

                annotatedBase.ifPresent(method -> failures.add(new Failure(candidate, method)));
            }

            current = current.getSuperclass();
        }

        if (!failures.isEmpty()) {
            throw new MissingAnnotationsException(testClass, failures);
        }
    }

    private static List<Method> getOverridden(Method method)
    {
        List<Method> result = new ArrayList<>();
        Class<?> clazz = method.getDeclaringClass();
        while (clazz.getSuperclass() != null) {
            try {
                result.add(clazz.getSuperclass().getDeclaredMethod(method.getName(), method.getParameterTypes()));
            }
            catch (NoSuchMethodException e) {
            }

            clazz = clazz.getSuperclass();
        }

        return result;
    }

    public record Failure(Method child, Method parent) {}
}
