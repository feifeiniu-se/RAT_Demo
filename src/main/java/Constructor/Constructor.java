package Constructor;

import Constructor.Enums.*;
import Constructor.Visitors.*;
import Model.*;
import Project.RefactoringMiner.Refactoring;
import Project.RefactoringMiner.Refactorings;
import Project.Utils.*;
import lombok.Data;
import Project.Project;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Constructor {
    Project project;
    List<CodeBlock> codeBlocks = new ArrayList<>();
    List<CommitCodeChange> codeChange = new ArrayList<>();
    HashMap<String, CodeBlock> mappings = new HashMap<>();// mapping between signature and codeBlockID

    public Constructor(Project p) {
        project = p;
    }


    public void start(){
        List<CommitHashCode> commitList = project.getCommitList();
        HashMap<String, String> code = new HashMap<>();
        for(CommitHashCode hashCode: commitList){
            System.out.println("Commit: "+hashCode.getHashCode());
            //add a new commitTime for each commit, for the code change during this commit
            CommitCodeChange commitTime = new CommitCodeChange(hashCode.getHashCode());
            if(codeChange.size()>0){
                commitTime.setPreCommit(codeChange.get(codeChange.size()-1));
                codeChange.get(codeChange.size()-1).setPostCommit(commitTime);
            }else{
                commitTime.setPreCommit(null);
            }
            codeChange.add(commitTime);

            //generate localFileDiff
            //todo：确认与diffFile的关系
            HashMap<String, LocalDiffFile> diffMap = project.getLocalDiffMap(hashCode);

            //go through all the files and refactorings
            HashMap<String, DiffFile> fileList1 =  project.getDiffList(hashCode);
            if(fileList1==null){continue;}//no file changes during this commit
            Map<String, DiffFile> fileList = fileList1.entrySet().stream()
                    .filter(p -> FileType.ADD.equals(p.getValue().getType())|| FileType.MODIFY.equals(p.getValue().getType()))
                    .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

            //if refactoring is not null, separate them into three levels: package, class, method&attribute
            Refactorings refact = project.getRefactorings().get(hashCode.getHashCode());

            //generate the parameter of ASTReader
            Map<String, String> fileContents = fileList.entrySet().stream()
                    .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue().getContent()));

            //firstly, generate all codeBlock and codeBlockTime
            Visitor visitor = new Visitor();
            visitor.visit(fileContents, codeBlocks, codeChange, mappings, diffMap);

            //refactorings
            System.out.println("--------Package Level--------");
            if (refact != null && commitTime.getPreCommit() != null) {
                if (!refact.getRefactorings().isEmpty()) {
                    List<Refactoring> packageLevelRefactorings = refact.filter("package");
                    if (!packageLevelRefactorings.isEmpty()) {
                        for(Refactoring r: packageLevelRefactorings){
                            Operator.valueOf(r.getType().replace(" ", "_")).apply(codeBlocks, mappings, r, commitTime, null);
                        }
                    }
                }
            }

            updateMappings(mappings, codeBlocks);

            System.out.println("--------Class Level--------");
            //classLevel; firstly refactorings, then javaparser visitor
            if (refact != null && commitTime.getPreCommit() != null) {
                if (!refact.getRefactorings().isEmpty()) {
                    // class level
                    List<Refactoring> classLevelRefactorings = refact.filter("class");
                    if (!classLevelRefactorings.isEmpty()) {
                        for(Refactoring r: classLevelRefactorings){
                            Operator.valueOf(r.getType().replace(" ", "_")).apply(codeBlocks, mappings, r, commitTime, null);
                        }
                    }
                }
            }

            updateMappings(mappings, codeBlocks);
//            //method and attribute level: firstly refactoring, then javaparser visitor
            System.out.println("--------Method Level--------");
            if (refact != null && commitTime.getPreCommit() != null) {
                if (!refact.getRefactorings().isEmpty()) {
                    //method & attribute
                    List<Refactoring> methodAndAttributeLevelRefactorings = refact.filter("methodAndAttribute");
                    if (!methodAndAttributeLevelRefactorings.isEmpty()) {
                        for(Refactoring r: methodAndAttributeLevelRefactorings){
                            Operator.valueOf(r.getType().replace(" ", "_")).apply(codeBlocks, mappings, r, commitTime, null);
                        }
                    }
                    //parameters & return type
                    List<Refactoring> parameterLevelRefactorings = refact.filter("parameter");
                    if (!parameterLevelRefactorings.isEmpty()) {
                        for(Refactoring r: parameterLevelRefactorings){
                            Operator.valueOf(r.getType().replace(" ", "_")).apply(codeBlocks, mappings, r, commitTime, null);
                        }
                    }

                }
            }

            updateMappings(mappings, codeBlocks);
        }
    }

    private void updateMappings(HashMap<String, CodeBlock> mappings, List<CodeBlock> codeBlocks) {
        for(CodeBlock codeBlock: codeBlocks){
            mappings.put(codeBlock.getLastHistory().getSignature(), codeBlock);
        }
    }

}
