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
public class Specification{
    /**
     * The current version of the app
     */
    private int appVersion;

    /**
     * The list of models to be inspected.
     */
    private List<Class<?>> models;

    /**
     * The headers that apply to all endpoints in the API.
     */
    private Map<String, String> headers;

    /**
     * Control flag. Indicates whether the user has run this specification, if so, prevents him from modifying it.
     */
    private boolean locked;


    /**
     * Constructor.
     */
    public Specification(){
        appVersion = -1;
        models = new ArrayList<>();
        headers = new HashMap<>();
        locked = false;
    }

    /**
     * Sets the current application version.
     *
     * @param appVersion the current version of the application.
     */
    public Specification setCurrentApplicationVersion(int appVersion){
        this.appVersion = appVersion;
        return this;
    }

    /**
     * Adds a model to the specification.
     *
     * @param model the model to be added.
     * @return this object.
     */
    public Specification addModel(@NotNull Class<?> model){
        if (!locked){
            models.add(model);
        }
        return this;
    }

    /**
     * Adds a header to this specification.
     *
     * @param header the header to be added.
     * @param value the value of the header to be added.
     * @return this object.
     */
    public Specification addHeader(@NotNull String header, @NotNull String value){
        if (!locked){
            headers.put(header, value);
        }
        return this;
    }

    /**
     * Locks the specification.
     */
    void lock(){
        locked = true;
    }

    /**
     * Application version getter.
     *
     * @return the application version.
     */
    int getAppVersion(){
        return appVersion;
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
