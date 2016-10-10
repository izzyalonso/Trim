package es.sandwatch.trim.sample.model;

import es.sandwatch.trim.annotation.AttributeName;
import es.sandwatch.trim.annotation.Endpoint;
import es.sandwatch.trim.annotation.Header;
import es.sandwatch.trim.annotation.Headers;

/**
 * TNData's UserAction model.
 */
@Endpoint("https://staging.tndata.org/api/users/actions/66834/")
@Headers({
        @Header(header = "Authorization", value = "Token 9ca707815e8ccd22f630de6077f5d3689c6da878")
})
public class UserAction extends Action{
    private TDCAction action;

    @AttributeName("userbehavior_id")
    private long userBehaviorId;

    @AttributeName("primary_goal")
    private long primaryGoalId;
    @AttributeName("primary_usergoal")
    private long primaryUserGoalId;
    @AttributeName("goal_icon")
    private String goalIconUrl;

    @AttributeName("primary_category")
    private long primaryCategoryId;
}
