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
     * @param specification the Specification object containing all API and model information.
     * @return the report object.
     */
    public static @NotNull Report run(@NotNull Specification specification){
        return run(specification, null);
    }

    /**
     * Triggers the analysis with a progress listener.
     *
     * @param specification the Specification object containing all API and model information.
     * @param listener the progress listener or null if you are not interested in progress updates.
     * @return the report object.
     */
    public static @NotNull Report run(@NotNull Specification specification, @Nullable ProgressListener listener){
        specification.lock();
        Trim trim = new Trim(specification, listener);
        return trim.run();
    }


    private Specification specification;
    private ProgressListener listener;


    /**
     * Constructor.
     *
     * @param specification the Specification object containing all API and model information.
     * @param listener the progress listener or null if you are not interested in progress updates.
     */
    private Trim(@NotNull Specification specification, @Nullable ProgressListener listener){
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
                report.addAttributeReport(createObjectReport(endpointObject, fields));
            }
        }
        return report;
    }

    /**
     * Creates an ObjectReport for a Json FieldNode given a map of model fields.
     *
     * @param jsonNode the Json FieldNode whose report is to be generated. The payload needs to be of type
     *                 {@code JsonType.OBJECT} or {@code JsonType.Array}.
     * @param modelFields the set of fields in the model that share hierarchy with the Json FieldNode's children.
     * @return the generated ObjectReport.
     */
    private @NotNull Report.ObjectReport createObjectReport(@NotNull Parser.FieldNode<JsonType> jsonNode,
                                                            @NotNull Map<String, Parser.FieldNode<Field>> modelFields){

        Report.ObjectReport report = new Report.ObjectReport(jsonNode.getName());
        if (jsonNode.isParsedObject()){
            //Generate AttributeReports for all children
            for (Parser.FieldNode<JsonType> attribute:jsonNode.getChildren().values()){
                report.addAttributeReport(createAttributeReport(attribute, modelFields));
            }
        }

        return report;
    }

    /**
     * Creates an AttributeReport for a Json FieldNode given a map of model fields.
     *
     * @param jsonObject the Json FieldNode whose report is to be generated.
     * @param modelFields the set of fields in the model that share hierarchy with the Json FieldNode's children.
     * @return the generated AttributeReport.
     */
    private Report.AttributeReport createAttributeReport(@NotNull Parser.FieldNode<JsonType> jsonObject,
                                                         @NotNull Map<String, Parser.FieldNode<Field>> modelFields){

        Report.AttributeReport report;
        if (modelFields.containsKey(jsonObject.getName())){
            //If this is a JsonType.OBJECT or a JsonType.ARRAY, create an ObjectReport
            if (jsonObject.isParsedObject()){
                report = createObjectReport(jsonObject, modelFields.get(jsonObject.getName()).getChildren());
            }
            else{
                report = new Report.AttributeReport(jsonObject.getName());
            }
            //Populate the report
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
