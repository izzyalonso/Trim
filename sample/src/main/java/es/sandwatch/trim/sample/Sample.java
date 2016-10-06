package es.sandwatch.trim.sample;

import es.sandwatch.trim.ApiSpecification;
import es.sandwatch.trim.Endpoint;
import es.sandwatch.trim.Report;
import es.sandwatch.trim.Trim;
import es.sandwatch.trim.sample.model.Category;
import es.sandwatch.trim.sample.model.Goal;


/**
 * Sample program. For now it is used to test new functionality.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Sample{
    public static void main(String args[]){
        System.out.println("I am running!!");
        ApiSpecification spec = new ApiSpecification()
                .addEndpoint(new Endpoint("http://app.tndata.org/api/categories/23/", Category.class))
                .addEndpoint(new Endpoint("http://app.tndata.org/api/goals/82/", Goal.class));
        Report report = Trim.run(spec);
        System.out.println(report);
    }
}
