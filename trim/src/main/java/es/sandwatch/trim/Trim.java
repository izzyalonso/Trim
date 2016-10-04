package es.sandwatch.trim;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;


/**
 * Main project class.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Trim{
    /**
     * Triggers the analysis.
     *
     * @param spec the ApiSpecification object containing all API and model information.
     */
    public static void run(ApiSpecification spec){
        Trim trim = new Trim(spec);
        trim.run();
    }


    private ApiSpecification spec;
    private HttpClient client;


    private Trim(ApiSpecification spec){
        this.spec = spec;
    }

    private void run(){
        //Add all generic headers to all endpoints
        for (Endpoint endpoint:spec.getEndpoints()){
            for (String header:spec.getHeaders().keySet()){
                endpoint.addHeader(header, spec.getHeaders().get(header));
            }
        }

        //Create the http client object
        client = HttpClientBuilder.create().build();

        //Execute the requests to endpoints
        for (Endpoint endpoint:spec.getEndpoints()){
            RequestResult result = getEndpointData(endpoint);

            if (result.is2xx()){
                List<String> keys = getJsonFieldList(result.getResponse());
                for (String key:keys){
                    System.out.println(key);
                }
            }
        }
    }

    private RequestResult getEndpointData(Endpoint endpoint){
        HttpGet request = new HttpGet(endpoint.getUrl());
        for (String header:endpoint.getHeaders().keySet()){
            request.addHeader(header, endpoint.getHeaders().get(header));
        }

        try{
            System.out.println("Executing request for " + endpoint.getUrl());
            HttpResponse response = client.execute(request);

            Reader isr = new InputStreamReader(response.getEntity().getContent());
            BufferedReader reader = new BufferedReader(isr);

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null){
                result.append(line);
            }

            return new RequestResult(response.getStatusLine().getStatusCode(), result.toString());
        }
        catch (IOException iox){
            iox.printStackTrace();
        }
        return new RequestResult();
    }

    private List<String> getJsonFieldList(String src){
        List<String> fieldSet = new ArrayList<>();
        try{
            JSONObject object = new JSONObject(src);

            Iterator<String> keys = object.keys();
            while (keys.hasNext()){
                fieldSet.add(keys.next());
            }
        }
        catch (JSONException jx){
            jx.printStackTrace();
        }
        return fieldSet;
    }


    /**
     * Class containing the relevant information about the result of an HTTP request.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    private class RequestResult{
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
        private RequestResult(int statusCode, String response){
            this.statusCode = statusCode;
            this.response = response;
        }

        /**
         * Tells whether the request failed before it was sent.
         *
         * @return true if the request failed, false otherwise.
         */
        private boolean requestFailed(){
            return statusCode == -1;
        }

        /**
         * Tells whether the request yielded a 2xx status code.
         *
         * @return true if the request yielded a 2xx status code, false otherwise.
         */
        private boolean is2xx(){
            return statusCode >= 200 && statusCode < 300;
        }

        /**
         * Tells whether the request yielded a 3xx status code.
         *
         * @return true if the request yielded a 3xx status code, false otherwise.
         */
        private boolean is3xx(){
            return statusCode >= 300 && statusCode < 400;
        }

        /**
         * Tells whether the request yielded a 4xx status code.
         *
         * @return true if the request yielded a 4xx status code, false otherwise.
         */
        private boolean is4xx(){
            return statusCode >= 400 && statusCode < 500;
        }

        /**
         * Tells whether the request yielded a 5xx status code.
         *
         * @return true if the request yielded a 5xx status code, false otherwise.
         */
        private boolean is5xx(){
            return statusCode >= 500 && statusCode < 600;
        }

        /**
         * Status code getter.
         *
         * @return the status code.
         */
        private int getStatusCode(){
            return statusCode;
        }

        /**
         * Response getter.
         *
         * @return the response
         */
        private String getResponse(){
            return response;
        }

        @Override
        public String toString() {
            return "Status code: " + statusCode + ", response: " + response;
        }
    }
}
