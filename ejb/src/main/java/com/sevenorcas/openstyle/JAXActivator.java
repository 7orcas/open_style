package com.sevenorcas.openstyle;


import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAXActivator is an arbitrary name, what is important is that javax.ws.rs.core.Application is extended
 * and the @ApplicationPath annotation is used with a "rest" path.  Without this the rest routes linked to
 * from index.html would not be found.<p>
 * 
 * Thanks to https://github.com/wildfly/quickstart/blob/10.x/helloworld-rs/src/main/java/org/jboss/as/quickstarts/rshelloworld/JAXActivator.java
 */
@ApplicationPath("rest")
public class JAXActivator extends Application {
    // Left empty intentionally
}