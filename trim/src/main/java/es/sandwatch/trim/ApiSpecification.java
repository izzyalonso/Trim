package es.sandwatch.trim;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains all the relevant information about an API, namely endpoints and headers.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ApiSpecification{
    /**
     * The list of models to be inspected.
     */
    private List<Class<?>> models;

    /**
     * The headers that apply to all endpoints in the API
     */
    private Map<String, String> headers;


    /**
     * Constructor.
     */
    public ApiSpecification(){
        models = new ArrayList<>();
        headers = new HashMap<>();
    }

    /**
     * Adds a model to the specification.
     *
     * @param model the model to be added.
     * @return this object.
     */
    public ApiSpecification addModel(@NotNull Class<?> model){
        models.add(model);
        return this;
    }

    /**
     * Adds a header to this specification.
     *
     * @param header the header to be added.
     * @param value the value of the header to be added.
     * @return this object.
     */
    public ApiSpecification addHeader(@NotNull String header, @NotNull String value){
        headers.put(header, value);
        return this;
    }

    /**
     * Model list getter.
     *
     * @return the list of models in this specification.
     */
    @NotNull List<Class<?>> getModels(){
        return models;
    }

    /**
     * Header map getter.
     *
     * @return a map containing the headers that apply to all endpoints in this specification.
     */
    @NotNull Map<String, String> getHeaders(){
        return headers;
    }
}
