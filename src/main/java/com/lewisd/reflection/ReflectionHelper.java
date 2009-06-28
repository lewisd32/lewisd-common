package com.lewisd.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionHelper {

    private static final Map<Class,List<Field>> listOfFieldsByClassMap = new ConcurrentHashMap<Class, List<Field>>();
    
    public static List<Field> getFields(Class klass) {
        List<Field> fields = listOfFieldsByClassMap.get(klass);
        if (fields == null) {
            fields = new LinkedList<Field>();
            for (Field field : klass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible( true );
                    fields.add(field);
                }
            }
            Class superClass = klass.getSuperclass();
            if (superClass != null) {
                fields.addAll(getFields(superClass));
            }
        }
        return fields;
    }

}
