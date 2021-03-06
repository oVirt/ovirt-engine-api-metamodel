package org.ovirt.api.metamodel.tool;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

import org.ovirt.api.metamodel.concepts.EnumType;
import org.ovirt.api.metamodel.concepts.ListType;
import org.ovirt.api.metamodel.concepts.MemberInvolvementTree;
import org.ovirt.api.metamodel.concepts.Method;
import org.ovirt.api.metamodel.concepts.Model;
import org.ovirt.api.metamodel.concepts.Name;
import org.ovirt.api.metamodel.concepts.NameParser;
import org.ovirt.api.metamodel.concepts.Parameter;
import org.ovirt.api.metamodel.concepts.PrimitiveType;
import org.ovirt.api.metamodel.concepts.Service;
import org.ovirt.api.metamodel.concepts.StructType;
import org.ovirt.api.metamodel.concepts.Type;
import org.ovirt.api.metamodel.server.ValidationException;
import org.ovirt.api.metamodel.tool.util.JaxrsGeneratorUtils;
import org.ovirt.api.metamodel.tool.util.JaxrsHelperGeneratorUtils;

public class JaxrsHelperGenerator extends JavaGenerator {
    // Well known names:
    private static final Name ACTION = NameParser.parseUsingCase("Action");

    // Reference to the object that calculate names:
    @Inject private Names names;
    @Inject private JavaNames javaNames;
    @Inject private JaxrsNames jaxrsNames;
    @Inject private SchemaNames schemaNames;

    // Reference to other utility objects:
    @Inject private JaxrsHelperGeneratorUtils jaxrsHelperGeneratorUtils;
    @Inject private JaxrsGeneratorUtils jaxrsGeneratorUtils;

    //The following are are not properties of the class, they are utility variables.
    private JavaClassName resourceName;
    private JavaClassName helperName;
    private List<Method> serviceMethods;
    private Map<Method, Set<Method>> baseMethods;

    private static final StructType ACTION_TYPE = new StructType();
    static {
        ACTION_TYPE.setName(ACTION);
    }

    @Override
    public void generate(Model model) {
        model.getServices().forEach(this::generateHelper);
    }

