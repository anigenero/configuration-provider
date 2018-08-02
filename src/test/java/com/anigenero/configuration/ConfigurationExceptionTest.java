package com.anigenero.configuration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationExceptionTest {

    @Test
    void testConfigurationException() {

        final ConfigurationException configurationException = new ConfigurationException("test");

        assertThat(configurationException.getMessage()).isEqualTo("test");

    }

    @Test
    void testConfigurationExceptionWithCause() {

        final Exception cause = new Exception("Test exception");

        final ConfigurationException configurationException = new ConfigurationException("test", cause);

        assertThat(configurationException.getMessage()).isEqualTo("test");
        assertThat(configurationException.getCause()).isEqualTo(cause);

    }

}