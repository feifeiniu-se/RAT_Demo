package Constructor.Visitors;

import Constructor.Enums.CodeBlockType;
import Constructor.Enums.Operator;
import Model.AttributeTime;
import Model.CodeBlock;
import Model.CommitCodeChange;
import Model.MethodTime;
import gr.uom.java.xmi.*;
import gr.uom.java.xmi.decomposition.VariableDeclaration;
import org.eclipse.jdt.core.dom.*;

import java.util.*;
import java.util.stream.Collectors;

public class MethodAndAttributeVisitor {
    private List<CodeBlock> codeBlocks;
    private HashMap<String, CodeBlock> mappings;
    private CommitCodeChange commitCodeChange;
    private ClassVisitor classVisitor;

    public void methodAAttributeVisitor(Map<String, String> javaFileContents, Set<String> repositoryDirectories, List<CodeBlock> codeBlocks, List<CommitCodeChange> codeChange, HashMap<String, CodeBlock> mappings, ClassVisitor classVisitor){
        this.codeBlocks = codeBlocks;
        this.mappings = mappings;
        this.commitCodeChange = codeChange.get(codeChange.size() - 1); //获得当前commit的内容
        this.classVisitor = classVisitor;

        Reader reader= new Reader(javaFileContents, repositoryDirectories);
    }

    private class Reader extends ASTReader {
        public Reader(Map<String, String> javaFileContents, Set<String> repositoryDirectories) {
            super(javaFileContents, repositoryDirectories);
        }

        @Override
        protected Map<BodyDeclaration, VariableDeclarationContainer> processBodyDeclarations(CompilationUnit cu, AbstractTypeDeclaration abstractTypeDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLClass umlClass, UMLJavadoc packageDoc, List<UMLComment> comments) {
            Map<BodyDeclaration, VariableDeclarationContainer> map = new LinkedHashMap();
            List<BodyDeclaration> bodyDeclarations = abstractTypeDeclaration.bodyDeclarations();
            Iterator var11 = bodyDeclarations.iterator();

            while(true) {
                while(var11.hasNext()) {
                    BodyDeclaration bodyDeclaration = (BodyDeclaration)var11.next();
                    if (bodyDeclaration instanceof FieldDeclaration) {
                        FieldDeclaration fieldDeclaration = (FieldDeclaration)bodyDeclaration;
                        List<UMLAttribute> attributes = processFieldDeclaration(cu, fieldDeclaration, umlClass.isInterface(), sourceFile, comments);
                        Iterator var15 = attributes.iterator();

                        while(var15.hasNext()) {
                            UMLAttribute attribute = (UMLAttribute)var15.next();
                            attribute.setClassName(umlClass.getName());
                            umlClass.addAttribute(attribute);
                            attributeVisitor(attribute);
                        }
                    } else if (bodyDeclaration instanceof MethodDeclaration) {
                        MethodDeclaration methodDeclaration = (MethodDeclaration)bodyDeclaration;
                        UMLOperation operation = processMethodDeclaration(cu, methodDeclaration, packageName, umlClass.isInterface(), sourceFile, comments);
                        operation.setClassName(umlClass.getName());
                        umlClass.addOperation(operation);
                        map.put(methodDeclaration, operation);
                        methodVisitor(operation);
                    } else if (bodyDeclaration instanceof Initializer) {
                        Initializer initializer = (Initializer)bodyDeclaration;
                        UMLInitializer umlInitializer = processInitializer(cu, initializer, packageName, false, sourceFile, comments);
                        umlInitializer.setClassName(umlClass.getName());
                        umlClass.addInitializer(umlInitializer);
                        map.put(initializer, umlInitializer);
                    } else if (bodyDeclaration instanceof TypeDeclaration) {
                        TypeDeclaration typeDeclaration = (TypeDeclaration)bodyDeclaration;
                        processTypeDeclaration(cu, typeDeclaration, umlClass.getName(), sourceFile, importedTypes, packageDoc, comments);
                    } else if (bodyDeclaration instanceof EnumDeclaration) {
                        EnumDeclaration enumDeclaration = (EnumDeclaration)bodyDeclaration;
                        processEnumDeclaration(cu, enumDeclaration, umlClass.getName(), sourceFile, importedTypes, packageDoc, comments);
                    }
                }

                return map;
            }
        }

