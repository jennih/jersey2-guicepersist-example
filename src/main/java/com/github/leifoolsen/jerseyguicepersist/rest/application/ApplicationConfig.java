package com.github.leifoolsen.jerseyguicepersist.rest.application;

import com.github.leifoolsen.jerseyguicepersist.guice.GuiceModule;
import com.github.leifoolsen.jerseyguicepersist.guice.PersistenceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.AbstractContainerLifecycleListener;
import org.glassfish.jersey.server.spi.Container;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api/*")
public class ApplicationConfig extends ResourceConfig {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String APPLICATION_PATH;

    static {
        String appPath = "";
        if(ApplicationConfig.class.isAnnotationPresent(ApplicationPath.class)) {
            // Remove '/*' from @ApplicationPath, e.g:  "/api/*" -> /api
            appPath = ApplicationConfig.class.getAnnotation(ApplicationPath.class).value();
            appPath = appPath.substring(0, appPath.endsWith("/*") ? appPath.lastIndexOf("/*") : appPath.length()-1);
        }
        APPLICATION_PATH = appPath;
    }

    @Inject
    public ApplicationConfig(ServiceLocator serviceLocator) {

        logger.debug("Initializing ...");

        configureJerseyLogging();

        guiceHK2Integration(serviceLocator);

        // Enable LoggingFilter & output entity.
        //registerInstances(new LoggingFilter(java.util.logging.Logger.getLogger(this.getClass().getName()), true));

        //
        register(LifecycleHandler.class);

        // Scans during deployment for JAX-RS components in packages
        packages("com.github.leifoolsen.jerseyguicepersist.rest");
    }

    // make Jersey log through SLF4J
    private static void configureJerseyLogging() {
        // Jersey uses java.util.logging. Bridge to slf4j
        java.util.logging.LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        java.util.logging.Logger.getGlobal().setLevel(java.util.logging.Level.FINEST);
        java.util.logging.Logger.getLogger("org.glassfish.jersey").setLevel(java.util.logging.Level.INFO);
    }

    // Guice
    private static void guiceHK2Integration(ServiceLocator serviceLocator) {

        Injector injector = Guice.createInjector(new PersistenceModule(), new GuiceModule());

        // Guice HK2 bridge
        // See e.g. https://github.com/t-tang/jetty-jersey-HK2-Guice-boilerplate
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge bridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        bridge.bridgeGuiceInjector(injector);
    }


    private static class LifecycleHandler extends AbstractContainerLifecycleListener {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public void onStartup(Container container) {
            logger.info(">>> Application Startup");
        }

        @Override
        public void onShutdown(Container container) {
            logger.info(">>> Application Shutdown");
        }
    }
}


/*
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet();
        classes.add(UserResource.class);

        return classes;
    }
}
*/