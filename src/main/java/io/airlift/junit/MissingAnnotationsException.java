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

import java.util.Set;
import java.util.stream.Collectors;

class MissingAnnotationsException
        extends Exception
{
    private final Class<?> testClass;
    private final Set<ReportMissingTestAnnotation.Failure> failures;

    public MissingAnnotationsException(Class<?> testClass, Set<ReportMissingTestAnnotation.Failure> failures)
    {
        this.testClass = testClass;
        this.failures = failures;
    }

    public Class<?> getTestClass()
    {
        return testClass;
    }

    public Set<ReportMissingTestAnnotation.Failure> getFailures()
    {
        return failures;
    }

    @Override
    public String toString()
    {
        return "Failed to instantiate " + testClass.getName() + " due to missing @Test annotation on the following methods:\n" +
                failures.stream()
                        .map(failure -> "    '" + failure.child() + "', which overrides '" + failure.parent() + "'")
                        .collect(Collectors.joining("\n"));
    }
}
