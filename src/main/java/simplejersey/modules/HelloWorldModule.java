package simplejersey.modules;

import com.google.inject.AbstractModule;
import simplejersey.pojos.Message;
import simplejersey.pojos.impl.HelloWorldMessage;

/**
 * @author kilantzis
 */
public class HelloWorldModule extends AbstractModule {

    @Override
    protected void configure() {
        bind( Message.class ).to( HelloWorldMessage.class );
    }
}
