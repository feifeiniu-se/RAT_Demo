package Constructor.Visitors;

import Constructor.Enums.CodeBlockType;
import Constructor.Enums.OpeTypeEnum;
import Constructor.Enums.Operator;
import Model.*;
import Project.Utils.LocalDiffFile;
import gr.uom.java.xmi.*;
import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class Visitor {
    private LocalDiffFile currentLocalDiff;
    private HashMap<String, LocalDiffFile> diffMap;
    private List<CodeBlock> codeBlocks;
    private HashMap<String, CodeBlock> mappings;
    private CommitCodeChange commitCodeChange;

    public void visit(Map<String, String> javaFileContents, List<CodeBlock> codeBlocks, List<CommitCodeChange> codeChange, HashMap<String, CodeBlock> mappings, HashMap<String, LocalDiffFile> diffMap) {
        this.codeBlocks = codeBlocks;
        this.mappings = mappings;
        this.commitCodeChange = codeChange.get(codeChange.size() - 1); //获得当前commit的内容
        this.diffMap = diffMap;

        currentLocalDiff = null;
        Reader reader= new Reader(javaFileContents);
    }

    private class Reader extends ASTReader {
        public Reader(Map<String, String> javaFileContents) {
            super(javaFileContents);
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

            if(diffMap.containsKey(signature)){
                currentLocalDiff = diffMap.get(signature);
            }

            CodeBlock pkgBlock = mappings.get(packageName);
            CodeBlock codeBlock;
            ClassTime classTime;
            if (!mappings.containsKey(signature)) {
                codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Class);
                mappings.put(signature, codeBlock);
                codeBlocks.add(codeBlock);
                classTime = new ClassTime(className, commitCodeChange, Operator.Add_Class, codeBlock, pkgBlock);//create classTime, add to classBlock, commitTime, update parentBlock

            } else {
                codeBlock = mappings.get(signature);
                classTime = (ClassTime) codeBlock.getLastHistory().clone();
                commitCodeChange.addCodeChange(classTime);
                codeBlock.addHistory(classTime);
            }
            CodeBlockTime oldTime = codeBlock.getLastHistory();

            int startLine = cu.getLineNumber(enumDeclaration.getStartPosition());
            int endLine = cu.getLineNumber(enumDeclaration.getStartPosition() + enumDeclaration.getLength());
            int declarationLine = cu.getLineNumber(enumDeclaration.getName().getStartPosition());
            classTime.setNewLineNum(startLine, endLine, declarationLine);

            if(oldTime != null){
                int oldStartLine = oldTime.getOldStartLineNum();
                int oldEndLine = oldTime.getOldEndLineNum();
                int oldDeclarationLine = oldTime.getOldDeclarationLineNum();
                classTime.setOldLineNum(oldStartLine, oldEndLine, oldDeclarationLine);
            }

            if(currentLocalDiff != null){
                setChangeLineLabel(classTime);
            }

            super.processEnumDeclaration(cu, enumDeclaration, packageName, sourceFile, importedTypes, packageDoc, comments);

            if(diffMap.containsKey(signature)){
                currentLocalDiff = null;
            }
        }

        @Override
        protected void processTypeDeclaration(CompilationUnit cu, TypeDeclaration typeDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments){
            String className = typeDeclaration.getName().getFullyQualifiedName();
            String signature = packageName.equals("") ? className : packageName + "." + className;

            if(diffMap.containsKey(signature)){
                currentLocalDiff = diffMap.get(signature);
            }

            CodeBlock pkgBlock = mappings.get(packageName);
            CodeBlock codeBlock;
            ClassTime classTime;
            if (!mappings.containsKey(signature)) {
                codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Class);
                mappings.put(signature, codeBlock);
                codeBlocks.add(codeBlock);
                classTime = new ClassTime(className, commitCodeChange, Operator.Add_Class, codeBlock, pkgBlock);//create classTime, add to classBlock, commitTime, update parentBlock

            } else {
                codeBlock = mappings.get(signature);
                classTime = (ClassTime) codeBlock.getLastHistory().clone();
                commitCodeChange.addCodeChange(classTime);
                codeBlock.addHistory(classTime);
            }
            CodeBlockTime oldTime = codeBlock.getLastHistory();

            int startLine = cu.getLineNumber(typeDeclaration.getStartPosition());
            int endLine = cu.getLineNumber(typeDeclaration.getStartPosition() + typeDeclaration.getLength());
            int declarationLine = cu.getLineNumber(typeDeclaration.getName().getStartPosition());
            classTime.setNewLineNum(startLine, endLine, declarationLine);

            if(oldTime != null){
                int oldStartLine = oldTime.getOldStartLineNum();
                int oldEndLine = oldTime.getOldEndLineNum();
                int oldDeclarationLine = oldTime.getOldDeclarationLineNum();
                classTime.setOldLineNum(oldStartLine, oldEndLine, oldDeclarationLine);
            }

            if(currentLocalDiff != null){
                setChangeLineLabel(classTime);
            }

            super.processTypeDeclaration(cu, typeDeclaration, packageName, sourceFile, importedTypes, packageDoc, comments);

            if(diffMap.containsKey(signature)){
                currentLocalDiff = null;
            }
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
        MethodTime methodTime;
        CodeBlock classBlock = mappings.get(signature);
        if (!mappings.containsKey(signature)) {
            codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Method);
            mappings.put(signature_method, codeBlock);
            mappings.put(signature + "." + umlOperation.getName(), codeBlock);
            codeBlocks.add(codeBlock);
            methodTime = new MethodTime(methodName, commitCodeChange, Operator.Add_Method, codeBlock, classBlock, parameterTypes.toString());
        } else {
            codeBlock = mappings.get(signature_method);
            methodTime = (MethodTime) codeBlock.getLastHistory().clone();
            commitCodeChange.addCodeChange(methodTime);
            codeBlock.addHistory(methodTime);
        }
        CodeBlockTime oldTime = codeBlock.getLastHistory();


        int startLine = cu.getLineNumber(md.getStartPosition());
        int endLine = cu.getLineNumber(md.getStartPosition() + md.getLength());
        int declarationLine = cu.getLineNumber(md.getName().getStartPosition());

        methodTime.setNewLineNum(startLine, endLine, declarationLine);
        if(oldTime != null){
            int oldStartLine = oldTime.getOldStartLineNum();
            int oldEndLine = oldTime.getOldEndLineNum();
            int oldDeclarationLine = oldTime.getOldDeclarationLineNum();
            methodTime.setOldLineNum(oldStartLine, oldEndLine, oldDeclarationLine);
        }

        if(currentLocalDiff != null){
            setChangeLineLabel(methodTime);
        }
    }

    private void attributeVisitor(CompilationUnit cu, FieldDeclaration fd, UMLAttribute umlAttribute, int index){
        String attributeName = umlAttribute.getType() + "_" + umlAttribute.getName();
        String signature = umlAttribute.getClassName();
        String signature_attribute = signature + ":" + attributeName;

        CodeBlock codeBlock;
        AttributeTime attriTime;
        CodeBlock classBlock = mappings.get(signature);
        if (!mappings.containsKey(signature)) {
            codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Method);
            mappings.put(signature_attribute, codeBlock);
            codeBlocks.add(codeBlock);
            attriTime = new AttributeTime(attributeName, commitCodeChange, Operator.Add_Attribute, codeBlock, classBlock);
        } else {
            codeBlock = mappings.get(signature_attribute);
            attriTime = (AttributeTime) codeBlock.getLastHistory().clone();
            commitCodeChange.addCodeChange(attriTime);
            codeBlock.addHistory(attriTime);
        }
        CodeBlockTime oldTime = codeBlock.getLastHistory();

        int startLine = cu.getLineNumber(fd.getStartPosition());
        int endLine = cu.getLineNumber(fd.getStartPosition() + fd.getLength());
        VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment)fd.fragments().get(index);
        int declarationLine = cu.getLineNumber(variableDeclarationFragment.getStartPosition());

        attriTime.setNewLineNum(startLine, endLine, declarationLine);

        if(oldTime != null){
            int oldStartLine = oldTime.getOldStartLineNum();
            int oldEndLine = oldTime.getOldEndLineNum();
            int oldDeclarationLine = oldTime.getOldDeclarationLineNum();
            attriTime.setOldLineNum(oldStartLine, oldEndLine, oldDeclarationLine);
        }

        if(currentLocalDiff != null){
            setChangeLineLabel(attriTime);
        }
    }

    private void setChangeLineLabel(CodeBlockTime time){
        for(int i = time.getNewStartLineNum(); i <= time.getNewEndLineNum(); i++){
            if(currentLocalDiff.getCodeChangeLineNum().contains(i)){
                time.getNewChangeLines().put(i, OpeTypeEnum.A);
            }
        }
        for(int i = time.getOldStartLineNum(); i <= time.getOldEndLineNum(); i++){
            if(currentLocalDiff.getOldCodeChangeLineNum().contains(i)){
                time.getNewChangeLines().put(i, OpeTypeEnum.D);
            }
        }
    }
}
