package com.github.leifoolsen.jerseyguicepersist.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ConfigJsonGsonUnMarshalTest {

    private static final String JETTY_CONFIG_ROOT = "application.jettyConfig";

    @Test
    public void unMarshalConfig() {
        final Config config = ConfigFactory
                .load("application-test")
                .withFallback(ConfigFactory.load())
                .getConfig(JETTY_CONFIG_ROOT);

        assertThat(config, notNullValue());

        Gson gson = new GsonBuilder().create();

        String json = config.root()
                .render(ConfigRenderOptions.concise().setJson(true).setFormatted(true));

        JettyConfig jettyConfig = gson.fromJson(json, JettyConfig.class);

        assertThat(config, notNullValue());
        assertThat(jettyConfig.serverConfig().useAccessLog(), is(false));
    }
}
