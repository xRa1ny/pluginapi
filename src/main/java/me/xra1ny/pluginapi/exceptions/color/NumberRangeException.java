package me.xra1ny.pluginapi.exceptions.color;


import me.xra1ny.pluginapi.models.exception.RainbowException;

public class NumberRangeException extends RainbowException {
    public NumberRangeException(double minNumber, double maxNumber) {
        super("maxNumber (" + maxNumber + ") is not greater than minNumber (" + minNumber + ")");
    }
}
