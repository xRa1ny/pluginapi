package me.xra1ny.pluginapi.models.user;

import lombok.Getter;
import lombok.Setter;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.models.task.RRepeatableTask;
import me.xra1ny.pluginapi.models.task.RepeatableTaskInfo;

@RepeatableTaskInfo(interval = 1000)
public class UserTimeoutHandler extends RRepeatableTask {
    @Getter
    @Setter
    private long userTimeout;

    public UserTimeoutHandler(long userTimeout) {
        this.userTimeout = userTimeout;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onTick() throws Exception {
        for(RUser user : RPlugin.getInstance().getUserManager().getUsers()) {
            if(user.getPlayer() != null && user.getPlayer().isOnline()) {
                continue;
            }

            if(user.getTimeout() <= 0) {
                RPlugin.getInstance().getUserManager().unregister(user);

                return;
            }

            user.setTimeout(user.getTimeout()-1);
        }
    }
}
