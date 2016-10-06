package es.sandwatch.trim;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Main project class.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Trim{
    /**
     * Triggers the analysis without setting a progress listener.
     *
     * @param specification the ApiSpecification object containing all API and model information.
     * @return the report object.
     */
    public static @NotNull Report run(@NotNull ApiSpecification specification){
        return run(specification, null);
    }

    /**
     * Triggers the analysis with a progress listener.
     *
     * @param specification the ApiSpecification object containing all API and model information.
     * @param listener the progress listener or null if you are not interested in progress updates.
     * @return the report object.
     */
    public static @NotNull Report run(@NotNull ApiSpecification specification, @Nullable ProgressListener listener){
        Trim trim = new Trim(specification, listener);
        return trim.run();
    }


    private ApiSpecification specification;
    private ProgressListener listener;
    private HttpClient client;


    /**
     * Constructor.
     *
     * @param specification the ApiSpecification object containing all API and model information.
     * @param listener the progress listener or null if you are not interested in progress updates.
     */
    private Trim(@NotNull ApiSpecification specification, @Nullable ProgressListener listener){
        this.specification = specification;
        this.listener = listener;
    }

    /**
     * Runs the analysis.
     *
     * @return the report object.
     */
    private @NotNull Report run(){
        //Add all generic headers to all endpoints
        for (Endpoint endpoint: specification.getEndpoints()){
            for (String header: specification.getHeaders().keySet()){
                endpoint.addHeader(header, specification.getHeaders().get(header));
            }
        }

        //Create the http client and the report objects
        client = HttpClientBuilder.create().build();
        Report report = new Report();

        //Initialize a counter
        int completed = 0;

        //Execute the requests to endpoints
        for (Endpoint endpoint: specification.getEndpoints()){
            RequestResult result = getEndpointData(endpoint);
            Report.EndpointReport endpointReport = report.addEndpointReport(endpoint, result);

            //If successful
            if (result.is2xx()){
                //Parse the response and create the usage map and the field list
                Set<String> keys = getJsonAttributeSet(result.getResponse());
                if (keys == null){
                    endpointReport.setResponseFormatError();
                }
                else {
                    //Create and populate the field list
                    List<Field> fields = new ArrayList<>();
                    getFieldsOf(endpoint.getModel(), fields);

                    for (Field field:fields){
                        //Extract the serialized name of the field, annotation overrides field name
                        AttributeName annotation = field.getAnnotation(AttributeName.class);
                        String attributeName;
                        if (annotation == null){
                            attributeName = field.getName();
                        }
                        else{
                            attributeName = annotation.value();
                        }

                        //Determine if it exists in the API response
                        if (keys.contains(attributeName)){
                            endpointReport.addAttributeReport(attributeName, true);
                            keys.remove(attributeName);
                        }
                    }

                    //The rest of the fields in the keys set are not used in the model
                    for (String key:keys){
                        endpointReport.addAttributeReport(key, false);
                    }
                }
            }

            if (listener != null){
                listener.onEndpointReportComplete(endpoint, ++completed);
            }
        }

        return report;
    }

    /**
     * Hits an endpoint and returns the result.
     *
     * @param endpoint the endpoint to hit.
     * @return a bundle containing request code and result
     */
    private @NotNull RequestResult getEndpointData(Endpoint endpoint){
        //Create the request and add all the headers
        HttpGet request = new HttpGet(endpoint.getUrl());
        for (String header:endpoint.getHeaders().keySet()){
            request.addHeader(header, endpoint.getHeaders().get(header));
        }

        RequestResult result = null;
        BufferedReader reader = null;
        try{
            //Execute the request and create the reader
            HttpResponse response = client.execute(request);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            //Fetch the result
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }

            //Create the result bundle
            result = new RequestResult(response.getStatusLine().getStatusCode(), stringBuilder.toString());
        }
        catch (IOException iox){
            iox.printStackTrace();
        }
        finally{
            if (reader != null){
                try{
                    reader.close();
                }
                catch (IOException iox){
                    iox.printStackTrace();
                }
            }
        }

        //If there is no result, something went south
        if (result == null){
            result = new RequestResult();
        }
        return result;
    }

    /**
     * Turns a string returned by an API endpoint into a Set of attributes.
     *
     * @param src the source string.
     * @return the parsed set of attributes or null if src couldn't be interpreted.
     */
    private @Nullable Set<String> getJsonAttributeSet(String src){
        Set<String> fieldSet = new HashSet<>();
        try{
            JSONObject object = new JSONObject(src);

            Iterator<String> keys = object.keys();
            while (keys.hasNext()){
                fieldSet.add(keys.next());
            }
        }
        catch (JSONException jx){
            jx.printStackTrace();
            return null;
        }
        return fieldSet;
    }

    /**
     * Gathers all the fields declared and inherited by a class until the immediate child of Object.
     *
     * @param targetClass the class from which the fields are to be extracted.
     * @param targetList the list where the fields are to be gathered.
     */
    private void getFieldsOf(@NotNull Class<?> targetClass, @NotNull List<Field> targetList){
        if (!targetClass.equals(Object.class)){
            targetList.addAll(Arrays.asList(targetClass.getDeclaredFields()));
            getFieldsOf(targetClass.getSuperclass(), targetList);
        }
    }


    /**
     * Class containing the relevant information about the result of an HTTP request.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class RequestResult{
        private int statusCode;
        private String response;


        /**
         * Constructor. Call if the request failed.
         */
        private RequestResult(){
            this(-1, "Request failed");
        }

        /**
         * Constructor. Call if the request got through to the server.
         *
         * @param statusCode the status code of the request.
         * @param response the response to the request.
         */
        private RequestResult(int statusCode, @NotNull String response){
            this.statusCode = statusCode;
            this.response = response;
        }

        /**
         * Tells whether the request failed before it was sent.
         *
         * @return true if the request failed, false otherwise.
         */
        boolean requestFailed(){
            return statusCode == -1;
        }

        /**
         * Tells whether the request yielded a 2xx status code.
         *
         * @return true if the request yielded a 2xx status code, false otherwise.
         */
        boolean is2xx(){
            return statusCode >= 200 && statusCode < 300;
        }

        /**
         * Tells whether the request yielded a 3xx status code.
         *
         * @return true if the request yielded a 3xx status code, false otherwise.
         */
        boolean is3xx(){
            return statusCode >= 300 && statusCode < 400;
        }

        /**
         * Tells whether the request yielded a 4xx status code.
         *
         * @return true if the request yielded a 4xx status code, false otherwise.
         */
        boolean is4xx(){
            return statusCode >= 400 && statusCode < 500;
        }

        /**
         * Tells whether the request yielded a 5xx status code.
         *
         * @return true if the request yielded a 5xx status code, false otherwise.
         */
        boolean is5xx(){
            return statusCode >= 500 && statusCode < 600;
        }

        /**
         * Status code getter.
         *
         * @return the status code.
         */
        int getStatusCode(){
            return statusCode;
        }

        /**
         * Response getter.
         *
         * @return the response
         */
        @NotNull String getResponse(){
            return response;
        }

        @Override
        public String toString() {
            return "Status code: " + statusCode + ", response: " + response;
        }
    }


    /**
     * Interface used to listen to progress updates from Trim.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface ProgressListener{
        /**
         * Called when the report about an individual endpoint has been completed.
         *
         * @param endpoint the endpoint whose report has been complete.
         * @param completed the number of endpoints whose reports have been completed.
         */
        void onEndpointReportComplete(@NotNull Endpoint endpoint, int completed);
    }
}
