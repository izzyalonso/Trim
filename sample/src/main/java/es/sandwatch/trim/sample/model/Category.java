package es.sandwatch.trim.sample.model;

import es.sandwatch.trim.annotation.*;


/**
 * TNData's Category model.
 */
@Endpoint("http://app.tndata.org/api/categories/23/")
/*@Headers({
        @Header(header="Authorization", value="Token ")
})*/
public class Category{
    @AttributeName("id")
    private long id;

    @AttributeName("title")
    @Skip
    private String title;
    @AttributeName("description")
    private String description;
    @AttributeName("html_description")
    private int htmlDescription;
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

    //@AttributeName("goal")
    //private Goal goal;
}
