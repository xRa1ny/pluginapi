package me.xra1ny.pluginapi.exceptions;

import me.xra1ny.pluginapi.models.exception.RainbowException;

public class HomogeneousRainbowException extends RainbowException {
    public HomogeneousRainbowException() {
        super("rainbow must have two or more colors");
    }
}
