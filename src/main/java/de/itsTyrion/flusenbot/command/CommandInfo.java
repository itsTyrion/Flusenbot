package de.itsTyrion.flusenbot.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    String name();
    Command.Permission[] permissions() default {};
    boolean isAdminOnly() default false;
}
