package es.sandwatch.trim.sample;

import es.sandwatch.trim.ApiSpecification;
import es.sandwatch.trim.EndpointLegacy;
import es.sandwatch.trim.Report;
import es.sandwatch.trim.Trim;
import es.sandwatch.trim.sample.model.Category;
import es.sandwatch.trim.sample.model.Goal;
import org.jetbrains.annotations.NotNull;


/**
 * Sample program. For now it is used to test new functionality.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class Sample implements Trim.ProgressListener{
    private Sample(){
        ApiSpecification spec = new ApiSpecification()
                .addModel(Category.class)
                .addModel(Goal.class);
        Report report = Trim.run(spec, this);
        System.out.println(report);
    }

    @Override
    public void onEndpointReportComplete(@NotNull EndpointLegacy endpoint, int completed){
        System.out.println(endpoint + ", " + completed + " completed so far.");
    }


    public static void main(String args[]){
        System.out.println("I am running!!");
        new Sample();
    }
}
