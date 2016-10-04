package es.sandwatch.trim;

import java.util.HashMap;
import java.util.Map;


/**
 * Represents an API endpoint.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Endpoint{
    private String url;
    private Class model;
    private Map<String, String> headers;


    /**
     * Creates an endpoint without endpoint-specific headers.
     *
     * @param url the endpoint url.
     * @param model the model associated to the endpoint.
     */
    public Endpoint(String url, Class model){
        this.url = url;
        this.model = model;
        this.headers = new HashMap<>();
    }

    /**
     * Creates an endpoint with endpoint-specific headers.
     *
     * @param url the url of the endpoint.
     * @param model the model associated to the endpoint.
     * @param headers the additional headers this endpoint should be queried with.
     */
    public Endpoint(String url, Class model, Map<String, String> headers){
        this.url = url;
        this.model = model;
        this.headers = headers;
    }

    /**
     * Adds a header to this endpoint if the endpoint doesn't already have it.
     *
     * @param header the header to be added.
     * @param value the value of the header to be added.
     * @return this Endpoint.
     */
    Endpoint addHeader(String header, String value){
        if (!headers.containsKey(header)){
            headers.put(header, value);
        }
        return this;
    }

    /**
     * Url getter.
     *
     * @return the endpoint's url.
     */
    String getUrl(){
        return url;
    }

    /**
     * Model getter.
     *
     * @return the model associated to the endpoint.
     */
    Class getModel(){
        return model;
    }

    /**
     * Headers getter.
     *
     * @return the endpoint's additional headers.
     */
    Map<String, String> getHeaders(){
        return headers;
    }
}
