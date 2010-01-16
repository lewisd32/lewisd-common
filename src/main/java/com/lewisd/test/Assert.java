package com.lewisd.test;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.AssertionFailedError;

import com.lewisd.reflection.ReflectionHelper;

public class Assert extends junit.framework.Assert {
    
    private static final List<String> REFLECTIVE_EQUALS_EXCLUDE_PACKAGES = new LinkedList<String>();
    
    static {
        REFLECTIVE_EQUALS_EXCLUDE_PACKAGES.add("java");
        REFLECTIVE_EQUALS_EXCLUDE_PACKAGES.add("javax");
        REFLECTIVE_EQUALS_EXCLUDE_PACKAGES.add("sun");
    }
    
    public static void assertEqualsReflectively(Object expected, Object actual) {
        assertEqualsReflectively(new HashSet<Object>(), expected, actual);
    }
    
    private static void assertEqualsReflectively(Set<Object> alreadyChecked, Object expected, Object actual) {
        if (expected != null && actual == null)
        {
            throw new AssertionFailedError("Was null, expected " + expected);
        }
        Class<?> expectedClass = expected.getClass();
        if (isInReflectivelyEqualsPackage(expectedClass)) {
            assertSame(expectedClass, actual.getClass());
            if (!alreadyChecked.contains( expected )) {
                alreadyChecked.add(expected);
                final List<Field> fields = ReflectionHelper.getFields( expectedClass );
                for (Field field : fields) {
                    try {
                        final Object expectedValue = field.get(expected);
                        final Object actualValue = field.get(actual);
                        if (expectedValue != actualValue) {
                            assertEqualsReflectively(alreadyChecked, expectedValue, actualValue);
                        }
                    } catch ( IllegalArgumentException e ) {
                        throw new RuntimeException(e);
                    } catch ( IllegalAccessException e ) {
                        throw new RuntimeException(e);
                    } catch ( AssertionFailedError e) {
                        throw new AssertionFailedError("Field " + field.getDeclaringClass().getSimpleName() + "." +
                                field.getName() + " not equal: " + e.getMessage());
                    }
                }
            }
        } else if (expected instanceof Collection) {
            if (expected instanceof List)
            {
                final List expectedList = (List) expected;
                final List actualList = (List) actual;
                final Iterator ia = actualList.iterator();
                final Iterator ie = expectedList.iterator();
                while(ia.hasNext() && ie.hasNext())
                {
                    Object expectedItem = ie.next();
                    Object actualItem = ia.next();
                    assertEqualsReflectively(alreadyChecked, expectedItem, actualItem);
                }
                if (ie.hasNext())
                {
                    throw new AssertionFailedError("Missing item " + ie.next() + " from list : " + actualList + ", expected " + expected);
                }
                if (ia.hasNext())
                {
                    throw new AssertionFailedError("Unexpected item " + ia.next() + " in list : " + actualList + ", expected " + expected);
                }
            }
            else if (expected instanceof Set)
            {
                throw new UnsupportedOperationException("Unsupported collection: " + expected.getClass());
            }
            else if (expected instanceof Map)
            {
                throw new UnsupportedOperationException("Unsupported collection: " + expected.getClass());
            }
            else
            {
                throw new UnsupportedOperationException("Unsupported collection: " + expected.getClass());
            }
        } else {
            assertEquals(expected, actual);
        }
    }

    private static boolean isInReflectivelyEqualsPackage( Class<?> klass ) {
        final String className = klass.getName();
        for (String packagePrefix : REFLECTIVE_EQUALS_EXCLUDE_PACKAGES) {
            if (className.startsWith( packagePrefix+"." )) {
                return false;
            }
        }
        return true;
    }

    public static void assertStartsWith( String beginning, String stringToCheck ) {
        if (!stringToCheck.startsWith( beginning )) {
            throw new AssertionFailedError("Expected String to start with <" + beginning + "> but was <" + stringToCheck + ">");
        }
    }

}
