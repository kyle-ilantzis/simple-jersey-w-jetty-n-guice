package simplejersey.pojos.impl;

import simplejersey.pojos.Message;

/**
 * @author kilantzis
 */
public class HelloWorldMessage implements Message{

    @Override
    public String getText() {
        return "Hello, World!";
    }
}
