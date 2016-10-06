package es.sandwatch.trim;

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
     * @param endpoint the endpoint associated to the report.
     * @param requestResult the result of the request to the above endpoint.
     * @return the report object.
     */
    EndpointReport addEndpointReport(Endpoint endpoint, Trim.RequestResult requestResult){
        EndpointReport report = new EndpointReport(endpoint, requestResult);
        endpointReports.add(report);
        return report;
    }

    @Override
    public String toString(){
        String report = "";
        if (endpointReports.isEmpty()){
            report += "Nothing to report.";
        }
        else{
            report += "Trim report, " + endpointReports.size() + " endpoints:";
            for (EndpointReport endpointReport:endpointReports){
                report += "\n\n" + endpointReport;
            }
        }
        return report;
    }


    /**
     * Report for a single endpoint.
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    class EndpointReport{
        private Endpoint endpoint;
        private Trim.RequestResult requestResult;
        private boolean responseFormatError;
        private Map<String, Boolean> attributeReports;


        /**
         * Constructor.
         *
         * @param endpoint the endpoint associated to the report.
         * @param requestResult the result of the request to the above endpoint.
         */
        private EndpointReport(Endpoint endpoint, Trim.RequestResult requestResult){
            this.endpoint = endpoint;
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
        void addAttributeReport(String attribute, boolean used){
            attributeReports.put(attribute, used);
        }

        @Override
        public String toString(){
            String report = endpoint.getModel().getName() + " -> " + endpoint.getUrl();
            if (requestResult.requestFailed()){
                report += "\n  The request could not be performed.";
            }
            else{
                report += "\n  Request status code: " + requestResult.getStatusCode();
                if (requestResult.is4xx()){
                    report += "\n  Server response: " + requestResult.getResponse();
                }
                else if (responseFormatError){
                    report += "\n  The format of the response was unknown.";
                }
                else{
                    for (Map.Entry<String, Boolean> entry:attributeReports.entrySet()){
                        report += "\n    " + entry.getKey() + ": " + (entry.getValue() ? "used" : "not used");
                    }
                }
            }
            return report;
        }
    }
}
