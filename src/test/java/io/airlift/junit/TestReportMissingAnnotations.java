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

import io.airlift.junit.ReportMissingTestAnnotation.Failure;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TestReportMissingAnnotations
{
    @Test
    public void test()
    {
        assertThatThrownBy(() -> ReportMissingTestAnnotation.reportMissingTestAnnotations(C.class))
                .isInstanceOf(MissingAnnotationsException.class)
                .satisfies(e -> {
                    MissingAnnotationsException exception = (MissingAnnotationsException) e;
                    assertThat(exception.getTestClass()).isEqualTo(C.class);
                    assertThat(exception.getFailures())
                            .isEqualTo(Sets.set(
                                    new Failure(method(C.class, "test3"), method(B.class, "test3")),
                                    new Failure(method(C.class, "test6"), method(A.class, "test6")),
                                    new Failure(method(B.class, "test7"), method(A.class, "test7")),
                                    new Failure(method(B.class, "test8"), method(A.class, "test8")),
                                    new Failure(method(C.class, "test9"), method(A.class, "test9")),
                                    new Failure(method(B.class, "test9"), method(A.class, "test9"))));
                });
    }

    @Test
    public void testRepeatedTest()
    {
        assertThatThrownBy(() -> ReportMissingTestAnnotation.reportMissingTestAnnotations(RepeatedTestChild.class))
                .isInstanceOf(MissingAnnotationsException.class)
                .satisfies(e -> {
                    MissingAnnotationsException exception = (MissingAnnotationsException) e;
                    assertThat(exception.getTestClass()).isEqualTo(RepeatedTestChild.class);
                    assertThat(exception.getFailures())
                            .isEqualTo(Sets.set(
                                    new Failure(method(RepeatedTestChild.class, "test3"), method(RepeatedTestParent.class, "test3")),
                                    new Failure(method(RepeatedTestChild.class, "test4"), method(RepeatedTestParent.class, "test4"))));
                });
    }

    private static Method method(Class<?> clazz, String name)
    {
        try {
            return clazz.getDeclaredMethod(name);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    class A
    {
        @Test
        public void test1() {}

        @Test
        public void test2() {}

        @Test
        public void test3() {}

        @Test
        public void test4() {}

        @Test
        public void test5() {}

        @Test
        public void test6() {}

        @Test
        public void test7() {}

        @Test
        public void test8() {}

        @Test
        public void test9() {}
    }

    class B
            extends A
    {
        @Test
        public void test1() {}

        @Test
        public void test2() {}

        @Test
        public void test3() {}

        public void test7() {}

        public void test8() {}

        public void test9() {}
    }

    class C
            extends B
    {
        @Test
        public void test1() {}

        public void test3() {}

        @Test
        public void test4() {}

        public void test6() {}

        @Test
        public void test7() {}

        public void test9() {}
    }

    class RepeatedTestChild
            extends RepeatedTestParent
    {
        @RepeatedTest(0)
        @Override
        public void test1() {}

        @Test
        @Override
        public void test2() {}

        @Override
        public void test3() {}

        @Override
        public void test4() {}
    }

    class RepeatedTestParent
    {
        @Test
        public void test1() {}

        @RepeatedTest(0)
        public void test2() {}

        @Test
        public void test3() {}

        @RepeatedTest(0)
        public void test4() {}
    }
}
