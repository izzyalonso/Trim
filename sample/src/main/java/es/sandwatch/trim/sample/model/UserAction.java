package es.sandwatch.trim.sample.model;

import es.sandwatch.trim.annotation.*;

/**
 * TNData's UserAction model.
 */
@Endpoint("https://staging.tndata.org/api/users/actions/54040/")
@Headers({
        @Header(header = "Authorization", value = "Token xxx")
})
public class UserAction extends Action{
    private TDCAction action;

    @AttributeName("userbehavior_id")
    @RemovedInVersion(22)
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
