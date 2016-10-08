package es.sandwatch.trim;

import es.sandwatch.trim.annotation.Endpoint;
import es.sandwatch.trim.annotation.Header;
import es.sandwatch.trim.annotation.Headers;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * Handles network requests.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
class Fetcher{
    private Map<String, String> headers;
    private HttpClient client;


    /**
     * Constructor.
     *
     * @param headers the headers that are common to all requests.
     */
    Fetcher(@NotNull Map<String, String> headers){
        this.headers = headers;
        client = HttpClientBuilder.create().build();
    }

    /**
     * Hits an endpoint and returns the result.
     *
     * @param model the model containing endpoint and header data.
     * @return a bundle containing request code and result.
     */
    @NotNull RequestResult getEndpointData(@NotNull Class<?> model){
        //Create the request and add all the headers
        HttpGet request = new HttpGet(model.getAnnotation(Endpoint.class).value());
        Map<String, String> headers = new HashMap<>();
        //First, get all headers declared in the model
        Headers headersAnnotation = model.getAnnotation(Headers.class);
        if (headersAnnotation != null) {
            for (Header header:headersAnnotation.value()){
                headers.put(header.header(), header.value());
            }
        }
        //Next, add all generic headers not overridden in the model
        for (String header:this.headers.keySet()){
            if (!headers.containsKey(header)){
                headers.put(header, this.headers.get(header));
            }
        }
        //Finally, add the result to the request
        for (String header:headers.keySet()){
            System.out.println(header + " -> " + headers.get(header));
            request.addHeader(header, headers.get(header));
        }

        RequestResult result = null;
        BufferedReader reader = null;
        try{
            long startTime = System.currentTimeMillis();
            //Execute the request and create the reader
            HttpResponse response = client.execute(request);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            //Fetch the result
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }

            float timeSecs = (System.currentTimeMillis() - startTime)/1000f;

            //Create the result bundle
            result = new RequestResult(timeSecs, response.getStatusLine().getStatusCode(), stringBuilder.toString());
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
     * Class containing the relevant information about the result of an HTTP request.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class RequestResult{
        private final float requestTime;
        private final int statusCode;
        private final String response;


        /**
         * Constructor. Call if the request failed.
         */
        private RequestResult(){
            this(0F, -1, "Request failed");
        }

        /**
         * Constructor. Call if the request got through to the server.
         *
         * @param requestTime the time that took to complete the request.
         * @param statusCode the status code of the request.
         * @param response the response to the request.
         */
        private RequestResult(float requestTime, int statusCode, @NotNull String response){
            this.requestTime = requestTime;
            this.statusCode = statusCode;
            this.response = response;
        }

        /**
         * Request time getter.
         *
         * @return the time that took to complete the request.
         */
        float getRequestTime(){
            return requestTime;
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
}
