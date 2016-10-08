package es.sandwatch.trim.sample.model;

import es.sandwatch.trim.annotation.AttributeName;
import es.sandwatch.trim.annotation.Endpoint;

import java.util.Set;


/**
 * TNData's Goal model.
 */
@Endpoint("http://app.tndata.org/api/goals/82/")
public class Goal{
    @AttributeName("id")
    private long id;

    @AttributeName("title")
    private String title;
    @AttributeName("description")
    private String description;
    @AttributeName("html_description")
    private String htmlDescription;
    @AttributeName("icon_url")
    private String iconUrl;

    @AttributeName("outcome")
    private String outcome;
    @AttributeName("categories")
    private Set<Long> categoryIdSet;
}
