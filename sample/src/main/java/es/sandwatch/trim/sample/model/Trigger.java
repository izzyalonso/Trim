package es.sandwatch.trim.sample.model;

import es.sandwatch.trim.annotation.AttributeName;


/**
 * TNData's trigger model.
 */
class Trigger extends TDCBase{
    private String name;
    private String time;
    @AttributeName("trigger_date")
    private String date;
    private String recurrences;
    @AttributeName("recurrences_display")
    private String recurrencesDisplay;
    private boolean disabled;
}
