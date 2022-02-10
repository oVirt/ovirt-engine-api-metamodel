/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for bootstrapping the CDI container, creating the application entry point and running it
 * with the command line arguments.
 */
public class Main {
    private static final Logger log;

    static {
        // loads logging.properties from the classpath
        String path = Main.class.getClassLoader().getResource("logging.properties").getFile();
        System.setProperty("java.util.logging.config.file", path);

        // initialize logger
        log = LoggerFactory.getLogger(Main.class);

    }

    public static void main(String[] args) {
        // The first argument must be the name of the tool class:
        if (args.length < 1) {
            log.error("The first argument must be the fully qualified name of the tool class.");
            System.exit(1);
        }
        String toolName = args[0];

        // The rest of the arguments are passed to the tool:
        String[] toolArgs = new String[args.length - 1];
        System.arraycopy(args, 1, toolArgs, 0, toolArgs.length);

        // Load the tool class:
        ClassLoader toolLoader = Thread.currentThread().getContextClassLoader();
        Class<?> toolClass = null;
        try {
            toolClass = toolLoader.loadClass(toolName);
        }
        catch (ClassNotFoundException exception) {
            log.error("Can't load tool class \"{}\".", toolName, exception);
            System.exit(1);
        }

        // Create the CDI container:
        Weld weld = new Weld();
        WeldContainer container = weld.initialize();

        // Create a CDI bean for the tool class:
        Object toolBean = container.instance().select(toolClass).get();

        // Find and execute the "run" method of the CDI bean:
        Method runMethod = null;
        try {
            runMethod = toolClass.getMethod("run", String[].class);
        }
        catch (NoSuchMethodException exception) {
            log.error("Can't find the \"run\" method in tool class \"{}\".", toolName, exception);
            System.exit(1);
        }
        try {
            runMethod.invoke(toolBean, new Object[] { toolArgs });
        }
        catch (IllegalAccessException | InvocationTargetException exception) {
            log.error("Error while executing the \"run\" method of tool class \"{}\".", toolName, exception);
            System.exit(1);
        }

        // When the tool finishes, shutdown the CDI container:
        weld.shutdown();
    }
}