    private void generateHelper(Service service) {
        //initialize the utility variables. Once initialized, they
        //are regarded as read-only pieces of information.
        initVariables(service);

        javaBuffer.addImport(ValidationException.class);
        //generate class declaration
        String helperClassName = jaxrsNames.getHelperName(service).getSimpleName();
        javaBuffer.addLine("public class %s {", helperClassName);
        //generate helper code for this method
        serviceMethods.forEach(x -> generateHelperCode(x));
        javaBuffer.addLine("}");
        try {
            javaBuffer.write(outDir);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void initVariables(Service service) {
        resourceName = jaxrsNames.getInterfaceName(service);
        helperName = new JavaClassName(resourceName.getPackageName(),
                resourceName.getSimpleName() + "Helper");
        serviceMethods = service.getDeclaredMethods();
        baseMethods = jaxrsGeneratorUtils.getBaseMethodsMap(serviceMethods);
        javaBuffer = new JavaClassBuffer();
        javaBuffer.setClassName(helperName);
        javaBuffer.addImport(resourceName);
    }

    private void generateHelperCode(Method method) {
        boolean base = baseMethods.containsKey(method);
        Name name = method.getName();
        //for the following cases - no validation required
        //(signatures are handled in the context of the base method).
        if (JaxrsGeneratorUtils.GET.equals(name)
                || JaxrsGeneratorUtils.LIST.equals(name)
                || JaxrsGeneratorUtils.REMOVE.equals(name)
                || jaxrsGeneratorUtils.isAddSignature(method)
                || jaxrsGeneratorUtils.isUpdateSignature(method)
                || jaxrsGeneratorUtils.isActionSignature(method)) {
            return; //do nothing.
        }
        if (JaxrsGeneratorUtils.ADD.equals(name) || JaxrsGeneratorUtils.UPDATE.equals(name)) {
            if (base) {
                generateSignatureDetection(method);
            }
            else {
                generateValidation(method);
            }
        }
        else {//other options exhausted, this must be an action.
            if (base) {
                generateActionSignatureDetection(method);
            }
            else {
                generateActionValidation(method);
            }
        }
    }

    private void generateSignatureDetection(Method method) {
        Set<Method> signatures = baseMethods.get(method);
        if (mandatoryAttributeExists(signatures)) {
            javaBuffer.addImport(java.lang.reflect.Method.class);
            Parameter parameter = jaxrsGeneratorUtils.getMainAddParameter(method);
            Name parameterName = parameter.getName();
            javaBuffer.addImports(schemaNames.getXjcTypeReference(parameter.getType()).getImports());
            Name methodName = getSignatureDetectionMethodName(method);
            javaBuffer.addLine("public static Method %s(%s %s) throws NoSuchMethodException, SecurityException {",
                    javaNames.getJavaMemberStyleName(methodName),
                    javaNames.getJavaClassStyleName(parameter.getType().getName()),
                    javaNames.getJavaMemberStyleName(parameterName));
            generateParameterValidation(parameter);
            CyclicIterator iterator = new CyclicIterator(signatures);
            while (iterator.hasNext()) {
                Method signature = iterator.next();
                if (!jaxrsHelperGeneratorUtils.isContained(signature, signatures)) {
                    handleSignature(method.getName(), signature, parameterName, parameter.getType());
                    iterator.remove();
                }
            }
            closeSignatureDetectionMethod();
        }
        //TODO: in the future fail for this
//        else {
//            throw new ValidationException(method.getName() + "'s signatures have 0 mandatory attributes (any signature must have at least 1 mandatory attribute)");
//        }
    }

    private void handleSignature(Name parentMethodName, Method signature, Name parameterName, Type parameterType) {
        Parameter parameter = signature.getParameter(parameterName);
        Iterator<MemberInvolvementTree> iterator = parameter.getMandatoryAttributes().iterator();
        if (iterator.hasNext()) {//if there are any mandatory attributes
            javaBuffer.addDocComment(signature.getName().toString());
            javaBuffer.addLine("if (" +javaNames.getJavaMemberStyleName(parameter.getName()) + "!=null");
            while (iterator.hasNext()) {
                MemberInvolvementTree attribute = iterator.next();
                List<MemberInvolvementTree> list = stackAttributeComponents(attribute);
                String attributeCheck = getFullAttributeCheck(javaNames.getJavaMemberStyleName(parameter.getName()), list, Operator.AND, true);
                if (attribute.getAlternative()!=null) {//'or' expression
                    list = stackAttributeComponents(attribute.getAlternative());
                    String alternativeCheck = getFullAttributeCheck(javaNames.getJavaMemberStyleName(parameter.getName()), list, Operator.AND, true);
                    String fullCheck = mergeChecks(attributeCheck, alternativeCheck);
                    javaBuffer.addLine(fullCheck);
                } else {
                    javaBuffer.addLine(attributeCheck);
                }
            }
            javaBuffer.addLine(") {");
            javaBuffer.addLine("return " + resourceName.getSimpleName() + ".class.getMethod(\""
                    + javaNames.getJavaMemberStyleName(parentMethodName)
                    + javaNames.getJavaClassStyleName(signature.getName()) + "\", " + javaNames.getJavaClassStyleName(parameter.getType().getName()) + ".class);");
            javaBuffer.addLine("}");
        }
    }

    /**
     * Expected input: two strings of the structure:
     *  && vm.getCluster()!=null && vm.getCluster().getName()!=null
     *  && vm.getCluster()!=null && vm.getCluster().getId()!=null
     * Expected output:
     *  && ((vm.getCluster()!=null && vm.getCluster().getName()!=null) || (vm.getCluster()!=null && vm.getCluster().getId()!=null))
     */
    private String mergeChecks(String check1, String check2) {
        assert check1.startsWith(" && ") && check2.startsWith(" && ");
        StringBuilder builder = new StringBuilder("&& ((");
        builder.append(check1.substring(3)).append(") || (")
        .append(check2.substring(3)).append("))");
        return builder.toString();
    }

    private enum Operator {
        AND("&&", "!="),
        OR("||", "==");
        private String sign;
        private String comaprison;
        private Operator(String operator, String comparison) {
            this.sign = operator;
            this.comaprison = comparison;
        }
        private String getPaddedSign() {
            return new StringBuilder(" ").append(sign).append(" ").toString();
        }
    }

    private String getFullAttributeCheck(String paramName, List<MemberInvolvementTree> list, Operator operator, boolean startWithOperator) {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<list.size(); i++) {
            if (i!=0 || startWithOperator) {
                builder.append(operator.getPaddedSign());
            }
            builder.append(paramName).append(getAttributePath(list.subList(0, i+1), paramName,  operator));
            if ( (!builder.toString().endsWith(".size() == 0")) && (!builder.toString().endsWith(".size() > 0")) ) {
                builder.append(operator.comaprison).append("null");
            }
        }
        return builder.toString();
    }

    private String getAttributePath(List<MemberInvolvementTree> list, String paramName, Operator operator) {
        StringBuilder attributePath = new StringBuilder();
        for (int i=0; i<list.size(); i++) {
            MemberInvolvementTree current = list.get(i);
            attributePath.append(isOrGet(current.getType())).append(javaNames.getJavaClassStyleName(current.getName())).append("()");
            if (current.isCollection()) {
                Name name = ((ListType)current.getType()).getElementType().getName();
                String getterName = javaNames.getJavaClassStyleName(name) + "s";
                if (i==list.size()-1) { //the last element of the expression is a collection
                    StringBuilder soFar = new StringBuilder(paramName).append(attributePath).append(".get").append(getterName).append("()");
                    if (operator==Operator.AND) {
                        attributePath.append("!=null && ").append(soFar).append("!=null && ");
                        attributePath.append(soFar).append(".size() > 0");
                    }
                    else {
                        attributePath.append("==null || ").append(soFar).append("==null || ");
                        attributePath.append(soFar).append(".size() == 0");
                    }
                }
                else {
                    attributePath.append(".get").append(getterName).append("().get(0)");
                }
            }
        }
        return attributePath.toString();
    }

    /**
     * Builds the path corresponding to the given chain of members using the same names that are used in the XML and
     * JSON representations. For example, if the chain is composed of {@code Vm.cpu} member and then {@code Cpu.name}
     * then the returned string will be {@code cpu.name}.
     */
    private String getSchemaPath(List<MemberInvolvementTree> chain) {
        return chain.stream()
            .map(MemberInvolvementTree::getName)
            .map(schemaNames::getSchemaTagName)
            .collect(joining("."));
    }

    private String isOrGet(Type type) {
        if (type!=null && type.getName()!=null && type.getName().toString().equals("boolean")) {
            return ".is";
        }
        else {
            return ".get";
        }
    }

    private List<MemberInvolvementTree> stackAttributeComponents(MemberInvolvementTree attribute) {
        List<MemberInvolvementTree> list = new LinkedList<>();
        list.add(attribute);
        while (attribute.getParent()!=null) {
            list.add(0, attribute.getParent());
            attribute = attribute.getParent();
        }
        return list;
    }

    private void closeSignatureDetectionMethod() {
        javaBuffer.addLine("");
        javaBuffer.addLine("throw new ValidationException(\"No matching signature found, make sure that mandatory attributes are provided.\");");
        javaBuffer.addLine("}");
        javaBuffer.addLine("");
    }

    private void generateValidation(Method method) {
        if (method.isMandatoryAttributeExists()) {
            generateValidationMethodName(method);
            for (Parameter parameter : method.getParameters()) {
                generateParameterValidation(parameter);
            }
            javaBuffer.addLine("}"); //close validation method
            javaBuffer.addLine("");
        }
    }

    private void generateValidationMethodName(Method method) {
        Parameter parameter = jaxrsGeneratorUtils.getMainAddParameter(method);
        javaBuffer.addImports(schemaNames.getXjcTypeReference(parameter.getType()).getImports());
        javaBuffer.addLine("public static void validate%s(%s %s) {",
                javaNames.getJavaClassStyleName(method.getName()),
                javaNames.getJavaClassStyleName(parameter.getType().getName()),
                javaNames.getJavaMemberStyleName(parameter.getName()));
    }

    private void generateParameterValidation(Parameter parameter) {
        Name parameterName = parameter.getName();
        String argName = javaNames.getJavaMemberStyleName(parameterName);
        String tagName = schemaNames.getSchemaTagName(parameterName);
        List<MemberInvolvementTree> mandatoryAttributes = parameter.getMandatoryAttributes();
        if (parameter.isMandatory() || !mandatoryAttributes.isEmpty()) {
            javaBuffer.addLine("if (%1$s == null) {", argName);
            javaBuffer.addLine(
                "throw new ValidationException(\"Parameter '%1$s' is mandatory but was not provided.\");", tagName);
            javaBuffer.addLine("}");
        }
        for (MemberInvolvementTree attribute : mandatoryAttributes) {
            List<MemberInvolvementTree> attributeComponents = stackAttributeComponents(attribute);
            String attributePath = getSchemaPath(attributeComponents);
            if (attribute.hasAlternative()) { //'OR' scenario
                generateAlternativesValidation(parameterName, attribute, attributeComponents, attributePath);
            }
            else {
                javaBuffer.addLine(
                    "if (%1$s) {",
                    getFullAttributeCheck(argName, attributeComponents, Operator.OR, false)
                );
                //(TODO: replace line below with invocation of CompletenessAssertor)
                String fullAttributePath = convertToModelNotation(argName + "." + attributePath);
                javaBuffer.addLine("throw new ValidationException(\"Parameter '%1$s' is mandatory but was not provided.\");", fullAttributePath);
            }
            javaBuffer.addLine("}");
            javaBuffer.addLine();
        }
    }

    private void generateAlternativesValidation(Name parameterName, MemberInvolvementTree attribute, List<MemberInvolvementTree> attributeComponents, String attributePath) {
        List<MemberInvolvementTree> alternativeAttributeComponents = stackAttributeComponents(attribute.getAlternative());
        String condition1 = "if ( (" + getFullAttributeCheck(javaNames.getJavaMemberStyleName(parameterName), attributeComponents, Operator.OR, false) + ")";
        String condition2 = "(" + getFullAttributeCheck(javaNames.getJavaMemberStyleName(parameterName), alternativeAttributeComponents, Operator.OR, false) + ") ) {";
        javaBuffer.addLine(condition1 + " && " + condition2);
        String fullAttributePath1 = convertToModelNotation(parameterName + "." + attributePath);
        String fullAttributePath2 = convertToModelNotation(parameterName + "." + getSchemaPath(alternativeAttributeComponents));
        javaBuffer.addLine(
            "throw new ValidationException(\"Parameters '%1$s' or '%2$s' are mandatory but both were not provided.\");",
                fullAttributePath1,
                fullAttributePath2
        );
    }

    private void generateActionValidation(Method method) {
        if (method.isMandatoryAttributeExists()) {
            writeActionValidationMethodName(method);
            validateActionNotNull();
            for (Parameter parameter : method.getParameters()) {
                generateActionParameterValidation(parameter);
            }
            javaBuffer.addLine("}"); //close validation method
            javaBuffer.addLine("");
        }
    }

    private void writeActionValidationMethodName(Method method) {
        javaBuffer.addImports(schemaNames.getXjcTypeReference(ACTION_TYPE).getImports());
        javaBuffer.addLine("public static void validate%s(Action action) {",
                javaNames.getJavaClassStyleName(method.getName()));
    }

    private void generateActionParameterValidation(Parameter parameter) {
        Name paramName = parameter.getName();
        String propertyName = javaNames.getJavaPropertyStyleName(paramName);
        String tagName = schemaNames.getSchemaTagName(paramName);
        if (parameter.isMandatory()) {//a simple parameter being mandatory only happens in 'action's.
            javaBuffer.addLine("if (action%1$s%2$s() == null) {", isOrGet(parameter.getType()), propertyName);
            //(TODO: replace line below with invocation of CompletenessAssertor)
            javaBuffer.addLine("throw new ValidationException(\"Parameter '%1$s' is mandatory but was not provided.\");", tagName);
            javaBuffer.addLine("}");
        }
        else {
            for (MemberInvolvementTree attribute : parameter.getMandatoryAttributes()) {
                List<MemberInvolvementTree> attributeComponents = stackAttributeComponents(attribute);
                MemberInvolvementTree component = new MemberInvolvementTree(new Name(parameter.getName()));
                component.setType(parameter.getType());
                attributeComponents.add(0, component);
                String attributePath = getSchemaPath(attributeComponents);
                if (attribute.hasAlternative()) { //'OR' scenario
                    List<MemberInvolvementTree> alternativeAttributeComponents = stackAttributeComponents(attribute.getAlternative());
                    alternativeAttributeComponents.add(0, component);
                    String condition1 = "if ( (" + getFullAttributeCheck(javaNames.getJavaMemberStyleName(ACTION), attributeComponents, Operator.OR, false) + ")";
                    String condition2 = "(" + getFullAttributeCheck(javaNames.getJavaMemberStyleName(ACTION), alternativeAttributeComponents, Operator.OR, false) + ") ) {";
                    javaBuffer.addLine(condition1 + " && " + condition2);
                    javaBuffer.addLine(
                        "throw new ValidationException(\"Parameters '%1$s' or '%2$s' are mandatory but both were not provided.\");",
                        convertToModelNotation(attributePath),
                        convertToModelNotation(getSchemaPath(alternativeAttributeComponents))
                     );
                }
                else {
                    javaBuffer.addLine("if (" + getFullAttributeCheck("action", attributeComponents, Operator.OR, false) + ") {");
                    //(TODO: replace line below with invocation of CompletenessAssertor)
                    javaBuffer.addLine(
                        "throw new ValidationException(\"Parameter '%1$s' is mandatory but was not provided.\");",
                        convertToModelNotation(attributePath)
                    );
                }
                javaBuffer.addLine("}");
                javaBuffer.addLine();
            }
        }
    }

    private void generateActionSignatureDetection(Method method) {
        Set<Method> signatures = baseMethods.get(method);
        if (mandatoryAttributeExists(signatures)) {
            //validate that the action object itself is not null
            javaBuffer.addImport(java.lang.reflect.Method.class);
            javaBuffer.addImports(schemaNames.getXjcTypeReference(ACTION_TYPE).getImports());
            Name methodName = getSignatureDetectionMethodName(method);
            javaBuffer.addLine("public static Method %s(Action action) throws NoSuchMethodException, SecurityException {",
                    javaNames.getJavaMemberStyleName(methodName));
            validateActionNotNull();
            for (Parameter parameter : method.getParameters()) {
                generateActionParameterValidation(parameter);
            }
            javaBuffer.addLine();
            for (Method signature : signatures) {
                javaBuffer.addDocComment(signature.getName().toString());
                if (signature.isMandatoryAttributeExists()) {
                    javaBuffer.addLine("if (action!=null");
                    for (Parameter parameter : signature.getParameters()) {
                        if (parameter.isMandatory()) {
                            assert parameter.getType() instanceof EnumType || parameter.getType() instanceof PrimitiveType;
                            javaBuffer.addLine(" && %s!=null", "action" + isOrGet(parameter.getType()) + javaNames.getJavaClassStyleName(parameter.getName()) + "()");
                        }
                        else {
                            Iterator<MemberInvolvementTree> iterator = parameter.getMandatoryAttributes().iterator();
                            while (iterator.hasNext()) {
                                // This will contain the potentially multiple checks for alternative attributes:
                                List<String> checks = new ArrayList<>();

                                // Add the first check:
                                MemberInvolvementTree attribute = iterator.next();
                                List<MemberInvolvementTree> list = stackAttributeComponents(attribute);
                                list.add(0, new MemberInvolvementTree(parameter.getName()));
                                String check = getFullAttributeCheck(
                                    javaNames.getJavaMemberStyleName(ACTION),
                                    list,
                                    Operator.AND,
                                    false
                                );
                                checks.add(check);

                                // If there is an alternative then add the second check:
                                MemberInvolvementTree alternative = attribute.getAlternative();
                                if (alternative != null) {
                                    list = stackAttributeComponents(alternative);
                                    list.add(0, new MemberInvolvementTree(parameter.getName()));
                                    check = getFullAttributeCheck(
                                        javaNames.getJavaMemberStyleName(ACTION),
                                        list,
                                        Operator.AND,
                                        false
                                    );
                                    checks.add(check);
                                }

                                // Build the complete check line:
                                StringBuilder line = new StringBuilder();
                                line.append(" && ");
                                if (checks.size() > 1) {
                                    line.append(checks.stream().collect(joining(" || ", "(", ")")));
                                }
                                else {
                                    line.append(checks.get(0));
                                }

                                // Add the line to the file:
                                javaBuffer.addLine(line.toString());
                            }
                        }
                    }
                }
                javaBuffer.addLine(") {");
                javaBuffer.addLine("return " + resourceName.getSimpleName() + ".class.getMethod(\""
                        + javaNames.getJavaMemberStyleName(method.getName())
                        + javaNames.getJavaClassStyleName(signature.getName()) + "\", Action.class);");
                javaBuffer.addLine("}");
                javaBuffer.addLine();
            }
            closeSignatureDetectionMethod();
        }
      //TODO: in the future fail for this
//        else {
//            throw new ValidationException(method.getName() + "'s signatures have 0 mandatory attributes (any signature must have at least 1 mandatory attribute)");
//        }
    }

    private void validateActionNotNull() {
        javaBuffer.addLine("if (action == null) {");
        javaBuffer.addLine("throw new ValidationException(\"Action is mandatory but was not provided.\");");
        javaBuffer.addLine("}");
    }

    private boolean mandatoryAttributeExists(Set<Method> methods) {
        for (Method method : methods) {
            if (method.isMandatoryAttributeExists()) {
                return true;
            }
        }
        return false;
    }

    private Name getSignatureDetectionMethodName(Method method) {
        Name methodName = new Name();
        methodName.addWord("get");
        methodName.addWords(method.getName().getWords());
        methodName.addWord("Signature");
        return methodName;
    }

    /**
     * Iterator cyclicly returning elements of a list until the
     * list is empty. The assumption is that the client of the iterator
     * gradually removes elements from the list.
     */
    private static class CyclicIterator implements Iterator<Method> {
        private Collection<Method> methods;
        private Iterator<Method> iterator;
        public CyclicIterator(Collection<Method> methods) {
            this.methods = methods;
            iterator = methods.iterator();
        }

        @Override
        public boolean hasNext() {
            return !methods.isEmpty();
        }

        @Override
        public Method next() {
            if (!iterator.hasNext()) {
                iterator = methods.iterator();
            }
            return iterator.next();
        }
        @Override
        public void remove() {
            iterator.remove();
        }
    }

    /**
     * If mandatory parameters are missing, an error message is issued.
     * This error message needs to be in the 'model' language (e.g: "vm.disk_attachments is missing"),
     * not in Java (e.g: "vm.getDiskAttachments() is missing"). This method receives the java path
     * to an attribute and converts it to the model path
     */
    private String convertToModelNotation(String attributePath) {
        String modelNotation = attributePath.replaceAll("\\(\\)", "");
        modelNotation = modelNotation.replaceAll("\\.get", "\\.");
        modelNotation = modelNotation.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
        modelNotation = modelNotation.replaceAll("\\._", "\\.");
        return modelNotation;
    }
}
