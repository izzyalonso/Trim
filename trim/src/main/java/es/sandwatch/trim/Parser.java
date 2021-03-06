package es.sandwatch.trim;

import es.sandwatch.trim.annotation.AttributeName;
import es.sandwatch.trim.annotation.CollectionGenericType;
import es.sandwatch.trim.annotation.Skip;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
     * @param src the class to parse.
     * @return the root node of the complete model hierarchy
     */
    static @NotNull Map<String, FieldNode<Field>> parseClass(@NotNull Class<?> src){
        List<FieldNode<Field>> classFields = new ArrayList<>();
        new Parser().parseClass(src, classFields);
        //Put the results into a map.
        Map<String, FieldNode<Field>> result = new HashMap<>();
        for (FieldNode<Field> node:classFields){
            result.put(node.getName(), node);
        }
        return result;
    }

    /**
     * Parses a JSON string into a FieldNode hierarchy.
     *
     * @param src the source string.
     * @return a set of FieldNodes.
     */
    static @NotNull FieldNode<JsonType> parseJson(@NotNull String src){
        return new FieldNode<>(JsonType.OBJECT, "", new Parser().parseJsonInternal(src));
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
     * @param srcClass the class to parse.
     * @param targetList the root node of the model hierarchy
     */
    private void parseClass(@NotNull Class<?> srcClass, @NotNull List<FieldNode<Field>> targetList){
        //Do not parse java.lang.Object
        if (!srcClass.equals(Object.class)){
            seenClasses.add(srcClass);
            //For every declared field in the target
            for (Field field:srcClass.getDeclaredFields()){
                if (field.getAnnotation(Skip.class) == null){
                    //Extract the serialized name of the field, annotation overrides field name
                    AttributeName annotation = field.getAnnotation(AttributeName.class);
                    String name;
                    if (annotation == null){
                        name = field.getName();
                    }
                    else{
                        name = annotation.value();
                    }
                    List<FieldNode<Field>> fieldClassFields = null;

                    Class<?> fieldClass = field.getType();
                    if (shouldParseClass(fieldClass)){
                        fieldClassFields = new ArrayList<>();
                        parseClass(fieldClass, fieldClassFields);
                    }
                    if (Collection.class.isAssignableFrom(fieldClass)){
                        CollectionGenericType type = field.getAnnotation(CollectionGenericType.class);
                        if (type != null){
                            fieldClassFields = new ArrayList<>();
                            parseClass(type.value(), fieldClassFields);
                        }
                    }

                    //Add a new FieldNode to the list
                    targetList.add(new FieldNode<>(field, name, fieldClassFields));
                }
            }

            //Parse superclasses as well
            parseClass(srcClass.getSuperclass(), targetList);
            seenClasses.remove(srcClass);
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
                !CharSequence.class.isAssignableFrom(target) &&
                !Collection.class.isAssignableFrom(target) &&
                !seenClasses.contains(target);
    }

    /**
     * Parses a JSON string into a FieldNode hierarchy.
     *
     * @param src the source string.
     * @return a set of FieldNodes or null if src was malformatted.
     */
    private @Nullable Set<FieldNode<JsonType>> parseJsonInternal(@NotNull String src){
        Set<FieldNode<JsonType>> fieldSet = new HashSet<>();
        try{
            //Parse the object and get a key iterator
            JSONObject object = new JSONObject(src);
            Iterator<String> keys = object.keys();
            //So long as there are values
            while (keys.hasNext()){
                //Extract the next key
                String key = keys.next();
                Set<FieldNode<JsonType>> objectFields = null;
                Object unknown = object.get(key);
                JsonType type = JsonType.getTypeOf(unknown);
                //If the next object is a nested JSON object, parse it
                if (type == JsonType.OBJECT){
                    objectFields = parseJsonInternal(unknown.toString());
                }
                else if (type == JsonType.ARRAY){
                    objectFields = parseJsonArrayInternal((JSONArray)unknown);
                }
                //Add the node to the set
                fieldSet.add(new FieldNode<>(type, key, objectFields));
            }
        }
        catch (JSONException jx){
            //Halt if an exception is raised
            return null;
        }
        return fieldSet;
    }

    /**
     * Parses a JSONArray into a FieldNode hierarchy.
     *
     * @param array the source JSONArray.
     * @return a set of FieldNodes containing a single FieldNode representing the structure of the array's items.
     */
    private Set<FieldNode<JsonType>> parseJsonArrayInternal(JSONArray array){
        if (array.length() == 0){
            Set<FieldNode<JsonType>> set = new HashSet<>();
            set.add(new FieldNode<>(JsonType.NONE, "", null));
            return set;
        }
        else{
            Set<FieldNode<JsonType>> objectFields = null;
            //Get only the first item in the array, let's assume all items are of the same type
            //TODO? Java is statically typed though
            Object unknown = array.get(0);
            JsonType type = JsonType.getTypeOf(unknown);
            if (type == JsonType.OBJECT){
                objectFields = parseJsonInternal(unknown.toString());
            }
            else if (type == JsonType.ARRAY){
                objectFields = parseJsonArrayInternal((JSONArray)unknown);
            }
            return objectFields;
        }
    }


    /**
     * Represents an object in the field hierarchy.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class FieldNode<T>{
        private T payload;
        private String name;
        private Map<String, FieldNode<T>> children;


        /**
         * Constructor.
         *
         * @param name the name of the field.
         * @param children a list containing the object's fields
         */
        private FieldNode(@NotNull T payload, @NotNull String name, @Nullable Collection<FieldNode<T>> children){
            this.payload = payload;
            this.name = name;
            if (children != null){
                this.children = new HashMap<>();
                for (FieldNode<T> child:children){
                    this.children.put(child.getName(), child);
                }
            }
        }

        /**
         * Payload getter.
         *
         * @return the node's payload.
         */
        @NotNull T getPayload(){
            return payload;
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
            return children != null;
        }

        /**
         * Children getter.
         *
         * @return the children map.
         */
        Map<String, FieldNode<T>> getChildren(){
            return children;
        }

        /**
         * Tells whether this node contains a child.
         *
         * @param childName the name of the child to be checked.
         * @return true if it does, false otherwise.
         */
        boolean contains(String childName){
            return children.containsKey(childName);
        }

        FieldNode<T> get(String childName){
            return children.get(childName);
        }

        /**
         * Removes a child a child.
         *
         * @param childName the name of the child to be removed.
         */
        void remove(String childName){
            children.remove(childName);
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
            result.append("\n").append(spacing);
            if (!(payload instanceof Field)){
                result.append(payload).append(" ");
            }
            result.append(name);
            if (isParsedObject()){
                spacing += "  ";
                for (FieldNode<T> node:children.values()){
                    result.append(node.toString(spacing));
                }
            }
            return result.toString();
        }
    }
}
