package es.sandwatch.trim.sample.model;

import es.sandwatch.trim.AttributeName;

/**
 * TNData's Category model.
 */
public class Category{
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

    @AttributeName("grouping")
    private int group;
    @AttributeName("grouping_name")
    private String groupName;

    @AttributeName("order")
    private int order;
    @AttributeName("image_url")
    private String imageUrl;
    @AttributeName("color")
    private String color;
    @AttributeName("secondary_color")
    private String secondaryColor;

    @AttributeName("packaged_content")
    private boolean packagedContent;

    @AttributeName("selected_by_default")
    private boolean selectedByDefault;
}
