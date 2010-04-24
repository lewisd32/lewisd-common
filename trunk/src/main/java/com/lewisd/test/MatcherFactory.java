package com.lewisd.test;

import junit.framework.AssertionFailedError;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

public class MatcherFactory
{

    public static <T> Matcher<T> reflectiveEquals(final T expectedObject)
    {
        return new ReflectiveEqualsMatcher<T>(expectedObject);
    }
    
    private static class ReflectiveEqualsMatcher<T> extends TypeSafeMatcher<T>
    {
        private final T expectedObject;
        
        public ReflectiveEqualsMatcher(T expectedObject)
        {
            this.expectedObject = expectedObject;
        }

        @Override
        public boolean matchesSafely(T actualObject)
        {
            try
            {
                Assert.assertEqualsReflectively(expectedObject, actualObject);
                return true;
            }
            catch (AssertionFailedError e)
            {
                return false;
            }
        }

        @Override
        public void describeTo(Description description)
        {
            description.appendText("Expected ").appendValue(expectedObject);
        }
    }
}
