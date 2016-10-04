package es.sandwatch.trim;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;


/**
 * Main project class.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Trim{
    public static void run(ApiSpecification spec){
        //Add all generic headers to all endpoints
        for (Endpoint endpoint:spec.getEndpoints()){
            for (String header:spec.getHeaders().keySet()){
                endpoint.addHeader(header, spec.getHeaders().get(header));
            }
        }

        //Create the http client object
        HttpClient httpClient = HttpClientBuilder.create().build();

        //Execute the requests to endpoints
        for (Endpoint endpoint:spec.getEndpoints()){
            HttpGet request = new HttpGet(endpoint.getUrl());
            for (String header:endpoint.getHeaders().keySet()){
                request.addHeader(header, endpoint.getHeaders().get(header));
            }

            try{
                System.out.println("Executing request for " + endpoint.getUrl());
                HttpResponse response = httpClient.execute(request);

                Reader isr = new InputStreamReader(response.getEntity().getContent());
                BufferedReader reader = new BufferedReader(isr);

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null){
                    result.append(line);
                }

                System.out.println("Result: " + result.toString());
            }
            catch (IOException iox){
                iox.printStackTrace();
            }
        }
    }
}
