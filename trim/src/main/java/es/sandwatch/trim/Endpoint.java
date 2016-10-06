package es.sandwatch.trim;

import org.jetbrains.annotations.NotNull;

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
    public Endpoint(@NotNull String url, @NotNull Class model){
        this(url, model, new HashMap<>());
    }

    /**
     * Creates an endpoint with endpoint-specific headers.
     *
     * @param url the url of the endpoint.
     * @param model the model associated to the endpoint.
     * @param headers the additional headers this endpoint should be queried with.
     */
    public Endpoint(@NotNull String url, @NotNull Class model, @NotNull Map<String, String> headers){
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
    @NotNull Endpoint addHeader(@NotNull String header, @NotNull String value){
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
    @NotNull String getUrl(){
        return url;
    }

    /**
     * Model getter.
     *
     * @return the model associated to the endpoint.
     */
    @NotNull Class getModel(){
        return model;
    }

    /**
     * Headers getter.
     *
     * @return the endpoint's additional headers.
     */
    @NotNull Map<String, String> getHeaders(){
        return headers;
    }

    @Override
    public String toString(){
        return model.getName() + " -> " + url;
    }
}
