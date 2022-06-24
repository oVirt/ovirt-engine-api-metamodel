/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.ovirt.api.metamodel.analyzer.ModelAnalyzer;
import org.ovirt.api.metamodel.concepts.Model;

@ApplicationScoped
public class Tool {
    // Regular expression used to extract name and value from documentation attribute options:
    private static final Pattern ADOC_ATTRIBUTE_RE = Pattern.compile("^(?<name>[^:]+):(?<value>.*)?$");

    // References to the objects that implement the rules to generate names for Java concepts:
    @Inject private JavaPackages javaPackages;

    // Reference to the object used to generate Java names:
    @Inject
    @Style("versioned")
    private VersionedJavaNames versionedJavaNames;

    // References to the objects used to generate code:
    @Inject private SchemaGenerator schemaGenerator;
    @Inject private JaxrsGenerator jaxrsGenerator;
    @Inject private TypesGenerator typesGenerator;
    @Inject private JaxrsHelperGenerator jaxrsHelperGenerator;
    @Inject private XmlSupportGenerator xmlSupportGenerator;
    @Inject private JsonSupportGenerator jsonSupportGenerator;

    // Reference to the object used to add built-in types to the model:
    @Inject private BuiltinTypes builtinTypes;

    // The names of the command line options:
    private static final String MODEL_OPTION = "model";
    private static final String IN_SCHEMA_OPTION = "in-schema";
    private static final String OUT_SCHEMA_OPTION = "out-schema";
    private static final String JAVA_OPTION = "java";
    private static final String JAXRS_OPTION = "jaxrs";
    private static final String VERSION_PREFIX_OPTION = "version-prefix";
    private static final String RESOURCES_OPTION = "resources";

    // Names of options for Java package names:
    private static final String JAXRS_PACKAGE_OPTION = "jaxrs-package";
    private static final String XJC_PACKAGE_OPTION = "xjc-package";
    private static final String TYPES_PACKAGE_OPTION = "types-package";
    private static final String CONTAINERS_PACKAGE_OPTION = "containers-package";
    private static final String BUILDERS_PACKAGE_OPTION = "builders-package";
    private static final String JSON_PACKAGE_OPTION = "json-package";
    private static final String XML_PACKAGE_OPTION = "xml-package";

