package com.github.leifoolsen.jerseyguicepersist.embeddedjetty;

import com.github.leifoolsen.jerseyguicepersist.config.ApplicationConfig;
import org.eclipse.jetty.server.Server;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JettyFactoryTest {

    @Test
    //@Ignore  // Tested in UserResourceTest. Uses this test to verify startup time
    public void startJetty() throws Exception {
        ApplicationConfig.load();

        assertThat("ApplicationConfig.stage() does NOT report Stage.TEST! " +
                "This could be a config error or the 'org.junit' dependency is not on class path",
                ApplicationConfig.stage(), is(ApplicationConfig.Stage.TEST));

        Server server = JettyFactory.createServer(ApplicationConfig.jettyConfig());

        JettyFactory.start(server);
        assertThat(server.isRunning(), is(true));
        JettyFactory.stop(server);
    }
}