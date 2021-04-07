/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ovirt.api.metamodel.tool;

import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ovirt.api.metamodel.concepts.ListType;
import org.ovirt.api.metamodel.concepts.Model;
import org.ovirt.api.metamodel.concepts.Name;
import org.ovirt.api.metamodel.concepts.NameParser;
import org.ovirt.api.metamodel.concepts.PrimitiveType;
import org.ovirt.api.metamodel.concepts.StructType;
import org.ovirt.api.metamodel.concepts.Type;

/**
 * This class contains methods that compute names of XML schema types and also of Java classes generated by the JAXB
 * compiler from the XML schema.
 */
@ApplicationScoped
public class SchemaNames {
    // Well known names:
    private static final Name HREF = NameParser.parseUsingCase("Href");
    private static final Name ID = NameParser.parseUsingCase("Id");
    private static final Name REL = NameParser.parseUsingCase("Rel");

    // Exceptions to the rules to calculate complex type names:
    private static final Map<String, String> TYPE_NAME_EXCEPTIONS = new HashMap<>();

    static {
        TYPE_NAME_EXCEPTIONS.put("Device", "BaseDevice");
    }

    // Exceptions to the rules to calculate tag names:
    private static final Map<String, String> TAG_NAME_EXCEPTIONS = new HashMap<>();

    static {
        TAG_NAME_EXCEPTIONS.put("gluster_brick", "brick");
        TAG_NAME_EXCEPTIONS.put("gluster_brick_memory_info", "brick_memoryinfo");
        TAG_NAME_EXCEPTIONS.put("gluster_bricks", "bricks");
        TAG_NAME_EXCEPTIONS.put("gluster_memory_pool", "memory_pool");
        TAG_NAME_EXCEPTIONS.put("gluster_memory_pools", "memory_pools");
        TAG_NAME_EXCEPTIONS.put("gluster_server_hook", "server_hook");
        TAG_NAME_EXCEPTIONS.put("gluster_server_hooks", "server_hooks");
        TAG_NAME_EXCEPTIONS.put("migration_options", "migration");
        TAG_NAME_EXCEPTIONS.put("numa_node", "host_numa_node");
        TAG_NAME_EXCEPTIONS.put("numa_nodes", "host_numa_nodes");
        TAG_NAME_EXCEPTIONS.put("open_stack_image", "openstack_image");
        TAG_NAME_EXCEPTIONS.put("open_stack_image_provider", "openstack_image_provider");
        TAG_NAME_EXCEPTIONS.put("open_stack_image_providers", "openstack_image_providers");
        TAG_NAME_EXCEPTIONS.put("open_stack_images", "openstack_images");
        TAG_NAME_EXCEPTIONS.put("open_stack_network", "openstack_network");
        TAG_NAME_EXCEPTIONS.put("open_stack_network_provider", "openstack_network_provider");
        TAG_NAME_EXCEPTIONS.put("open_stack_network_providers", "openstack_network_providers");
        TAG_NAME_EXCEPTIONS.put("open_stack_networks", "openstack_networks");
        TAG_NAME_EXCEPTIONS.put("open_stack_subnet", "openstack_subnet");
        TAG_NAME_EXCEPTIONS.put("open_stack_subnets", "openstack_subnets");
        TAG_NAME_EXCEPTIONS.put("open_stack_volume", "openstack_volume");
        TAG_NAME_EXCEPTIONS.put("open_stack_volume_provider", "openstack_volume_provider");
        TAG_NAME_EXCEPTIONS.put("open_stack_volume_providers", "openstack_volume_providers");
        TAG_NAME_EXCEPTIONS.put("open_stack_volumes", "openstack_volumes");
        TAG_NAME_EXCEPTIONS.put("operating_system", "os");
        TAG_NAME_EXCEPTIONS.put("operating_system_info", "operating_system");
        TAG_NAME_EXCEPTIONS.put("operating_system_infos", "operation_systems");
        TAG_NAME_EXCEPTIONS.put("operating_systems", "oss");
        TAG_NAME_EXCEPTIONS.put("transparent_huge_pages", "transparent_hugepages");
        TAG_NAME_EXCEPTIONS.put("virtual_numa_node", "vm_numa_node");
        TAG_NAME_EXCEPTIONS.put("virtual_numa_nodes", "vm_numa_nodes");
    }

    private static final Set<Name> SCHEMA_ENUMS = new HashSet<>();

