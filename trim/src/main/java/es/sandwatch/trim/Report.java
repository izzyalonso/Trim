package es.sandwatch.trim;

import es.sandwatch.trim.annotation.Endpoint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


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
     * Adds an endpoint report to the report.
     *
     * @param endpointReport an endpoint report.
     */
    void addEndpointReport(@NotNull EndpointReport endpointReport){
        endpointReports.add(endpointReport);
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
    static class EndpointReport{
        private String endpoint;
        private Class<?> model;
        private Fetcher.RequestResult requestResult;
        private boolean responseFormatError;
        private List<AttributeReport> attributeReports;


        /**
         * Constructor.
         *
         * @param model the model associated to the report.
         * @param requestResult the result of the request to the above model.
         */
        EndpointReport(@NotNull Class<?> model, @NotNull Fetcher.RequestResult requestResult){
            this.endpoint = model.getAnnotation(Endpoint.class).value();
            this.model = model;
            this.requestResult = requestResult;
            this.responseFormatError = false;
            this.attributeReports = new ArrayList<>();
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
         * @param attributeReport an attribute report.
         */
        void addAttributeReport(@NotNull AttributeReport attributeReport){
            attributeReports.add(attributeReport);
        }

        @Override
        public String toString(){
            StringBuilder report = new StringBuilder().append(endpoint).append("\n").append(model.toString());
            if (requestResult.requestFailed()){
                report.append("\n  The request could not be performed.");
            }
            else{
                report.append("\n  Request time: ").append(requestResult.getRequestTime()).append("s");
                report.append("\n  Request status code: ").append(requestResult.getStatusCode());
                report.append("\n  Response size: ").append(requestResult.getResponse().length());
                if (requestResult.is4xx()){
                    report.append("\n  Server response: ").append(requestResult.getResponse());
                }
                else if (responseFormatError){
                    report.append("\n  The format of the response was unknown.");
                }
                else{
                    for (AttributeReport attributeReport:attributeReports){
                        report.append("\n  ").append(attributeReport);
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
    static class AttributeReport{
        String name;
        private boolean used;
        private int versionsSinceLeftUnused;
        private JsonType apiType;
        private JsonType modelType;


        /**
         * Constructor.
         *
         * @param name the name of the attribute.
         */
        AttributeReport(@NotNull String name){
            this.name = name;
            this.used = false;
            this.versionsSinceLeftUnused = -1;
            this.apiType = JsonType.NONE;
            this.modelType = JsonType.NONE;
        }

        /**
         * Sets attribute usage information.
         *
         * @param used true if it is used, false otherwise.
         * @return this object.
         */
        AttributeReport setUsed(boolean used){
            this.used = used;
            this.versionsSinceLeftUnused = -1;
            return this;
        }

        /**
         * Sets the number of versions since this attribute was removed.
         *
         * @param versionsSinceLeftUnused the number of versions since the attribute was left unused.
         * @return this object.
         */
        AttributeReport setVersionsSinceLeftUnused(int versionsSinceLeftUnused){
            this.versionsSinceLeftUnused = versionsSinceLeftUnused;
            this.used = false;
            return this;
        }

        /**
         * Sets type information.
         *
         * @param apiType the type found in the API endpoint result.
         * @param modelType the type found in the model.
         * @return this object
         */
        AttributeReport setTypes(@NotNull JsonType apiType, @NotNull JsonType modelType){
            this.apiType = apiType;
            this.modelType = modelType;
            return this;
        }

        @Override
        public String toString(){
            StringBuilder result = new StringBuilder().append(name).append(": ");
            if (versionsSinceLeftUnused == -1){
                result.append(used ? "used" : "unused");
            }
            else{
                result.append("left unused ").append(versionsSinceLeftUnused).append(" versions ago");
            }
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


    /**
     * Attribute report for the case where the attribute is an Object or an Array.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    static class ObjectReport extends AttributeReport{
        private static String spacing = "  ";
        private List<AttributeReport> attributeReports;


        /**
         * Constructor.
         *
         * @param name the name of the attribute.
         */
        ObjectReport(String name){
            super(name);
            attributeReports = new ArrayList<>();
        }

        /**
         * Adds an attribute report.
         *
         * @param attributeReport the attribute report to be added.
         */
        void addAttributeReport(AttributeReport attributeReport){
            attributeReports.add(attributeReport);
        }

        @Override
        public String toString(){
            StringBuilder result = new StringBuilder();
            if (!name.isEmpty()){
                result.append(super.toString());
            }
            else{
                result.append("Result:");
            }
            spacing += "  ";
            for (AttributeReport attributeReport:attributeReports){
                result.append("\n").append(spacing).append(attributeReport);
            }
            spacing = spacing.substring(2);
            return result.toString();
        }
    }
}
