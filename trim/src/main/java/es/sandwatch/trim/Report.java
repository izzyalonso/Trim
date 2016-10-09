package es.sandwatch.trim;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class containing all report information.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Report{
    private List<EndpointReport> endpointReports;


    /**
     * Constructor.
     */
    Report(){
        endpointReports = new ArrayList<>();
    }

    /**
     * Creates an instance of EndpointReport and adds it to the list.
     *
     * @param model the model associated to the report.
     * @param requestResult the result of the request to the above model.
     * @return the report object.
     */
    @NotNull
    EndpointReport addEndpointReport(@NotNull Class<?> model, @NotNull Fetcher.RequestResult requestResult){
        EndpointReport report = new EndpointReport(model, requestResult);
        endpointReports.add(report);
        return report;
    }

    @Override
    public String toString(){
        StringBuilder report = new StringBuilder();
        if (endpointReports.isEmpty()){
            report.append("Nothing to report.");
        }
        else{
            report.append("Trim report, ").append(endpointReports.size()).append(" endpoints:");
            for (EndpointReport endpointReport:endpointReports){
                report.append("\n\n").append(endpointReport);
            }
        }
        return report.toString();
    }


    /**
     * Report for a single model.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class EndpointReport{
        private Class<?> model;
        private Fetcher.RequestResult requestResult;
        private boolean responseFormatError;
        private Map<String, Boolean> attributeReports;


        /**
         * Constructor.
         *
         * @param model the model associated to the report.
         * @param requestResult the result of the request to the above model.
         */
        private EndpointReport(@NotNull Class<?> model, @NotNull Fetcher.RequestResult requestResult){
            this.model = model;
            this.requestResult = requestResult;
            this.responseFormatError = false;
            this.attributeReports = new HashMap<>();
        }

        /**
         * Lets the report know that the format of the response couldn't be understood.
         */
        void setResponseFormatError(){
            responseFormatError = true;
        }

        /**
         * Adds information about attribute usage to the report.
         *
         * @param attribute the relevant attribute.
         * @param used whether the model uses it.
         */
        void addAttributeReport(@NotNull String attribute, boolean used){
            attributeReports.put(attribute, used);
        }

        @Override
        public String toString(){
            StringBuilder report = new StringBuilder().append(model.toString());
            if (requestResult.requestFailed()){
                report.append("\n  The request could not be performed.");
            }
            else{
                report.append("\n  Request time: ").append(requestResult.getRequestTime()).append("s");
                report.append("\n  Request status code: ").append(requestResult.getStatusCode());
                if (requestResult.is4xx()){
                    report.append("\n  Server response: ").append(requestResult.getResponse());
                }
                else if (responseFormatError){
                    report.append("\n  The format of the response was unknown.");
                }
                else{
                    for (Map.Entry<String, Boolean> entry:attributeReports.entrySet()){
                        report.append("\n    ").append(entry.getKey()).append(": ")
                                .append(entry.getValue() ? "used" : "not used");
                    }
                }
            }
            return report.toString();
        }
    }
    

    /**
     * Report for a single attribute.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class AttributeReport{
        private String name;
        private boolean used;
        private JsonType apiType;
        private JsonType modelType;


        /**
         * Constructor.
         *
         * @param name the name of the attribute.
         */
        AttributeReport(String name){
            this.name = name;
        }

        /**
         * Sets attribute usage information.
         *
         * @param used true if it is used, false otherwise.
         * @return this object.
         */
        AttributeReport setUsed(boolean used){
            this.used = used;
            return this;
        }

        /**
         * Sets type information.
         *
         * @param apiType the type found in the API endpoint result.
         * @param modelType the type found in the model.
         * @return this object
         */
        AttributeReport setTypes(JsonType apiType, JsonType modelType){
            this.apiType = apiType;
            this.modelType = modelType;
            return this;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder().append(name).append(": ").append(used ? "used" : "unused");
            if (used){
                result.append(", ");
                if (apiType == modelType){
                    result.append("types match");
                }
                else{
                    result.append("types mismatch (")
                            .append(apiType).append(" in endpoint, ")
                            .append(modelType).append(" in model)");
                }
            }
            return result.toString();
        }
    }
}
