package me.xra1ny.pluginapi.exceptions.color;

import me.xra1ny.pluginapi.models.exception.RainbowException;

public class InvalidColorException extends RainbowException {
    public InvalidColorException(String nonColor) {
        super(nonColor + " is not a valid color");
    }
}
