package Constructor.Visitors;

import Constructor.Enums.Operator;
import Model.CodeBlock;
import Model.CommitCodeChange;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageVisitor {
    private List<CodeBlock> codeBlocks;
    private HashMap<String, CodeBlock> mappings;
    private CommitCodeChange commitTime;

    public void packageVisitor(Map<String, String> javaFileContents, List<CodeBlock> codeBlocks, List<CommitCodeChange> codeChange, HashMap<String, CodeBlock> mappings) {
        this.codeBlocks = codeBlocks;
        this.mappings = mappings;
        this.commitTime = codeChange.get(codeChange.size()-1); //获得当前commit的内容

        Reader reader= new Reader(javaFileContents);
    }

    private class Reader extends ASTReader{
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
            if(!mappings.containsKey(packageName)){
                //if mappings don't contain package, then create
                Operator.Add_Package.apply(codeBlocks, mappings, null, commitTime, packageName);
            }
            super.processCompilationUnit(sourceFilePath, compilationUnit, javaFileContent);
        }
    }
}
