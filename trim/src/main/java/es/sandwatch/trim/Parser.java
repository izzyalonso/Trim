package es.sandwatch.trim;

import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Dedicated parser.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
class Parser{
    /**
     * Turns a class into a list of ClassFields
     *
     * @param target the class to parse.
     * @return the root node of the complete model hierarchy
     */
    static @NotNull List<ClassField> parseClass(@NotNull Class<?> target){
        List<ClassField> classFields = new ArrayList<>();
        new Parser().parseClass(target, classFields);
        return classFields;
    }


    /**
     * A set containing all seen classes down a single hierarchy path. This class is used to prevent
     * infinite loops in the case where someone might have included a reference to class A in class B
     * and a reference to class B in class A.
     */
    private Set<Class<?>> seenClasses;


    /**
     * Constructor.
     */
    private Parser(){
        seenClasses = new HashSet<>();
    }

    /**
     * Turns a class into a list of ClassFields
     *
     * @param targetClass the class to parse.
     * @param targetList the root node of the model hierarchy
     */
    private void parseClass(@NotNull Class<?> targetClass, @NotNull List<ClassField> targetList){
        //Do not parse java.lang.Object
        if (!targetClass.equals(Object.class)){
            //For every declared field in the target
            for (Field field:targetClass.getDeclaredFields()){
                //Extract the serialized name of the field, annotation overrides field name
                AttributeName annotation = field.getAnnotation(AttributeName.class);
                String name;
                if (annotation == null){
                    name = field.getName();
                }
                else{
                    name = annotation.value();
                }
                List<ClassField> fieldClassFields = null;

                Class<?> fieldClass = field.getDeclaringClass();
                //TODO Collection check
                //If the field is a class other than:
                //  - A wrapper
                //  - A primitive
                //  - A class already seen in the current hierarchy branch
                if (!ClassUtils.isPrimitiveOrWrapper(fieldClass) && !seenClasses.contains(fieldClass)){
                    seenClasses.add(fieldClass);
                    fieldClassFields = new ArrayList<>();
                    parseClass(fieldClass, fieldClassFields);
                    seenClasses.remove(fieldClass);
                }

                //Add a new ClassField to the list
                targetList.add(new ClassField(name, fieldClassFields));
            }

            //Parse superclasses as well
            parseClass(targetClass.getSuperclass(), targetList);
        }
    }


    /**
     * Represents an object in the field hierarchy.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class ClassField{
        private String name;
        private List<ClassField> classFields;


        /**
         * Constructor.
         *
         * @param name the name of the field.
         * @param classFields a list containing the object's fields
         */
        private ClassField(@NotNull String name, @Nullable List<ClassField> classFields){
            this.name = name;
            this.classFields = classFields;
        }

        /**
         * Name getter.
         *
         * @return the name of the field.
         */
        @NotNull String getName(){
            return name;
        }

        /**
         * Tells whether this field is a relevant object.
         *
         * @return true if it is, false otherwise.
         */
        boolean isObject(){
            return classFields == null;
        }

        /**
         * Getter for the list of object's fields, if any.
         *
         * @return the named list.
         */
        @Nullable List<ClassField> getClassFields(){
            return classFields;
        }
    }
}
