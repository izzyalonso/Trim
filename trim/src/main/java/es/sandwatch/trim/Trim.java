package es.sandwatch.trim;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;


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
        specification.lock();
        Trim trim = new Trim(specification, listener);
        return trim.run();
    }


    private ApiSpecification specification;
    private ProgressListener listener;


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
        //Create the fetcher and the report objects
        Fetcher fetcher = new Fetcher(specification.getHeaders());
        Report report = new Report();

        //Initialize a counter
        int completed = 0;

        //Execute the requests to endpoints
        for (Class<?> model:specification.getModels()){
            Fetcher.RequestResult result = fetcher.getEndpointData(model);
            Report.EndpointReport endpointReport = report.addEndpointReport(model, result);

            //If successful
            if (result.is2xx()){
                //Parse the response and create the usage map and the field list
                Parser.FieldNode<JsonType> keys = Parser.parseJson(result.getResponse());
                if (!keys.isParsedObject()){
                    endpointReport.setResponseFormatError();
                }
                else {
                    //Create and populate the field list
                    List<Parser.FieldNode<Field>> fields = Parser.parseClass(model);
                    for (Parser.FieldNode field:fields){
                        //Determine if it exists in the API response
                        if (keys.contains(field.getName())){
                            endpointReport.addAttributeReport(field.getName(), true);
                            keys.remove(field.getName());
                        }
                    }

                    //The rest of the fields in the keys set are not used in the model
                    for (String key:keys.getChildrenNames()){
                        endpointReport.addAttributeReport(key, false);
                    }
                }
            }

            if (listener != null){
                listener.onEndpointReportComplete(model, ++completed);
            }
        }

        return report;
    }


    /**c
     * Interface used to listen to progress updates from Trim.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public interface ProgressListener{
        /**
         * Called when the report about an individual endpoint has been completed.
         *
         * @param model the model whose report has been complete.
         * @param completed the number of endpoints whose reports have been completed.
         */
        void onEndpointReportComplete(@NotNull Class<?> model, int completed);
    }
}
