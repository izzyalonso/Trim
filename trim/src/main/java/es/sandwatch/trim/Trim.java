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
            report.addEndpointReport(createEndpointReport(model, result));

            if (listener != null){
                listener.onEndpointReportComplete(model, ++completed);
            }
        }

        return report;
    }

    private @NotNull Report.EndpointReport createEndpointReport(Class<?> model, Fetcher.RequestResult result){
        Report.EndpointReport report = new Report.EndpointReport(model, result);

        //If successful
        if (result.is2xx()){
            //Parse the response and create the usage map and the field list
            Parser.FieldNode<JsonType> endpointObject = Parser.parseJson(result.getResponse());
            if (!endpointObject.isParsedObject()){
                report.setResponseFormatError();
            }
            else{
                //Parse the model structure
                Map<String, Parser.FieldNode<Field>> fields = Parser.parseClass(model);

                System.out.println("\nFrom endpoint:\n");
                System.out.println(endpointObject);
                System.out.println("\n\nFrom model:\n");
                System.out.println(fields.values());

                report.addAttributeReport(createObjectReport(endpointObject, fields));
            }
        }
        return report;
    }

    private @NotNull Report.ObjectReport createObjectReport(Parser.FieldNode<JsonType> jsonObject,
                                                            Map<String, Parser.FieldNode<Field>> modelFields){

        Report.ObjectReport report = new Report.ObjectReport(jsonObject.getName());
        for (Parser.FieldNode<JsonType> attribute:jsonObject.getChildren().values()){
            report.addAttributeReport(createAttributeReport(attribute, modelFields));
        }

        return report;
    }

    private Report.AttributeReport createAttributeReport(Parser.FieldNode<JsonType> jsonObject,
                                                         Map<String, Parser.FieldNode<Field>> modelFields){

        Report.AttributeReport report;
        if (modelFields.containsKey(jsonObject.getName())){
            if (jsonObject.isParsedObject()){
                report = createObjectReport(jsonObject, modelFields.get(jsonObject.getName()).getChildren());
            }
            else{
                report = new Report.AttributeReport(jsonObject.getName());
            }
            JsonType apiType = jsonObject.getPayload();
            JsonType modelType = JsonType.getTypeOf(modelFields.get(jsonObject.getName()).getPayload().getType());
            report.setUsed(true)
                    .setTypes(apiType, modelType);

        }
        else{
            report = new Report.AttributeReport(jsonObject.getName());
            report.setUsed(false);
        }

        return report;
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
         * @param model the model whose report has been complete.
         * @param completed the number of endpoints whose reports have been completed.
         */
        void onEndpointReportComplete(@NotNull Class<?> model, int completed);
    }
}