    public void run(String[] args) throws Exception {
        // Create the command line options:
        Options options = new Options();

        // Options for the locations of files and directories:
        options.addOption(Option.builder()
            .longOpt(MODEL_OPTION)
            .desc("The directory or .jar file containing the source model files.")
            .type(File.class)
            .required(true)
            .hasArg(true)
            .argName("DIRECTORY|JAR")
            .build()
        );

        // Options for the location of the input and output XML schemas:
        options.addOption(Option.builder()
            .longOpt(IN_SCHEMA_OPTION)
            .desc("The XML schema input file.")
            .type(File.class)
            .required(false)
            .hasArg(true)
            .argName("FILE")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(OUT_SCHEMA_OPTION)
            .desc("The XML schema output file.")
            .type(File.class)
            .required(false)
            .hasArg(true)
            .argName("FILE")
            .build()
        );

        // Options for the names of generated Java sources:
        options.addOption(Option.builder()
            .longOpt(JAVA_OPTION)
            .desc("The directory where the generated Java source will be created.")
            .type(File.class)
            .required(false)
            .hasArg(true)
            .argName("DIRECTORY")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(JAXRS_OPTION)
            .desc("The directory where the generated JAX-RS source will be created.")
            .type(File.class)
            .required(false)
            .hasArg(true)
            .argName("DIRECTORY")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(JAXRS_PACKAGE_OPTION)
            .desc("The name of the Java package for JAX-RS interfaces.")
            .required(false)
            .hasArg(true)
            .argName("PACKAGE")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(XJC_PACKAGE_OPTION)
            .desc("The name of the Java package for classes generated by the XJC compiler.")
            .required(false)
            .hasArg(true)
            .argName("PACKAGE")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(TYPES_PACKAGE_OPTION)
            .desc("The name of the Java package for the generated type interfaces.")
            .required(false)
            .hasArg(true)
            .argName("PACKAGE")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(CONTAINERS_PACKAGE_OPTION)
            .desc("The name of the Java package for the generated type containers.")
            .required(false)
            .hasArg(true)
            .argName("PACKAGE")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(BUILDERS_PACKAGE_OPTION)
            .desc("The name of the Java package for the generated type builders.")
            .required(false)
            .hasArg(true)
            .argName("PACKAGE")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(JSON_PACKAGE_OPTION)
            .desc("The name of the Java package for the generated JSON readers and writers.")
            .required(false)
            .hasArg(true)
            .argName("PACKAGE")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(XML_PACKAGE_OPTION)
            .desc("The name of the Java package for the generated XML readers and writers.")
            .required(false)
            .hasArg(true)
            .argName("PACKAGE")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(VERSION_PREFIX_OPTION)
            .desc("The version prefix to add to the generated Java class names, for example V4.")
            .required(false)
            .hasArg(true)
            .argName("PREFIX")
            .build()
        );

        options.addOption(Option.builder()
            .longOpt(RESOURCES_OPTION)
            .desc("The directory where the resources files will be created.")
            .type(File.class)
            .required(false)
            .hasArg(true)
            .argName("DIRECTORY")
            .build()
        );

        // Parse the command line:
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        }
        catch (ParseException exception) {
            System.err.println(exception.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.setSyntaxPrefix("Usage: ");
            formatter.printHelp("metamodel-tool [OPTIONS]", options);
            System.exit(1);
        }

        // Extract the locations of files and directories from the command line:
        File modelFile = (File) line.getParsedOptionValue(MODEL_OPTION);
        File inSchemaFile = (File) line.getParsedOptionValue(IN_SCHEMA_OPTION);
        File outSchemaFile = (File) line.getParsedOptionValue(OUT_SCHEMA_OPTION);
        File jaxrsDir = (File) line.getParsedOptionValue(JAXRS_OPTION);
        File javaDir = (File) line.getParsedOptionValue(JAVA_OPTION);
        File resourcesDir = (File) line.getParsedOptionValue(RESOURCES_OPTION);

        // Analyze the model files:
        Model model = new Model();
        ModelAnalyzer modelAnalyzer = new ModelAnalyzer();
        modelAnalyzer.setModel(model);
        modelAnalyzer.analyzeSource(modelFile);

        // Add the built-in types to the model:
        builtinTypes.addBuiltinTypes(model);

        // Extract the version prefix from the command line and copy it to the object that manages names:
        String versionPrefix = line.getOptionValue(VERSION_PREFIX_OPTION);
        if (versionPrefix != null) {
            versionedJavaNames.setVersionPrefix(versionPrefix);
        }

        // Extract the names of the Java packages from the command line and copy them to the object that manages them:
        String[] jaxrsPackages = line.getOptionValues(JAXRS_PACKAGE_OPTION);
        if (jaxrsPackages != null) {
            for (String jaxrsPackage : jaxrsPackages) {
                javaPackages.addJaxrsRule(jaxrsPackage);
            }
        }
        String xjcPackage = line.getOptionValue(XJC_PACKAGE_OPTION);
        if (xjcPackage != null) {
            javaPackages.setXjcPackageName(xjcPackage);
        }
        String typesPackage = line.getOptionValue(TYPES_PACKAGE_OPTION);
        if (typesPackage != null) {
            javaPackages.setTypesPackageName(typesPackage);
        }
        String containersPackage = line.getOptionValue(CONTAINERS_PACKAGE_OPTION);
        if (containersPackage != null) {
            javaPackages.setContainersPackageName(containersPackage);
        }
        String buildersPackage = line.getOptionValue(BUILDERS_PACKAGE_OPTION);
        if (buildersPackage != null) {
            javaPackages.setBuildersPackageName(buildersPackage);
        }
        String jsonPackage = line.getOptionValue(JSON_PACKAGE_OPTION);
        if (jsonPackage != null) {
            javaPackages.setJsonPackageName(jsonPackage);
        }
        String xmlPackage = line.getOptionValue(XML_PACKAGE_OPTION);
        if (xmlPackage != null) {
            javaPackages.setXmlPackageName(xmlPackage);
        }

        // Generate the XML schema:
        if (inSchemaFile != null && outSchemaFile != null) {
            schemaGenerator.setInFile(inSchemaFile);
            schemaGenerator.setOutFile(outSchemaFile);
            schemaGenerator.generate(model);
        }

        // Generate the JAX-RS source:
        if (jaxrsDir != null) {
            FileUtils.forceMkdir(jaxrsDir);
            jaxrsGenerator.setOutDir(jaxrsDir);
            jaxrsGenerator.generate(model);
            // Generate the JAX-RS helper classes):
            jaxrsHelperGenerator.setOutDir(jaxrsDir);
            jaxrsHelperGenerator.generate(model);
        }

        // Generate the Java source:
        if (javaDir != null) {
            typesGenerator.setOutDir(javaDir);
            typesGenerator.generate(model);

            // Generate JSON support classes:
            jsonSupportGenerator.setOutDir(javaDir);
            jsonSupportGenerator.generate(model);

            // Generate XML support classes:
            xmlSupportGenerator.setOutDir(javaDir);
            xmlSupportGenerator.setResourcesDir(resourcesDir);
            xmlSupportGenerator.generate(model);
        }
    }
}
