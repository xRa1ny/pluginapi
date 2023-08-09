package me.xra1ny.pluginapi;

import me.xra1ny.pluginapi.models.config.RConfig;
import me.xra1ny.pluginapi.models.user.RUser;
import me.xra1ny.pluginapi.models.user.RUserManager;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginInfo {
    /**
     * the user class to use for this plugin
     * @return the user class
     */
    @NotNull
    Class<? extends RUser> userClass() default RUser.class;

    /**
     * the user manager class to use for this plugin
     * @return the user manager class
     */
    @NotNull
    Class<? extends RUserManager> userManagerClass() default RUserManager.class;

    @NotNull
    Class<? extends RConfig>[] localisationConfigClasses() default {};
}
