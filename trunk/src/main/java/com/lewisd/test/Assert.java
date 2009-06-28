package com.lewisd.test;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.AssertionFailedError;

import com.lewisd.reflection.ReflectionHelper;

public class Assert extends junit.framework.Assert
{

    private static final List<String> REFLECTIVE_EQUALS_EXCLUDE_PACKAGES = new LinkedList<String>();

    static
    {
        REFLECTIVE_EQUALS_EXCLUDE_PACKAGES.add("java");
        REFLECTIVE_EQUALS_EXCLUDE_PACKAGES.add("javax");
        REFLECTIVE_EQUALS_EXCLUDE_PACKAGES.add("sun");
    }

    public static void assertEqualsReflectively(Object expected, Object actual)
    {
        assertEqualsReflectively(new HashSet<Object>(), expected, actual);
    }

    private static void assertEqualsReflectively(Set<Object> alreadyChecked, Object expected, Object actual)
    {
        Class<?> expectedClass = expected.getClass();
        if (isInReflectivelyEqualsPackage(expectedClass))
        {
            assertSame(expectedClass, actual.getClass());
            if (!alreadyChecked.contains(expected))
            {
                alreadyChecked.add(expected);
                List<Field> fields = ReflectionHelper.getFields(expectedClass);
                for (Field field : fields)
                {
                    try
                    {
                        Object expectedValue = field.get(expected);
                        Object actualValue = field.get(actual);
                        if (expectedValue != actualValue)
                        {
                            assertEqualsReflectively(alreadyChecked, expectedValue, actualValue);
                        }
                    }
                    catch (IllegalArgumentException e)
                    {
                        throw new RuntimeException(e);
                    }
                    catch (IllegalAccessException e)
                    {
                        throw new RuntimeException(e);
                    }
                    catch (AssertionFailedError e)
                    {
                        throw new AssertionFailedError("Field " + field.getDeclaringClass().getSimpleName() + "." + field.getName() + " not equal: " + e.getMessage());
                    }
                }
            }
        }
        else if (expected instanceof Collection)
        {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        else
        {
            assertEquals(expected, actual);
        }
    }

    private static boolean isInReflectivelyEqualsPackage(Class<?> klass)
    {
        String className = klass.getName();
        for (String packagePrefix : REFLECTIVE_EQUALS_EXCLUDE_PACKAGES)
        {
            if (className.startsWith(packagePrefix + "."))
            {
                return false;
            }
        }
        return true;
    }

    public static void assertStartsWith(String beginning, String stringToCheck)
    {
        if (!stringToCheck.startsWith(beginning))
        {
            throw new AssertionFailedError("Expected String to start with <" + beginning + "> but was <" + stringToCheck + ">");
        }
    }

}
