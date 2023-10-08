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
            List<LocalFileDiff> diffList = project.getLocalDiffFileList(hashCode); //obtain line number of code change
            Metric metric = new Metric();

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
//            Map<String, String> fileContents = diffList.stream()
//                    .filter(p -> p.getStatus().equals("ADD") || p.getStatus().equals("Modify"))
//                    .collect(Collectors.toMap(LocalFileDiff::getFilePath, LocalFileDiff::getContent));

            //packageLevel: firstly refactorings, then javaParser visitor
            System.out.println("--------Package Level--------");
            if (refact != null && commitTime.getPreCommit() != null) {
                if (!refact.getRefactorings().isEmpty()) {
                    List<Refactoring> packageLevelRefactorings = refact.filter("package");
                    if (!packageLevelRefactorings.isEmpty()) {
                        for(Refactoring r: packageLevelRefactorings){
                            Operator.valueOf(r.getType().replace(" ", "_")).apply(codeBlocks, mappings, r, commitTime, null);
                            ComponentOperator.valueOf(r.getType().replace(" ", "_")).apply(metric, diffList, r);
                        }
                    }
                }
            }
            PackageVisitor packageVisitor = new PackageVisitor();
            packageVisitor.packageVisitor(fileContents, codeBlocks, codeChange, mappings);
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
                            ComponentOperator.valueOf(r.getType().replace(" ", "_")).apply(metric, diffList, r);
                        }
                    }
                }
            }
            ClassVisitor classVisitor = new ClassVisitor();
            classVisitor.classVisitor(fileContents, codeBlocks, codeChange, mappings);
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
                            ComponentOperator.valueOf(r.getType().replace(" ", "_")).apply(metric, diffList, r);
                        }
                    }
                    //parameters & return type
                    List<Refactoring> parameterLevelRefactorings = refact.filter("parameter");
                    if (!parameterLevelRefactorings.isEmpty()) {
                        for(Refactoring r: parameterLevelRefactorings){
                            Operator.valueOf(r.getType().replace(" ", "_")).apply(codeBlocks, mappings, r, commitTime, null);
                            ComponentOperator.valueOf(r.getType().replace(" ", "_")).apply(metric, diffList, r);
                        }
                    }

                }
            }
            MethodAndAttributeVisitor methodAndAttributeVisitor = new MethodAndAttributeVisitor();
            methodAndAttributeVisitor.methodAAttributeVisitor(fileContents, codeBlocks, codeChange, mappings, classVisitor);
            classVisitor.processResidualClass();
            updateMappings(mappings, codeBlocks);

            if(refact != null && commitTime.getPreCommit() != null){
                // metric label
                for(LocalFileDiff dfl: diffList){
                    dfl.label(metric);
                }
                project.getMetrics().put(hashCode.getHashCode(), metric.count());
                code.put(hashCode.getHashCode(), calculate(diffList));//obtain code content
                project.getEntropy().put(hashCode.getHashCode(), compute_entropy(diffList));//calculate entropy
            }
        }
    }

    private void updateMappings(HashMap<String, CodeBlock> mappings, List<CodeBlock> codeBlocks) {
        for(CodeBlock codeBlock: codeBlocks){
            mappings.put(codeBlock.getLastHistory().getSignature(), codeBlock);
        }
    }

    public String compute_entropy(List<LocalFileDiff> diffList){
        String res = "";

        List<Integer> methods_new = new ArrayList<>();
        List<Integer> class_new = new ArrayList<>();
        List<Integer> methods_old = new ArrayList<>();
        List<Integer> class_old = new ArrayList<>();
        for(LocalFileDiff dfl: diffList){
            if(dfl.getComponents()!=null){
                for(Component c: dfl.getComponents()){
                    int count = 0;
                    if(c.getType().equals(CompTypeEnum.M)||c.getType().equals(CompTypeEnum.C)){
                        count = 0;
                        for(Map.Entry<Integer, OpeTypeEnum> entry: c.getChangedLines().entrySet()){
                            if(dfl.getCodeChangeLineNum().contains(entry.getKey())){
                                count++;
                            }
                        }if(count==0){
                            continue;
                        }
                        if(c.getType().equals(CompTypeEnum.M)){
                            methods_new.add(count);
                        }else if(c.getType().equals(CompTypeEnum.C)){
                            class_new.add(count);
                        }
                    }
                }
            }

            if(dfl.getOldComponents()!=null){
                for(Component c: dfl.getOldComponents()){
                    int count = 0;
                    if(c.getType().equals(CompTypeEnum.M)||c.getType().equals(CompTypeEnum.C)){
                        count = 0;
                        if(c.getType().equals(CompTypeEnum.M)||c.getType().equals(CompTypeEnum.C)){
                            count = 0;
                            for(Map.Entry<Integer, OpeTypeEnum> entry: c.getChangedLines().entrySet()){
                                if(dfl.getOldCodeChangeLineNum().contains(entry.getKey())){
                                    count++;
                                }
                            }
                            if(count==0){
                                continue;
                            }
                            if(c.getType().equals(CompTypeEnum.M)){
                                methods_old.add(count);
                            }else if(c.getType().equals(CompTypeEnum.C)){
                                class_old.add(count);
                            }
                        }
                    }
                }
            }
        }
        res = res + Double.toString(calculate_entropy(class_new))+";"+Double.toString(calculate_entropy(class_old))+";"+Double.toString(calculate_entropy(methods_new))+";"+Double.toString(calculate_entropy(methods_old));
        return res;
    }
    private double calculate_entropy(List<Integer> counts){
        double entropy  = 0;
        if(counts.size()==0){
            return 0;
        }
        int totalLOCModified = counts.stream().reduce(Integer::sum).orElse(0);
        if(totalLOCModified==0){
            return 0;
        }
        for(Integer c: counts){
            double avg = (double)c/(double)totalLOCModified;
            entropy -= (avg*Math.log(avg)/Math.log(2));
        }
        return entropy;
    }
    public String calculate(List<LocalFileDiff> diffList) {
        String res = "";
        String add = "";
        String delete = "";
        String refact_d = "";
        String refact_a = "";
        String move_a = "";
        String move_d = "";
        String add_all = "";
        String delete_all = "";
//        <operation, code>
        for (LocalFileDiff dfl : diffList) {
            HashMap<String, String> fileCode = dfl.labelCode();
            add = add + fileCode.get("ADD");
            delete = delete + fileCode.get("DEL");
            refact_a = refact_a + fileCode.get("REF_A");
            refact_d = refact_d + fileCode.get("REF_D");
            move_a = move_a + fileCode.get("MOV_A");
            move_d = move_d + fileCode.get("MOV_D");
            add_all = add_all + fileCode.get("ADD_ALL");
            delete_all = delete_all + fileCode.get("DEL_ALL");
        }
        res = res+"[CLS]"+add+"[CLS]"+delete+"[CLS]"+refact_a+"[CLS]"+refact_d+"[CLS]"+move_a+"[CLS]"+move_d+"[CLS]"+add_all+"[CLS]"+delete_all+"[CLS]";
        return res;
    }
}
