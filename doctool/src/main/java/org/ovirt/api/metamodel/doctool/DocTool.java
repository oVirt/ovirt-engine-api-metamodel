/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.doctool;

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
import org.ovirt.api.metamodel.tool.BuiltinTypes;
import org.ovirt.api.metamodel.tool.JavaPackages;
import org.ovirt.api.metamodel.tool.Names;
import org.ovirt.api.metamodel.tool.Style;
import org.ovirt.api.metamodel.tool.VersionedJavaNames;

@ApplicationScoped
public class DocTool {
    // Regular expression used to extract name and value from documentation attribute options:
    private static final Pattern ADOC_ATTRIBUTE_RE = Pattern.compile("^(?<name>[^:]+):(?<value>.*)?$");

    // References to the objects that implement the rules to generate names for Java concepts:
    @Inject private JavaPackages javaPackages;

    // Reference to the object used to generate Java names:
    @Inject
    @Style("versioned")
    private VersionedJavaNames versionedJavaNames;

    // References to the objects used to generate code:
    @Inject private XmlDescriptionGenerator xmlDescriptionGenerator;
    @Inject private JsonDescriptionGenerator jsonDescriptionGenerator;
    @Inject private AsciiDocGenerator docGenerator;
    @Inject private DocReportGenerator reportGenerator;
    @Inject private AsciiDocConfiguration adocConfiguration;

    // Reference to the object used to add built-in types to the model:
    @Inject private BuiltinTypes builtinTypes;

    // The names of the command line options:
    private static final String MODEL_OPTION = "model";
    private static final String XML_DESCRIPTION_OPTION = "xml-description";
    private static final String JSON_DESCRIPTION_OPTION = "json-description";
    private static final String VERSION_PREFIX_OPTION = "version-prefix";
    private static final String DOCS_OPTION = "docs";
    private static final String REPORT_OPTION = "report";
    private static final String ADOC_ATTRIBUTE_OPTION = "adoc-attribute";
    private static final String ADOC_SEPARATOR_OPTION = "adoc-separator";
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

        // Options for the location of the generated XML and JSON model representations:
        options.addOption(Option.builder()
            .longOpt(XML_DESCRIPTION_OPTION)
            .desc(
                "The location of the generated XML description of the model. If not specified then the XML " +
                "description isn't generated.")
            .type(File.class)
            .required(false)
            .hasArg(true)
            .argName("FILE")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(JSON_DESCRIPTION_OPTION)
            .desc(
                "The location of the generated JSON description of the model. If not specified then the JSON " +
                "description isn't generated.")
            .type(File.class)
            .required(false)
            .hasArg(true)
            .argName("FILE")
            .build()
        );

        // Options for the generation of documentation:
        options.addOption(Option.builder()
            .longOpt(DOCS_OPTION)
            .desc("The directory where the generated documentation will be created.")
            .type(File.class)
            .required(false)
            .hasArg(true)
            .argName("DIRECTORY")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(ADOC_ATTRIBUTE_OPTION)
            .desc(
                "An attribute to be included in the generated AsciiDoc documentation. The value of the argument " +
                "should be the name attribute, followed by an optional colon and the value of the attribute."
            )
            .required(false)
            .hasArg(true)
            .argName("ATTRIBUTE")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(ADOC_SEPARATOR_OPTION)
            .desc(
                "The character to use as the separator of section identifiers in the generated AsciiDoc " +
                "documentation. If not specified the forward slash character will be used."
            )
            .required(false)
            .hasArg(true)
            .argName("SEPARATOR")
            .build()
        );
        options.addOption(Option.builder()
            .longOpt(REPORT_OPTION)
            .desc("The file where the documentation report be created.")
            .type(File.class)
            .required(false)
            .hasArg(true)
            .argName("FILE")
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
        File xmlFile = (File) line.getParsedOptionValue(XML_DESCRIPTION_OPTION);
        File jsonFile = (File) line.getParsedOptionValue(JSON_DESCRIPTION_OPTION);
        File docsDir = (File) line.getParsedOptionValue(DOCS_OPTION);
        File reportFile = (File) line.getParsedOptionValue(REPORT_OPTION);

        // Analyze the model files:
        Model model = new Model();
        ModelAnalyzer modelAnalyzer = new ModelAnalyzer();
        modelAnalyzer.setModel(model);
        modelAnalyzer.analyzeSource(modelFile);

        // Add the built-in types to the model:
        builtinTypes.addBuiltinTypes(model);

        // Extract the documentation attributes:
        String[] adocAttributeArgs = line.getOptionValues(ADOC_ATTRIBUTE_OPTION);
        if (adocAttributeArgs != null) {
            for (String adocAttributeArg : adocAttributeArgs) {
                Matcher adocAttributeMatch = ADOC_ATTRIBUTE_RE.matcher(adocAttributeArg);
                if (!adocAttributeMatch.matches()) {
                    throw new IllegalArgumentException(
                        "The AsciiDoc attribute \"" + adocAttributeArg + "\" doesn't match regular " +
                        "expression \"" + ADOC_ATTRIBUTE_RE.pattern() + "\"."
                    );
                }
                String adocAttributeName = adocAttributeMatch.group("name");
                String adocAttributeValue = adocAttributeMatch.group("value");
                adocConfiguration.setAttribute(adocAttributeName, adocAttributeValue);
            }
        }

        // Get the AsciiDoc section id separator:
        String adocSeparator = line.getOptionValue(ADOC_SEPARATOR_OPTION);
        if (adocSeparator != null) {
            adocConfiguration.setSeparator(adocSeparator);
        }

        // Generate the XML representation of the model:
        if (xmlFile != null) {
            File xmlDir = xmlFile.getParentFile();
            FileUtils.forceMkdir(xmlDir);
            xmlDescriptionGenerator.generate(model, xmlFile);
        }

        // Generate the JSON representation of the model:
        if (jsonFile != null) {
            File jsonDir = jsonFile.getParentFile();
            FileUtils.forceMkdir(jsonDir);
            jsonDescriptionGenerator.generate(model, jsonFile);
        }

        // Generate the documentation:
        if (docsDir != null) {
            docGenerator.setOutDir(docsDir);
            docGenerator.generate(model);
        }
        if (reportFile != null) {
            reportGenerator.setOutFile(reportFile);
            reportGenerator.generate(model);
        }
    }
}
