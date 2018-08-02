package com.anigenero.configuration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@ApplicationScoped
public class ConfigurationProducer {

    private Properties properties;
    private Context context;

    @PostConstruct
    public void init() throws ConfigurationException {

        this.properties = new Properties();

        try (final InputStream stream = ConfigurationProducer.class.getResourceAsStream("/application.properties")) {

            if (stream != null) {
                this.properties.load(stream);
            }

            InitialContext initialContext = new InitialContext();
            this.context = (Context) initialContext.lookup("java:comp/env");

        } catch (final IOException e) {
            throw new ConfigurationException("Configuration file cannot be loaded");
        } catch (NamingException e) {
            throw new ConfigurationException("Initial context cannot be loaded", e);
        }

    }

    @Produces
    @Configuration
    public String produceString(InjectionPoint injectionPoint) throws ConfigurationException {
        return this.getProperty(getKey(injectionPoint), isNullable(injectionPoint));
    }

    @Produces
    @Configuration
    public Integer produceInteger(InjectionPoint injectionPoint) throws ConfigurationException {

        final String value = this.getProperty(getKey(injectionPoint), isNullable(injectionPoint));
        return (value != null) ? Integer.valueOf(value) : null;

    }

    @Produces
    @Configuration
    public Long produceLong(InjectionPoint injectionPoint) throws ConfigurationException {

        final String value = this.getProperty(getKey(injectionPoint), isNullable(injectionPoint));
        return (value != null) ? Long.valueOf(value) : null;

    }

    @Produces
    @Configuration
    public Boolean produceBoolean(InjectionPoint injectionPoint) throws ConfigurationException {
        return Boolean.valueOf(this.getProperty(getKey(injectionPoint), isNullable(injectionPoint)));
    }

    /**
     * Gets the name of the configuration element
     *
     * @param injectionPoint {@link InjectionPoint}
     * @return {@link String}
     */
    private String getKey(InjectionPoint injectionPoint) {
        return injectionPoint.getAnnotated().getAnnotation(Configuration.class).value();
    }

    /**
     * Gets the nullable annotation property for {@link Configuration}
     *
     * @param injectionPoint {@link InjectionPoint}
     * @return boolean - true if value can be null, else false
     */
    private boolean isNullable(InjectionPoint injectionPoint) {
        return injectionPoint.getAnnotated().getAnnotation(Configuration.class).nullable();
    }

    /**
     * Get the property from the system
     *
     * @param key        {@link String}
     * @param isNullable boolean
     * @return {@link String}
     * @throws ConfigurationException - if the value is null and the value cannot be null
     */
    private String getProperty(final String key, final boolean isNullable) throws ConfigurationException {

        String value;
        if (this.properties.containsKey(key)) {
            value = this.properties.getProperty(key);
        } else {

            final String contextValue = this.getPropertyFromContext(key);
            if (contextValue != null) {
                value = contextValue;
            } else {
                value = System.getProperty(key);
            }

        }

        if (value == null) {
            if (!isNullable) {
                throw new ConfigurationException("The non-null configuration key '" + key + "' returned null or does not exist");
            } else {
                return null;
            }
        } else {
            return value;
        }

    }

    /**
     * Gets the property from the context
     *
     * @param key {@link String}
     * @return {@link String}
     */
    private String getPropertyFromContext(final String key) {
        try {
            return (String) context.lookup(key);
        } catch (NamingException e) {
            return null;
        }
    }

}
