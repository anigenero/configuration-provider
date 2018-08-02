package com.anigenero.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConfigurationProducerTest {

    private ConfigurationProducer configurationProducer;

    @SuppressWarnings("unused")
    public static class MyContextFactory implements InitialContextFactory {

        @Override
        public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {

            final Context context = mock(Context.class);

            when(context.lookup("configuration.ctx.string")).thenReturn("I do the cha-cha like a sissy girl");
            when(context.lookup("configuration.ctx.int")).thenReturn("1982673");
            when(context.lookup("configuration.ctx.long")).thenReturn("9223372036854775800");
            when(context.lookup("configuration.ctx.bool")).thenReturn("false");

            when(context.lookup("configuration.ctx.missing")).thenThrow(new NamingException());

            final InitialContext initialContext = mock(InitialContext.class);

            when(initialContext.lookup("java:comp/env")).thenReturn(context);

            return initialContext;

        }

    }

    @BeforeEach
    void setUp() throws ConfigurationException {

        System.setProperty("java.naming.factory.initial", this.getClass().getCanonicalName() + "$MyContextFactory");

        System.setProperty("configuration.sys.string", "Try me");
        System.setProperty("configuration.sys.int", "198264");
        System.setProperty("configuration.sys.long", "9223372006854775800");
        System.setProperty("configuration.sys.bool", "true");

        this.configurationProducer = new ConfigurationProducer();
        this.configurationProducer.init();

    }

    @Test
    void testMissing() throws ConfigurationException {

        final InjectionPoint configInjectionPoint = this.mockInjectionPoint("configuration.missing", true);
        final String cfgValue = configurationProducer.produceString(configInjectionPoint);

        assertThat(cfgValue).isNull();

        final InjectionPoint nonNullableConfigInjectionPoint = this.mockInjectionPoint("configuration.missing", false);
        assertThrows(ConfigurationException.class, () -> configurationProducer.produceString(nonNullableConfigInjectionPoint));

        final InjectionPoint ctxInjectionPoint = this.mockInjectionPoint("configuration.ctx.missing", true);
        final String ctxValue = configurationProducer.produceString(ctxInjectionPoint);

        assertThat(ctxValue).isNull();

        final InjectionPoint nonNullableCtxInjectionPoint = this.mockInjectionPoint("configuration.ctx.missing", false);
        assertThrows(ConfigurationException.class, () -> configurationProducer.produceString(nonNullableCtxInjectionPoint));

        final InjectionPoint sysInjectionPoint = this.mockInjectionPoint("configuration.sys.missing", true);
        final String sysValue = configurationProducer.produceString(sysInjectionPoint);

        assertThat(sysValue).isNull();

        final InjectionPoint nonNullableSysInjectionPoint = this.mockInjectionPoint("configuration.sys.missing", false);
        assertThrows(ConfigurationException.class, () -> configurationProducer.produceString(nonNullableSysInjectionPoint));

    }

    @Test
    void testProduceString() throws ConfigurationException {

        final InjectionPoint configInjectionPoint = this.mockInjectionPoint("configuration.string", false);
        final String cfgValue = configurationProducer.produceString(configInjectionPoint);

        assertThat(cfgValue).isEqualTo("The fox ran through the field");

        final InjectionPoint ctxInjectionPoint = this.mockInjectionPoint("configuration.ctx.string", false);
        final String ctxValue = configurationProducer.produceString(ctxInjectionPoint);

        assertThat(ctxValue).isEqualTo("I do the cha-cha like a sissy girl");

        final InjectionPoint sysInjectionPoint = this.mockInjectionPoint("configuration.sys.string", false);
        final String sysValue = configurationProducer.produceString(sysInjectionPoint);

        assertThat(sysValue).isEqualTo("Try me");

    }

    @Test
    void testProduceInteger() throws ConfigurationException {

        final InjectionPoint configInjectionPoint = this.mockInjectionPoint("configuration.int", false);
        final Integer cfgValue = configurationProducer.produceInteger(configInjectionPoint);

        assertThat(cfgValue).isEqualTo(2147483647);


        final InjectionPoint ctxInjectionPoint = this.mockInjectionPoint("configuration.ctx.int", false);
        final Integer ctxValue = configurationProducer.produceInteger(ctxInjectionPoint);

        assertThat(ctxValue).isEqualTo(1982673);

        final InjectionPoint sysInjectionPoint = this.mockInjectionPoint("configuration.sys.int", false);
        final Integer sysValue = configurationProducer.produceInteger(sysInjectionPoint);

        assertThat(sysValue).isEqualTo(198264);

        final InjectionPoint missingConfigInjectionPoint = this.mockInjectionPoint("configuration.missing.int", true);
        assertThat(configurationProducer.produceInteger(missingConfigInjectionPoint)).isNull();

    }

    @Test
    void testProduceLong() throws ConfigurationException {

        final InjectionPoint configInjectionPoint = this.mockInjectionPoint("configuration.long", false);
        final Long cfgValue = configurationProducer.produceLong(configInjectionPoint);

        assertThat(cfgValue).isEqualTo(9223372036854775807L);

        final InjectionPoint ctxInjectionPoint = this.mockInjectionPoint("configuration.ctx.long", false);
        final Long ctxValue = configurationProducer.produceLong(ctxInjectionPoint);

        assertThat(ctxValue).isEqualTo(9223372036854775800L);

        final InjectionPoint sysInjectionPoint = this.mockInjectionPoint("configuration.sys.long", false);
        final Long sysValue = configurationProducer.produceLong(sysInjectionPoint);

        assertThat(sysValue).isEqualTo(9223372006854775800L);

        final InjectionPoint missingConfigInjectionPoint = this.mockInjectionPoint("configuration.missing.long", true);
        assertThat(configurationProducer.produceLong(missingConfigInjectionPoint)).isNull();

    }

    @Test
    void testProduceBoolean() throws ConfigurationException {

        final InjectionPoint configInjectionPoint = this.mockInjectionPoint("configuration.bool", false);
        final Boolean cfgValue = configurationProducer.produceBoolean(configInjectionPoint);

        assertThat(cfgValue).isEqualTo(true);

        final InjectionPoint ctxInjectionPoint = this.mockInjectionPoint("configuration.ctx.bool", false);
        final Boolean ctxValue = configurationProducer.produceBoolean(ctxInjectionPoint);

        assertThat(ctxValue).isEqualTo(false);

        final InjectionPoint sysInjectionPoint = this.mockInjectionPoint("configuration.sys.bool", false);
        final Boolean sysValue = configurationProducer.produceBoolean(sysInjectionPoint);

        assertThat(sysValue).isEqualTo(true);

        final InjectionPoint missingConfigInjectionPoint = this.mockInjectionPoint("configuration.missing.bool", true);
        assertThat(configurationProducer.produceBoolean(missingConfigInjectionPoint)).isFalse();

    }

    private InjectionPoint mockInjectionPoint(final String configKey, final boolean nullable) {

        final Configuration mockConfiguration = mock(Configuration.class);

        when(mockConfiguration.value()).thenReturn(configKey);
        when(mockConfiguration.nullable()).thenReturn(nullable);

        final Annotated annotated = mock(Annotated.class);

        when(annotated.getAnnotation(Configuration.class)).thenReturn(mockConfiguration);

        final InjectionPoint injectionPoint = mock(InjectionPoint.class);
        when(injectionPoint.getAnnotated()).thenReturn(annotated);

        return injectionPoint;

    }

}