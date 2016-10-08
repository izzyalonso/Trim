package es.sandwatch.trim;

import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;


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
    static @NotNull List<FieldNode> parseClass(@NotNull Class<?> target){
        List<FieldNode> classFields = new ArrayList<>();
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
    private void parseClass(@NotNull Class<?> targetClass, @NotNull List<FieldNode> targetList){
        //Do not parse java.lang.Object
        if (!targetClass.equals(Object.class)){
            seenClasses.add(targetClass);
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
                List<FieldNode> fieldClassFields = null;

                Class<?> fieldClass = field.getType();
                if (shouldParseClass(fieldClass)){
                    fieldClassFields = new ArrayList<>();
                    parseClass(fieldClass, fieldClassFields);
                }

                //Add a new FieldNode to the list
                targetList.add(new FieldNode(name, fieldClassFields));
            }

            //Parse superclasses as well
            parseClass(targetClass.getSuperclass(), targetList);
            seenClasses.remove(targetClass);
        }
    }

    /**
     * Tells whether the target should be parsed and added to the hierarchy.
     *
     * Things that should NOT be parsed:
     *   - Primitives
     *   - Primitive wrappers
     *   - CharSequences
     *   - Collections
     *   - Classes already seen in the current hierarchy branch
     *
     * @param target the class type to be checked.
     * @return true if it should, false otherwise.
     */
    private boolean shouldParseClass(Class<?> target){
        return !ClassUtils.isPrimitiveOrWrapper(target) &&
                !CharSequence.class.isAssignableFrom(target)  &&
                !Collection.class.isAssignableFrom(target) &&
                !seenClasses.contains(target);
    }


    /**
     * Represents an object in the field hierarchy.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class FieldNode{
        private String name;
        private List<FieldNode> fieldNodes;


        /**
         * Constructor.
         *
         * @param name the name of the field.
         * @param fieldNodes a list containing the object's fields
         */
        private FieldNode(@NotNull String name, @Nullable List<FieldNode> fieldNodes){
            this.name = name;
            this.fieldNodes = fieldNodes;
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
         * Tells whether this field was parsed.
         *
         * @return true if it was, false otherwise.
         */
        boolean isParsedObject(){
            return fieldNodes == null;
        }

        /**
         * Getter for the list of object's fields, if any.
         *
         * @return the named list.
         */
        @Nullable List<FieldNode> getFieldNodes(){
            return fieldNodes;
        }

        @Override
        public String toString(){
            return toString("");
        }

        /**
         * String generation method with spacing to make hierarchy clear when printing.
         *
         * @param spacing the spacing to be included before field names.
         * @return the string representing this hierarchy.
         */
        private String toString(String spacing){
            StringBuilder result = new StringBuilder();
            result.append(spacing).append(name);
            if (isParsedObject()){
                spacing += "  ";
                for (FieldNode field: fieldNodes){
                    result.append(field.toString(spacing));
                }
            }
            return result.toString();
        }
    }
}