    static {
        SCHEMA_ENUMS.add(NameParser.parseUsingCase("StatisticUnit"));
        SCHEMA_ENUMS.add(NameParser.parseUsingCase("StatisticKind"));
        SCHEMA_ENUMS.add(NameParser.parseUsingCase("ValueType"));
    }

    // References to the objects used to compute names:
    @Inject Names names;
    @Inject JavaNames javaNames;
    @Inject JavaPackages javaPackages;

    public JavaTypeReference getXjcTypeReference(Type type) {
        if (type instanceof PrimitiveType) {
            return getXjcPrimitiveTypeReference((PrimitiveType) type);
        }
        if (type instanceof StructType) {
            return getXjcStructTypeReference((StructType) type);
        }
        if (type instanceof ListType) {
            return getXjcListTypeReference((ListType) type);
        }
        throw new RuntimeException("Don't know how to calculate the Java type reference for type \"" + type + "\"");
    }

    private JavaTypeReference getXjcPrimitiveTypeReference(PrimitiveType type) {
        JavaTypeReference reference = new JavaTypeReference();
        Model model = type.getModel();
        if (type == model.getBooleanType()) {
            reference.setText("bool");
        }
        else if (type == model.getStringType()) {
            reference.setText("String");
        }
        else if (type == model.getIntegerType()) {
            reference.setText("int");
        }
        else if (type == model.getDateType()) {
            reference.setText("Date");
        }
        else if (type == model.getDecimalType()) {
            reference.setText("double");
        }
        else {
            throw new RuntimeException("Don't know how to calculate the Java type reference for type \"" + type + "\"");
        }
        return reference;
    }

    private JavaTypeReference getXjcStructTypeReference(StructType type) {
        JavaTypeReference reference = new JavaTypeReference();
        String text = getSchemaTypeName(type.getName());
        reference.setText(text);
        reference.addImport(javaPackages.getXjcPackageName(), text);
        return reference;
    }

    private JavaTypeReference getXjcListTypeReference(ListType type) {
        Type elementType = type.getElementType();
        if (elementType instanceof StructType) {
            JavaTypeReference reference = new JavaTypeReference();
            String text = getSchemaTypeName(names.getPlural(elementType.getName()));
            reference.setText(text);
            reference.addImport(javaPackages.getXjcPackageName(), text);
            return reference;
        }
        else {
            throw new RuntimeException("Don't know how to calculate the Java type reference for type \"" + type + "\"");
        }
    }

    public String getSchemaTypeName(Type type) {
        Model model = type.getModel();
        if (type == model.getBooleanType()) {
            return "xs:boolean";
        }
        if (type == model.getStringType()) {
            return "xs:string";
        }
        if (type == model.getIntegerType()) {
            return "xs:int";
        }
        if (type == model.getDateType()) {
            return "xs:dateTime";
        }
        if (type == model.getDecimalType()) {
            return "xs:decimal";
        }
        if (type instanceof ListType) {
            ListType listType = (ListType) type;
            Type elementType = listType.getElementType();
            return getSchemaTypeName(names.getPlural(elementType.getName()));
        }
        return getSchemaTypeName(type.getName());
    }


    public String getSchemaTypeName(Name name) {
        String result = javaNames.getJavaClassStyleName(name);
        String exception = TYPE_NAME_EXCEPTIONS.get(result);
        if (exception != null) {
            result = exception;
        }
        return result;
    }

    public String getSchemaTagName(Name name) {
        String result = name.words().map(String::toLowerCase).collect(joining("_"));
        String exception = TAG_NAME_EXCEPTIONS.get(result);
        if (exception != null) {
            result = exception;
        }
        return result;
    }

    /**
     * Only specific enums should be added to the schema; most enums in the
     * model should not. This method returns true for enums that should be
     * added to the schema, and false for enums that should not.
     */
    public boolean isSchemaEnum(Type type) {
        return SCHEMA_ENUMS.contains(type.getName());
    }

    /**
     * Checks if the struct member with the given name should be represented in the XML schema as an attribute instead
     * of an nested element. Currently this is necessary for the {@code id} and {@code href} attribute.
     *
     * @param name the name to check
     * @return {@code true} if the attribute with the given name should be represented as an XML attribute,
     *     {@code false} otherwise
     */
    public boolean isRepresentedAsAttribute(Name name) {
        return HREF.equals(name) || ID.equals(name) || REL.equals(name);
    }
}

