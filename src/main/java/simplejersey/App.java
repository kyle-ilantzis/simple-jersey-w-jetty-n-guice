package simplejersey;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.jvnet.hk2.guice.bridge.internal.GuiceIntoHK2BridgeImpl;
import simplejersey.modules.HelloWorldModule;
import simplejersey.resources.HelloWorldResource;

import javax.inject.Inject;
import javax.ws.rs.core.FeatureContext;

public class App
{
    public static void main( String[] args ) throws Exception {

        // Our Guice injector
        Injector injector = Guice.createInjector( new HelloWorldModule() );

        // We set the injector for our jersey integration feature
        GuiceIntegrationFeature.guiceInjector = injector;

        // Our resource configuration for jersey
        ResourceConfig rc = new ResourceConfig(  );
        // It's important that we register our guice integration feature
        rc.register( GuiceIntegrationFeature.class );
        rc.register( HelloWorldResource.class );
        // ADD HERE!
        // rc.register( PeopleResource.class );
        // rc.register( PeoplePurchases.class );
        // ...

        // This container (which is a servlet) will route requests to our resources
        ServletContainer sc = new ServletContainer( rc );

        // To register a servlet instance with a ServletHandler we need to wrap the instance
        // in a holder.
        ServletHolder h = new ServletHolder( sc );

        // Jetty server needs a handler to requests.
        // Here we will make a handler for servlets
        ServletHandler sh = new ServletHandler();
        // and register an jersey resource config (which is wrapped in a ServletContainer which is wrapped in
        // a ServletHolder)
        sh.addServletWithMapping( h, "/" );

        // The simplest possibly jetty server
        Server server = new Server(8080);
        // and we add our handler that forwards to jersey
        server.setHandler( sh );
        // run the simplest jetty server
        server.start();
        server.join();
    }

    // Bridge Guice into jersey's HK2 dependency framework instance.
    public static class GuiceIntegrationFeature implements javax.ws.rs.core.Feature {

        public static Injector guiceInjector;

        private final ServiceLocator sl;

        @Inject
        public GuiceIntegrationFeature( ServiceLocator sl ) {
            this.sl = sl;
        }

        @Override
        public boolean configure( FeatureContext context ) {
            GuiceBridge.getGuiceBridge().initializeGuiceBridge( sl );
            GuiceIntoHK2Bridge guiceBridge = sl.getService( GuiceIntoHK2BridgeImpl.class );
            guiceBridge.bridgeGuiceInjector( guiceInjector );
            return true;
        }

    }
}
