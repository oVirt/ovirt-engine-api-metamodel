/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.doctool;

import java.io.File;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ovirt.api.metamodel.concepts.Annotation;
import org.ovirt.api.metamodel.concepts.AnnotationParameter;
import org.ovirt.api.metamodel.concepts.Attribute;
import org.ovirt.api.metamodel.concepts.Concept;
import org.ovirt.api.metamodel.concepts.Document;
import org.ovirt.api.metamodel.concepts.EnumType;
import org.ovirt.api.metamodel.concepts.EnumValue;
import org.ovirt.api.metamodel.concepts.Link;
import org.ovirt.api.metamodel.concepts.ListType;
import org.ovirt.api.metamodel.concepts.Locator;
import org.ovirt.api.metamodel.concepts.Method;
import org.ovirt.api.metamodel.concepts.Model;
import org.ovirt.api.metamodel.concepts.Name;
import org.ovirt.api.metamodel.concepts.Named;
import org.ovirt.api.metamodel.concepts.Parameter;
import org.ovirt.api.metamodel.concepts.PrimitiveType;
import org.ovirt.api.metamodel.concepts.Service;
import org.ovirt.api.metamodel.concepts.StructType;
import org.ovirt.api.metamodel.concepts.Type;
import org.ovirt.api.metamodel.runtime.xml.XmlWriter;

/**
 * This class takes a model and generates its XML description.
*/
@ApplicationScoped
public class XmlDescriptionGenerator {
    private Model model;
    private XmlWriter writer;

    // Reference to the object that converts documentation to HTML:
    @Inject
    private HtmlGenerator htmlGenerator;

    public void generate(Model model, File file) {
        // Save the model:
        this.model = model;

        // Create the XML writer:
        try (XmlWriter tmp = new XmlWriter(file, true)) {
            writer = tmp;
            writeModel();
        }
    }

    private void writeModel() {
        writer.writeStartElement("model");
        Service root = model.getRoot();
        if (root != null) {
            writer.writeElement("root", getServiceRef(root));
        }
        writer.writeStartElement("types");
        model.types().forEach(this::writeType);
        writer.writeEndElement();
        writer.writeStartElement("services");
        model.services().forEach(this::writeService);
        writer.writeEndElement();
        writer.writeStartElement("documents");
        model.documents().forEach(this::writeDocument);
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private void writeType(Type type) {
        if (type instanceof PrimitiveType) {
            writePrimitiveType((PrimitiveType) type);
        }
        else if (type instanceof StructType) {
            writeStructType((StructType) type);
        }
        else if (type instanceof EnumType) {
            writeEnumType((EnumType) type);
        }
    }

    private void writePrimitiveType(PrimitiveType type) {
        writer.writeStartElement("primitive");
        writeCommon(type);
        writer.writeEndElement();
    }

    private void writeStructType(StructType type) {
        writer.writeStartElement("struct");
        writeCommon(type);
        type.attributes().forEach(this::writeAttribute);
        type.links().forEach(this::writeStructLink);
        writer.writeEndElement();
    }

    private void writeAttribute(Attribute attribute) {
        writer.writeStartElement("attribute");
        writeCommon(attribute);
        writeTypeRef(attribute.getType());
        writer.writeEndElement();
    }

    private void writeStructLink(Link link) {
        writer.writeStartElement("link");
        writeCommon(link);
        writeTypeRef(link.getType());
        writer.writeEndElement();
    }

    private void writeEnumType(EnumType type) {
        writer.writeStartElement("enum");
        writeCommon(type);
        type.values().forEach(this::writeEnumValue);
        writer.writeEndElement();
    }

    private void writeEnumValue(EnumValue value) {
        writer.writeStartElement("value");
        writeCommon(value);
        writer.writeEndElement();
    }

    private void writeService(Service service) {
        writer.writeStartElement("service");
        writeCommon(service);
        service.methods().forEach(this::writeServiceMethod);
        service.locators().forEach(this::writeServiceLocator);
        writer.writeEndElement();
    }

    private void writeServiceMethod(Method method) {
        writer.writeStartElement("method");
        writeCommon(method);
        method.parameters().forEach(this::writeParameter);
        writer.writeEndElement();
    }

    private void writeServiceLocator(Locator locator) {
        writer.writeStartElement("locator");
        writeCommon(locator);
        writeServiceRef(locator.getService());
        locator.parameters().forEach(this::writeParameter);
        writer.writeEndElement();
    }

    private void writeParameter(Parameter parameter) {
        writer.writeStartElement("parameter");
        writeCommon(parameter);
        writer.writeElement("in", Boolean.toString(parameter.isIn()));
        writer.writeElement("out", Boolean.toString(parameter.isIn()));
        writeTypeRef(parameter.getType());
        writer.writeEndElement();
    }

    private void writeDocument(Document document) {
        writer.writeStartElement("document");
        writeName(document);
        String source = document.getSource();
        if (source != null) {
            writer.writeElement("source", source);
            String html = htmlGenerator.toHtml(source);
            if (html != null) {
                writer.writeElement("html", html);
            }
        }
        writer.writeEndElement();
    }

    private void writeCommon(Concept concept) {
        writeDoc(concept);
        writeAnnotations(concept);
        writeName(concept);
    }

    private void writeName(Named named) {
        Name name = named.getName();
        if (name != null) {
            writer.writeElement("name", name.toString());
        }
    }

    private void writeDoc(Concept concept) {
        String doc = concept.getDoc();
        if (doc != null) {
            writer.writeElement("doc", doc);
            String html = htmlGenerator.toHtml(doc);
            if (html != null) {
                writer.writeElement("html", html);
            }
        }
    }

    private void writeAnnotations(Concept concept) {
        List<Annotation> annotations = concept.getAnnotations();
        if (!annotations.isEmpty()) {
            writer.writeStartElement("annotations");
            annotations.forEach(this::writeAnnotation);
            writer.writeEndElement();
        }
    }

    private void writeAnnotation(Annotation annotation) {
        writer.writeStartElement("annotation");
        writeName(annotation);
        List<AnnotationParameter> parameters = annotation.getParameters();
        if (!parameters.isEmpty()) {
            writer.writeStartElement("parameters");
            annotation.parameters().forEach(this::writeAnnotationParameter);
            writer.writeEndElement();
        }
        writer.writeEndElement();
    }

    private void writeAnnotationParameter(AnnotationParameter parameter) {
        writer.writeStartElement("parameter");
        writeName(parameter);
        List<String> values = parameter.getValues();
        if (!values.isEmpty()) {
            writer.writeStartElement("values");
            values.forEach(x -> writer.writeElement("value", x));
            writer.writeEndElement();
        }
        writer.writeEndElement();
    }

    private void writeTypeRef(Type type) {
        writer.writeElement("type", getTypeRef(type));
    }

    private String getTypeRef(Type type) {
        if (type instanceof StructType || type instanceof PrimitiveType || type instanceof EnumType) {
            return type.getName().toString();
        }
        if (type instanceof ListType) {
            ListType listType = (ListType) type;
            Type elementType = listType.getElementType();
            return getTypeRef(elementType) + "[]";
        }
        return "";
    }

    private void writeServiceRef(Service service) {
        writer.writeElement("service", getServiceRef(service));
    }

    private String getServiceRef(Service service) {
        return service.getName().toString();
    }
}

