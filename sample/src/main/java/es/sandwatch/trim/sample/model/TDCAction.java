package es.sandwatch.trim.sample.model;

import es.sandwatch.trim.annotation.AttributeName;


/**
 * TNData's TDCAction model.
 */
class TDCAction extends TDCBase{
    @AttributeName("sequence_order")
    private int sequenceOrder;
    @AttributeName("more_info")
    private String moreInfo;
    @AttributeName("html_more_info")
    private String htmlMoreInfo;
    @AttributeName("external_resource")
    private String externalResource;
    @AttributeName("external_resource_name")
    private String externalResourceName;
    @AttributeName("external_resource_type")
    private String externalResourceType;

    @AttributeName("behavior")
    private long behaviorId;
    @AttributeName("behavior_title")
    private String behaviorTitle;
    @AttributeName("behavior_description")
    private String behaviorDescription;
}
