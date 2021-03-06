package simplejersey;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
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

        // We set the injector for our jersey integration feature (using global variable, ah!)
        GuiceIntegrationFeature.guiceInjector = injector;

        // Our resource configuration for jersey
        ResourceConfig rc = new ResourceConfig(  );
        // It's important that we register our guice integration feature
        rc.register( GuiceIntegrationFeature.class );
        rc.register( HelloWorldResource.class );
        // ADD HERE!
        // rc.register( PeopleResource.class );
        // rc.register( PeoplePurchasesResource.class );
        // ...

        // This container (which is a servlet) will route requests to our resources
        ServletContainer sc = new ServletContainer( rc );

        // To register a servlet instance we need to wrap it in a holder.
        ServletHolder h = new ServletHolder( sc );

        // Jetty server needs some sort of handler to process requests.
        ServletContextHandler sch = new ServletContextHandler();
        // We will take the root path for this application
        sch.setContextPath( "/" );
        // All request that enter this context should go to jersey
        sch.addServlet( h, "/*" );

        // The simplest possibly jetty server
        Server server = new Server(8080);
        // and we add our handler that forwards to jersey
        server.setHandler( sch );
        // run the simplest jetty server
        server.start();
        server.join();
    }

    // Bridge Guice into jersey's HK2 dependency framework instance, I suppose.
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
