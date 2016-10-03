package es.sandwatch.trim;

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
     * Tells whether this endpoint has additional headers.
     *
     * @return true if this endpoint has additional headers, false otherwise.
     */
    boolean hasHeaders(){
        return headers != null && !headers.isEmpty();
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
