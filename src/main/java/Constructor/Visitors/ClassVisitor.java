package Constructor.Visitors;

import Constructor.Enums.CodeBlockType;
import Constructor.Enums.Operator;
import Model.ClassTime;
import Model.CodeBlock;
import Model.CommitCodeChange;
import gr.uom.java.xmi.UMLComment;
import gr.uom.java.xmi.UMLImport;
import gr.uom.java.xmi.UMLJavadoc;
import gr.uom.java.xmi.UMLModelASTReader;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jgit.api.SubmoduleUpdateCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ClassVisitor {
    private List<CodeBlock> codeBlocks;
    private HashMap<String, CodeBlock> mappings;
    private CommitCodeChange commitCodeChange;

    public void classVisitor(Map<String, String> javaFileContents, Set<String> repositoryDirectories, List<CodeBlock> codeBlocks, List<CommitCodeChange> codeChange, HashMap<String, CodeBlock> mappings) {
        this.codeBlocks = codeBlocks;
        this.mappings = mappings;
        this.commitCodeChange = codeChange.get(codeChange.size() - 1); //获得当前commit的内容

        Reader reader= new Reader(javaFileContents, repositoryDirectories);
    }

    private class Reader extends ASTReader {
        public Reader(Map<String, String> javaFileContents, Set<String> repositoryDirectories) {
            super(javaFileContents, repositoryDirectories);
        }

        @Override
        protected void processEnumDeclaration(CompilationUnit cu, EnumDeclaration enumDeclaration, String packageName, String sourceFile, List<UMLImport> importedTypes, UMLJavadoc packageDoc, List<UMLComment> comments){
            String className = enumDeclaration.getName().getFullyQualifiedName();
            String signature = packageName.equals("") ? className : packageName + "." + className;
            CodeBlock pkgBlock = mappings.get(packageName);
            if (!mappings.containsKey(signature)) {
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
            if (!mappings.containsKey(signature)) {
                CodeBlock classBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Class);
                mappings.put(signature, classBlock);
                codeBlocks.add(classBlock);
                ClassTime classTime = new ClassTime(className, commitCodeChange, Operator.Add_Class, classBlock, pkgBlock);//create classTime, add to classBlock, commitTime, update parentBlock
            }
            super.processTypeDeclaration(cu, typeDeclaration, packageName, sourceFile, importedTypes, packageDoc, comments);
        }
    }
}
