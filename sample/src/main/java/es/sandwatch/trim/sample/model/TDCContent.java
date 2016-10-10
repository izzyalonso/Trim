package es.sandwatch.trim.sample.model;

import es.sandwatch.trim.annotation.AttributeName;


/**
 * TNData's Content model.
 */
public class TDCContent extends TDCBase{
    private String title;
    private String description;
    @AttributeName("html_description")
    private String htmlDescription;
    @AttributeName("icon_url")
    private String iconUrl;
}
