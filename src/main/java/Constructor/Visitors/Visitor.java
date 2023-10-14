package Constructor.Visitors;

import Constructor.Enums.CodeBlockType;
import Constructor.Enums.OpeTypeEnum;
import Constructor.Enums.Operator;
import Model.*;
import Project.Utils.DiffFile;
import gr.uom.java.xmi.*;
import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class Visitor {
    private List<CodeBlock> codeBlocks;
    private HashMap<String, CodeBlock> mappings;
    private CommitCodeChange commitCodeChange;
    private HashMap<String, CodeBlock> residualMethodMap;
    private Map<String, DiffFile> diffMap;

    public void visit(Map<String, String> javaFileContents, List<CodeBlock> codeBlocks, List<CommitCodeChange> codeChange, HashMap<String, CodeBlock> mappings, Set<String> repositoryDirectories, Map<String, DiffFile> fileList) {
        this.codeBlocks = codeBlocks;
        this.mappings = mappings;
        this.commitCodeChange = codeChange.get(codeChange.size() - 1); //获得当前commit的内容
        residualMethodMap = new HashMap<>();
        this.diffMap = fileList;

        Reader reader= new Reader(javaFileContents, repositoryDirectories);
    }

    private class Reader extends ASTReader {
        public Reader(Map<String, String> javaFileContents, Set<String> repositoryDirectories) {
            super(javaFileContents, repositoryDirectories);
        }

        protected void processCompilationUnit(String sourceFilePath, CompilationUnit compilationUnit, String javaFileContent) {
            PackageDeclaration packageDeclaration = compilationUnit.getPackage();
            String packageName;
            if (packageDeclaration != null) {
                packageName = packageDeclaration.getName().getFullyQualifiedName();
            } else {
                packageName = "";
            }
            CodeBlock codeBlock;
            PackageTime packageTime;
            if(!mappings.containsKey(packageName)){
                //if mappings don't contain package, then create
                codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Package);
                mappings.put(packageName, codeBlock);//更新mapping， codeblocks， commitcodechange
                codeBlocks.add(codeBlock);
                packageTime = new PackageTime(packageName, commitCodeChange, Operator.Add_Package, codeBlock);
            } else {
                codeBlock = mappings.get(packageName);
                packageTime = (PackageTime) codeBlock.getLastHistory().clone();
                commitCodeChange.addCodeChange(packageTime);
                codeBlock.addHistory(packageTime);
            }

            super.processCompilationUnit(sourceFilePath, compilationUnit, javaFileContent);
        }

        @Override
        protected void processEnumDeclaration(CompilationUnit cu, EnumDeclaration enumDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments){
            String className = enumDeclaration.getName().getFullyQualifiedName();
            String signature = packageName.equals("") ? className : packageName + "." + className;

            CodeBlock pkgBlock = mappings.get(packageName);
            String anotherName = packageName;
            if(anotherName.startsWith(".")){
                anotherName = anotherName.substring(1);
            }
            if(pkgBlock == null && residualMethodMap.containsKey(anotherName)){
                mappings.put(packageName, residualMethodMap.get(anotherName));
                pkgBlock = mappings.get(packageName);
            }

            CodeBlock codeBlock;
            ClassTime classTime = null;
            CodeBlockTime oldTime = null;
            int startLine = cu.getLineNumber(enumDeclaration.getStartPosition());
            int endLine = cu.getLineNumber(enumDeclaration.getStartPosition() + enumDeclaration.getLength());

            if (!mappings.containsKey(signature)) {
                codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Class);
                oldTime = codeBlock.getLastHistory();
                mappings.put(signature, codeBlock);
                codeBlocks.add(codeBlock);
                classTime = new ClassTime(className, commitCodeChange, Operator.Add_Class, codeBlock, pkgBlock);//create classTime, add to classBlock, commitTime, update parentBlock
            } else {
                for(int i = startLine; i <= endLine; i++){
                    if(diffMap.containsKey(sourceFile) && diffMap.get(sourceFile).containsChangeLine(i)){
                        codeBlock = mappings.get(signature);
                        oldTime = codeBlock.getLastHistory();
                        classTime = (ClassTime) codeBlock.getLastHistory().clone();
                        commitCodeChange.addCodeChange(classTime);
                        codeBlock.addHistory(classTime);
                        break;
                    }
                }

            }

            if(classTime != null){
                classTime.setNewStartLineNum(startLine);
                classTime.setNewEndLineNum(endLine);

                if(oldTime != null){
                    classTime.setOldStartLineNum(oldTime.getNewStartLineNum());
                    classTime.setOldEndLineNum(oldTime.getNewEndLineNum());
                }
            }


            super.processEnumDeclaration(cu, enumDeclaration, packageName, sourceFile, importedTypes, packageDoc, comments);

        }

        @Override
        protected void processTypeDeclaration(CompilationUnit cu, TypeDeclaration typeDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments){
            String className = typeDeclaration.getName().getFullyQualifiedName();
            String signature = packageName.equals("") ? className : packageName + "." + className;

            if(signature.contains("Main")){
                System.out.println(114514);
            }

            CodeBlock pkgBlock = mappings.get(packageName);
            String anotherName = packageName;
            if(anotherName.startsWith(".")){
                anotherName = anotherName.substring(1);
            }
            if(pkgBlock == null && residualMethodMap.containsKey(anotherName)){
                mappings.put(packageName, residualMethodMap.get(anotherName));
                pkgBlock = mappings.get(packageName);
            }

            CodeBlock codeBlock;
            ClassTime classTime = null;
            CodeBlockTime oldTime = null;
            int startLine = cu.getLineNumber(typeDeclaration.getStartPosition());
            int endLine = cu.getLineNumber(typeDeclaration.getStartPosition() + typeDeclaration.getLength());

            if (!mappings.containsKey(signature)) {
                codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Class);
                oldTime = codeBlock.getLastHistory();
                mappings.put(signature, codeBlock);
                codeBlocks.add(codeBlock);
                classTime = new ClassTime(className, commitCodeChange, Operator.Add_Class, codeBlock, pkgBlock);//create classTime, add to classBlock, commitTime, update parentBlock
            } else {
                for(int i = startLine; i <= endLine; i++){
                    if(diffMap.containsKey(sourceFile) && diffMap.get(sourceFile).containsChangeLine(i)){
                        codeBlock = mappings.get(signature);
                        oldTime = codeBlock.getLastHistory();
                        classTime = (ClassTime) codeBlock.getLastHistory().clone();
                        commitCodeChange.addCodeChange(classTime);
                        codeBlock.addHistory(classTime);
                        break;
                    }
                }

            }

            if(classTime != null){
                classTime.setNewStartLineNum(startLine);
                classTime.setNewEndLineNum(endLine);

                if(oldTime != null){
                    classTime.setOldStartLineNum(oldTime.getNewStartLineNum());
                    classTime.setOldEndLineNum(oldTime.getNewEndLineNum());
                }
            }


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
                            attributeVisitor(cu, fieldDeclaration, attribute, index, sourceFile);
                            index++;
                        }
                    } else if (bodyDeclaration instanceof MethodDeclaration) {
                        MethodDeclaration methodDeclaration = (MethodDeclaration)bodyDeclaration;
                        UMLOperation operation = processMethodDeclaration(cu, methodDeclaration, packageName, umlClass.isInterface(), sourceFile, comments);
                        operation.setClassName(umlClass.getName());
                        umlClass.addOperation(operation);
                        map.put(methodDeclaration, operation);
                        methodVisitor(cu, methodDeclaration, operation, sourceFile);
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

    private void methodVisitor(CompilationUnit cu, MethodDeclaration md, UMLOperation umlOperation, String sourceFile) {
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

        //处理完毕，生成CodeBlock和CodeBlockTime
        CodeBlock codeBlock;
        MethodTime methodTime = null;
        CodeBlock classBlock = mappings.get(signature);
        CodeBlockTime oldTime = null;
        int startLine = cu.getLineNumber(md.getStartPosition());
        int endLine = cu.getLineNumber(md.getStartPosition() + md.getLength());

        if (!mappings.containsKey(signature_method)) {
            codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Method);
            oldTime = codeBlock.getLastHistory();
            mappings.put(signature_method, codeBlock);
            if(!residualMethodMap.containsKey(signature + "." + umlOperation.getName())){
                residualMethodMap.put(signature + "." + umlOperation.getName(), codeBlock);
            }
            codeBlocks.add(codeBlock);
            methodTime = new MethodTime(methodName, commitCodeChange, Operator.Add_Method, codeBlock, classBlock, parameterTypes.toString());
        } else {
            for(int i = startLine; i <= endLine; i++) {
                if (diffMap.containsKey(sourceFile) && diffMap.get(sourceFile).containsChangeLine(i)) {
                    codeBlock = mappings.get(signature_method);
                    oldTime = codeBlock.getLastHistory();
                    methodTime = (MethodTime) codeBlock.getLastHistory().clone();
                    commitCodeChange.addCodeChange(methodTime);
                    codeBlock.addHistory(methodTime);
                    break;
                }
            }

        }

        if (methodTime != null){
            methodTime.setNewStartLineNum(startLine);
            methodTime.setNewEndLineNum(endLine);

            if(oldTime != null){
                methodTime.setOldStartLineNum(oldTime.getNewStartLineNum());
                methodTime.setOldEndLineNum(oldTime.getNewEndLineNum());
            }
        }



    }

    private void attributeVisitor(CompilationUnit cu, FieldDeclaration fd, UMLAttribute umlAttribute, int index, String sourceFile){
        String attributeName = umlAttribute.getType() + "_" + umlAttribute.getName();
        String signature = umlAttribute.getClassName();
        String signature_attribute = signature + ":" + attributeName;

        CodeBlock codeBlock;
        AttributeTime attriTime = null;
        CodeBlock classBlock = mappings.get(signature);
        CodeBlockTime oldTime = null;
        int startLine = cu.getLineNumber(fd.getStartPosition());
        int endLine = cu.getLineNumber(fd.getStartPosition() + fd.getLength());

        if (!mappings.containsKey(signature_attribute)) {
            codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Attribute);
            oldTime = codeBlock.getLastHistory();
            mappings.put(signature_attribute, codeBlock);
            codeBlocks.add(codeBlock);
            attriTime = new AttributeTime(attributeName, commitCodeChange, Operator.Add_Attribute, codeBlock, classBlock);
        } else {
            for(int i = startLine; i <= endLine; i++) {
                if (diffMap.containsKey(sourceFile) && diffMap.get(sourceFile).containsChangeLine(i)) {
                    codeBlock = mappings.get(signature_attribute);
                    oldTime = codeBlock.getLastHistory();
                    attriTime = (AttributeTime) codeBlock.getLastHistory().clone();
                    commitCodeChange.addCodeChange(attriTime);
                    codeBlock.addHistory(attriTime);
                    break;
                }
            }
        }


        if(attriTime != null){
            attriTime.setNewStartLineNum(startLine);
            attriTime.setNewEndLineNum(endLine);


            if(oldTime != null){
                attriTime.setOldStartLineNum(oldTime.getNewStartLineNum());
                attriTime.setOldEndLineNum(oldTime.getNewEndLineNum());
            }
        }
    }

}
