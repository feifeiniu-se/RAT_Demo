package Constructor.Visitors;

import Constructor.Enums.CodeBlockType;
import Constructor.Enums.Operator;
import Model.ClassTime;
import Model.CodeBlock;
import Model.CommitCodeChange;
import gr.uom.java.xmi.UMLComment;
import gr.uom.java.xmi.UMLImport;
import gr.uom.java.xmi.UMLJavadoc;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClassVisitor {
    private List<CodeBlock> codeBlocks;
    private HashMap<String, CodeBlock> mappings;
    private CommitCodeChange commitCodeChange;
    private HashMap<String, String[]> residualClassMap;

    public void classVisitor(Map<String, String> javaFileContents, List<CodeBlock> codeBlocks, List<CommitCodeChange> codeChange, HashMap<String, CodeBlock> mappings) {
        this.codeBlocks = codeBlocks;
        this.mappings = mappings;
        this.commitCodeChange = codeChange.get(codeChange.size() - 1); //获得当前commit的内容
        this.residualClassMap = new HashMap<>();

        Reader reader= new Reader(javaFileContents);
    }


    private class Reader extends ASTReader {
        public Reader(Map<String, String> javaFileContents) {
            super(javaFileContents);
        }

        @Override
        protected void processEnumDeclaration(CompilationUnit cu, EnumDeclaration enumDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments){
            String className = enumDeclaration.getName().getFullyQualifiedName();
            String signature = packageName.equals("") ? className : packageName + "." + className;
            CodeBlock pkgBlock = mappings.get(packageName);
            if(pkgBlock == null){
                residualClassMap.put(signature, new String[]{packageName, className});
            } else if (!mappings.containsKey(signature)) {
                CodeBlock classBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Class);
                mappings.put(signature, classBlock);
                codeBlocks.add(classBlock);
                ClassTime classTime = new ClassTime(className, commitCodeChange, Operator.Add_Class, classBlock, pkgBlock);//create classTime, add to classBlock, commitTime, update parentBlock
            }
            super.processEnumDeclaration(cu, enumDeclaration, packageName, sourceFile, importedTypes, packageDoc, comments);
        }

        @Override
        protected void processTypeDeclaration(CompilationUnit cu, TypeDeclaration typeDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments){
            String className = typeDeclaration.getName().getFullyQualifiedName();
            String signature = packageName.equals("") ? className : packageName + "." + className;
            CodeBlock pkgBlock = mappings.get(packageName);
            if(pkgBlock == null){
                residualClassMap.put(signature, new String[]{packageName, className});
            } else if (!mappings.containsKey(signature)) {
                CodeBlock classBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Class);
                mappings.put(signature, classBlock);
                codeBlocks.add(classBlock);
                ClassTime classTime = new ClassTime(className, commitCodeChange, Operator.Add_Class, classBlock, pkgBlock);//create classTime, add to classBlock, commitTime, update parentBlock
            }
            super.processTypeDeclaration(cu, typeDeclaration, packageName, sourceFile, importedTypes, packageDoc, comments);
        }
    }

    public void processResidualClass(){
        for(Map.Entry<String, String[]> entry : residualClassMap.entrySet()){
            String signature = entry.getKey();
            if(!mappings.containsKey(signature)){
                String parentName = entry.getValue()[0];
                String className = entry.getValue()[1];
                CodeBlock parentBlock = mappings.get(parentName);

                if(parentBlock == null){
                    System.out.println();
                    System.out.println(parentName);
                    System.out.println(className);
                    System.out.println(signature);
                    mappings.keySet().stream().filter(p -> p.contains("testDifferentVersionWarning"))
                            .forEach(System.out::println);
                }
                CodeBlock classBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Class);
                mappings.put(signature, classBlock);
                codeBlocks.add(classBlock);
                ClassTime classTime = new ClassTime(className, commitCodeChange, Operator.Add_Class, classBlock, parentBlock);//create classTime, add to classBlock, commitTime, update parentBlock
            }
        }
    }

    public void processResidualClass(String signature){
        if(!mappings.containsKey(signature)){
            String packageName = residualClassMap.get(signature)[0];
            String className = residualClassMap.get(signature)[1];
            CodeBlock pkgBlock = mappings.get(packageName);
            System.out.println(packageName);
            CodeBlock classBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Class);
            mappings.put(signature, classBlock);
            codeBlocks.add(classBlock);
            ClassTime classTime = new ClassTime(className, commitCodeChange, Operator.Add_Class, classBlock, pkgBlock);//create classTime, add to classBlock, commitTime, update parentBlock
        }
    }
}
