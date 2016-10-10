package es.sandwatch.trim.sample.model;

import es.sandwatch.trim.annotation.AttributeName;


/**
 * TNData's action model.
 */
class Action extends TDCBase{
    private Trigger trigger;
    @AttributeName("next_reminder")
    private String nextReminder;
    @AttributeName("goal_title")
    private String goalTitle;
    private boolean editable;
}
