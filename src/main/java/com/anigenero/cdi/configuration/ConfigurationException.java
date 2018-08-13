package com.anigenero.cdi.configuration;

@SuppressWarnings("WeakerAccess")
public class ConfigurationException extends Exception {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