        @Override
        protected UMLAnonymousClass processAnonymousClassDeclaration(CompilationUnit cu, AnonymousClassDeclaration anonymous, String packageName, String binaryName, String codePath, String sourceFile, List<UMLComment> comments, List<UMLImport> importedTypes) {
            List<BodyDeclaration> bodyDeclarations = anonymous.bodyDeclarations();
            LocationInfo locationInfo = generateLocationInfo(cu, sourceFile, anonymous, LocationInfo.CodeElementType.ANONYMOUS_CLASS_DECLARATION);
            UMLAnonymousClass anonymousClass = new UMLAnonymousClass(packageName, binaryName, codePath, locationInfo, importedTypes);
            Iterator var12 = bodyDeclarations.iterator();

            while(true) {
                while(var12.hasNext()) {
                    BodyDeclaration bodyDeclaration = (BodyDeclaration)var12.next();
                    if (bodyDeclaration instanceof FieldDeclaration) {
                        FieldDeclaration fieldDeclaration = (FieldDeclaration)bodyDeclaration;
                        List<UMLAttribute> attributes = processFieldDeclaration(cu, fieldDeclaration, false, sourceFile, comments);
                        Iterator var16 = attributes.iterator();

                        while(var16.hasNext()) {
                            UMLAttribute attribute = (UMLAttribute)var16.next();
                            attribute.setClassName(anonymousClass.getCodePath());
                            attribute.setAnonymousClassContainer(anonymousClass);
                            anonymousClass.addAttribute(attribute);
                        }
                    } else if (bodyDeclaration instanceof MethodDeclaration) {
                        MethodDeclaration methodDeclaration = (MethodDeclaration)bodyDeclaration;
                        UMLOperation operation = processMethodDeclaration(cu, methodDeclaration, packageName, false, sourceFile, comments);
                        operation.setClassName(anonymousClass.getCodePath());
                        operation.setAnonymousClassContainer(anonymousClass);
                        anonymousClass.addOperation(operation);
                    } else if (bodyDeclaration instanceof Initializer) {
                        Initializer initializer = (Initializer)bodyDeclaration;
                        UMLInitializer umlInitializer = processInitializer(cu, initializer, packageName, false, sourceFile, comments);
                        umlInitializer.setClassName(anonymousClass.getCodePath());
                        umlInitializer.setAnonymousClassContainer(anonymousClass);
                        anonymousClass.addInitializer(umlInitializer);
                    }
                }

                distributeComments(comments, locationInfo, anonymousClass.getComments());
                return anonymousClass;
            }
        }
    }

    private void methodVisitor(UMLOperation umlOperation) {
        String signature = umlOperation.getClassName();

        //根据operation生成方法的名称与方法参数类型列表
        StringBuilder sb = new StringBuilder();
        StringBuilder parameterTypes = new StringBuilder();

        UMLParameter returnParameter = umlOperation.getReturnParameter();
        if (returnParameter != null) {
            sb.append(returnParameter).append("_");
        }

        sb.append(umlOperation.getName());

        List<UMLParameter> parameters = new ArrayList(umlOperation.getParameters());
        parameters.remove(returnParameter);
        sb.append("(");

        for(int i = 0; i < parameters.size(); ++i) {
            UMLParameter parameter = parameters.get(i);
            if (parameter.getKind().equals("in")) {
                String parameterStr = parameter.toString();
                parameterTypes.append(parameterStr.substring(parameterStr.indexOf(" ")+1));
                if (i < parameters.size() - 1) {
                    parameterTypes.append(", ");
                }
            }
        }

        sb.append(parameterTypes);
        sb.append(")");

        String methodName = sb.toString();
        String signature_method = signature + ":" + methodName;

        if(signature_method.contains("testDifferentVersionWarning")){
            System.out.println(114514);
            System.out.println(signature_method);
        }

        //处理完毕，生成CodeBlock和CodeBlockTime
        if (!mappings.containsKey(signature_method)) {
            CodeBlock codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Method);
            mappings.put(signature_method, codeBlock);
            codeBlocks.add(codeBlock);
            CodeBlock classBlock = mappings.get(signature);
            if(classBlock == null){
                System.out.println();
                System.out.println(signature);
                classVisitor.processResidualClass(signature);
                classBlock = mappings.get(signature);
            }
            MethodTime methodTime = new MethodTime(methodName, commitCodeChange, Operator.Add_Method, codeBlock, classBlock, parameterTypes.toString());
        }
    }

    private void attributeVisitor(UMLAttribute umlAttribute){
        String attributeName = umlAttribute.getType() + "_" + umlAttribute.getName();
        String signature = umlAttribute.getClassName();
        String signature_attribute = signature + ":" + attributeName;

        if (!mappings.containsKey(signature_attribute)) {
            CodeBlock codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Attribute);
            mappings.put(signature_attribute, codeBlock);
            codeBlocks.add(codeBlock);
            CodeBlock classBlock = mappings.get(signature);
            if(classBlock == null){
                classVisitor.processResidualClass(signature);
                classBlock = mappings.get(signature);
            }
            AttributeTime attriTime = new AttributeTime(attributeName, commitCodeChange, Operator.Add_Attribute, codeBlock, classBlock);
        }
    }
}
