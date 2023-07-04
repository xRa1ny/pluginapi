package me.xra1ny.pluginapi.models.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandArg {
    String PLAYER = "%PLAYER%";
    String BOOLEAN = "%BOOLEAN%";
    String NUMBER = "%NUMBER%";

    String value();
    String permission() default "";
    boolean player() default false;
}
