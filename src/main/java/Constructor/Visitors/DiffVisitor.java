package Constructor.Visitors;

import Constructor.Enums.CodeBlockType;
import Constructor.Enums.CompTypeEnum;
import Constructor.Enums.Operator;
import Model.*;
import Project.Utils.Component;
import gr.uom.java.xmi.*;
import org.eclipse.jdt.core.dom.*;

import java.io.Reader;
import java.util.*;

public class DiffVisitor {
    List<Component> components;
    public void visit(String filePath, String fileContents) {
        components = new ArrayList<>();
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put(filePath, fileContents);
        Reader reader= new Reader(contentMap);
    }

    public List<Component> getComponents() {
        return components;
    }

    private class Reader extends ASTReader {
        public Reader(Map<String, String> javaFileContents) {
            super(javaFileContents);
        }

        @Override
        protected void processEnumDeclaration(CompilationUnit cu, EnumDeclaration enumDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments){
            String name = enumDeclaration.getName().getFullyQualifiedName();

            int startLine = cu.getLineNumber(enumDeclaration.getStartPosition());
            int endLine = cu.getLineNumber(enumDeclaration.getStartPosition() + enumDeclaration.getLength());
            int declarationLine = cu.getLineNumber(enumDeclaration.getName().getStartPosition());

            Component cmp = new Component(name, CompTypeEnum.C, startLine, endLine, declarationLine);
            components.add(cmp);
            super.processEnumDeclaration(cu, enumDeclaration, packageName, sourceFile, importedTypes, packageDoc, comments);
        }

        @Override
        protected void processTypeDeclaration(CompilationUnit cu, TypeDeclaration typeDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments){
            String name = typeDeclaration.getName().getFullyQualifiedName();

            int startLine = cu.getLineNumber(typeDeclaration.getStartPosition());
            int endLine = cu.getLineNumber(typeDeclaration.getStartPosition() + typeDeclaration.getLength());
            int declarationLine = cu.getLineNumber(typeDeclaration.getName().getStartPosition());

            Component cmp = new Component(name, CompTypeEnum.C, startLine, endLine, declarationLine);
            components.add(cmp);
            super.processTypeDeclaration(cu, typeDeclaration, packageName, sourceFile, importedTypes, packageDoc, comments);
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

                        int index = 0;
                        while(var15.hasNext()) {
                            UMLAttribute attribute = (UMLAttribute)var15.next();
                            attribute.setClassName(umlClass.getName());
                            umlClass.addAttribute(attribute);
                            attributeVisitor(cu, fieldDeclaration, attribute, index);
                            index++;
                        }
                    } else if (bodyDeclaration instanceof MethodDeclaration) {
                        MethodDeclaration methodDeclaration = (MethodDeclaration)bodyDeclaration;
                        UMLOperation operation = processMethodDeclaration(cu, methodDeclaration, packageName, umlClass.isInterface(), sourceFile, comments);
                        operation.setClassName(umlClass.getName());
                        umlClass.addOperation(operation);
                        map.put(methodDeclaration, operation);
                        methodVisitor(cu, methodDeclaration, operation);
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

    }

    private void methodVisitor(CompilationUnit cu, MethodDeclaration md, UMLOperation umlOperation) {
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

        int startLine = cu.getLineNumber(md.getStartPosition());
        int endLine = cu.getLineNumber(md.getStartPosition() + md.getLength());
        int declarationLine = cu.getLineNumber(md.getName().getStartPosition());

        Component cmp = new Component(methodName, CompTypeEnum.M, startLine, endLine, declarationLine);
        components.add(cmp);
    }

    private void attributeVisitor(CompilationUnit cu, FieldDeclaration fd, UMLAttribute umlAttribute, int index){
        String attributeName = umlAttribute.getType() + "_" + umlAttribute.getName();

        int startLine = cu.getLineNumber(fd.getStartPosition());
        int endLine = cu.getLineNumber(fd.getStartPosition() + fd.getLength());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment)fd.fragments().get(index);
        int declarationLine = cu.getLineNumber(variableDeclarationFragment.getStartPosition());

        Component cmp = new Component(attributeName, CompTypeEnum.A, startLine, endLine, declarationLine);
        components.add(cmp);
    }
}
