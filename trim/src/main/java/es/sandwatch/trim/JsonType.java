package es.sandwatch.trim;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;


/**
 * Data types supported by JSON.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
enum JsonType{
    NUMBER_INT, NUMBER_FLOAT, STRING, BOOLEAN, ARRAY, OBJECT, NULL, NONE;


    /**
     * Gets the json type of an object returned by JSONObject.get().
     *
     * @param object the object to be evaluated.
     * @return its JsonType.
     */
    static JsonType getTypeOf(Object object){
        if (JSONObject.NULL.equals(object)){
            return NULL;
        }
        if (object instanceof Boolean){
            return BOOLEAN;
        }
        if (object instanceof Integer || object instanceof Long){
            return NUMBER_INT;
        }
        if (object instanceof Float || object instanceof Double){
            return NUMBER_FLOAT;
        }
        if (object instanceof String){
            return STRING;
        }
        if (object instanceof JSONObject){
            return OBJECT;
        }
        if (object instanceof JSONArray){
            return ARRAY;
        }
        return NONE;
    }

    /**
     * Gets the json type associated with a java type.
     *
     * @param type the object to be evaluated.
     * @return its JsonType.
     */
    static JsonType getTypeOf(Class<?> type){
        if (type.equals(Boolean.class) || type.equals(boolean.class)){
            return BOOLEAN;
        }
        if (type.equals(Integer.class) || type.equals(int.class)){
            return NUMBER_INT;
        }
        if (type.equals(Long.class) || type.equals(long.class)){
            return NUMBER_INT;
        }
        if (type.equals(Float.class) || type.equals(float.class)){
            return NUMBER_FLOAT;
        }
        if (type.equals(Double.class) || type.equals(double.class)){
            return NUMBER_FLOAT;
        }
        if (type.equals(String.class)){
            return STRING;
        }
        if (type.isAssignableFrom(Collection.class)){
            return ARRAY;
        }
        return OBJECT;
    }

    @Override
    public String toString(){
        switch (this){
            case NUMBER_INT:
                return "Number (int)";

            case NUMBER_FLOAT:
                return "Number (float)";

            case STRING:
                return "String";

            case BOOLEAN:
                return "Boolean";

            case ARRAY:
                return "Array";

            case OBJECT:
                return "Object";

            case NULL:
                return "Null";
        }
        return "None";
    }
}
