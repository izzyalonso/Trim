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


    Report(){
        endpointReports = new ArrayList<>();
    }

    void addEndpointReport(EndpointReport endpointReport){
        endpointReports.add(endpointReport);
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


        EndpointReport(Endpoint endpoint, Trim.RequestResult requestResult){
            this.endpoint = endpoint;
            this.requestResult = requestResult;
            this.responseFormatError = false;
            this.attributeReports = new HashMap<>();
        }

        void setResponseFormatError(){
            responseFormatError = true;
        }

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
