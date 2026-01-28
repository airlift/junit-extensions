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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.airlift.junit.ReportStaticTestMethod.reportStaticTestMethod;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TestReportStaticTestMethod
{
    @Test
    public void test()
    {
        assertThatThrownBy(() -> reportStaticTestMethod(StaticTestMethod.class))
                .hasMessage("The following methods are annotated as tests but static. JUnit ignores static methods even if annotated. [static void io.airlift.junit.TestReportStaticTestMethod$StaticTestMethod.test()]");
        assertThatThrownBy(() -> reportStaticTestMethod(StaticRepeatedTestMethod.class))
                .hasMessage("The following methods are annotated as tests but static. JUnit ignores static methods even if annotated. [static void io.airlift.junit.TestReportStaticTestMethod$StaticRepeatedTestMethod.test()]");
        assertThatThrownBy(() -> reportStaticTestMethod(StaticParameterizedTestMethod.class))
                .hasMessage("The following methods are annotated as tests but static. JUnit ignores static methods even if annotated. [static void io.airlift.junit.TestReportStaticTestMethod$StaticParameterizedTestMethod.test(int)]");
    }

    @Test
    public void testNoStaticTestMethod()
    {
        reportStaticTestMethod(NonStaticTestMethod.class);
    }

    static class NonStaticTestMethod
    {
        @Test
        void test() {}
    }

    static class StaticTestMethod
    {
        @Test
        static void test() {}
    }

    static class StaticRepeatedTestMethod
    {
        @RepeatedTest(2)
        static void test() {}
    }

    static class StaticParameterizedTestMethod
    {
        @ParameterizedTest
        @ValueSource(ints = {1, 2})
        static void test(int x) {}
    }
}
