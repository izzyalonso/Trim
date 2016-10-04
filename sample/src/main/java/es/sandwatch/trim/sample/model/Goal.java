package es.sandwatch.trim.sample.model;

import es.sandwatch.trim.AttributeName;

import java.util.Set;

/**
 * TNData's Goal model.
 */
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
