package simplejersey.resources;

import simplejersey.pojos.Message;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author kilantzis
 */
@Path("")
public class HelloWorldResource {

    private final Message message;

    @Inject
    public HelloWorldResource( Message message ) {
        this.message = message;
    }

    @GET
    public String hello() { return message.getText(); }
}