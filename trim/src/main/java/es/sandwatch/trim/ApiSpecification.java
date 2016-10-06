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
     * The list of endpoints in the API.
     */
    private List<Endpoint> endpoints;

    /**
     * The headers that apply to all endpoints in the API
     */
    private Map<String, String> headers;


    /**
     * Constructor.
     */
    public ApiSpecification(){
        endpoints = new ArrayList<>();
        headers = new HashMap<>();
    }

    /**
     * Adds an endpoint to the specification.
     *
     * @param endpoint the endpoint to be added.
     * @return this object.
     */
    public ApiSpecification addEndpoint(@NotNull Endpoint endpoint){
        endpoints.add(endpoint);
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
     * Endpoint list getter.
     *
     * @return the list of endpoints in this specification.
     */
    @NotNull List<Endpoint> getEndpoints(){
        return endpoints;
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
