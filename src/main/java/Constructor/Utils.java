package Constructor;


import Constructor.Enums.CodeBlockType;
import Constructor.Enums.OpeTypeEnum;
import Constructor.Enums.Operator;
import Model.*;
import Project.RefactoringMiner.Refactoring;
import Project.RefactoringMiner.SideLocation;
import Project.Utils.Component;
import Project.Utils.LocalFileDiff;
import Project.Utils.Metric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils {
    public static String cutString(String str, String start, String end){
        Integer s = str.indexOf(start);
        Integer e = str.indexOf(end);
        return str.substring(s+1, e);
    }
    public static String findPackageName(String[] newPkgNames, String path) {
        for(String n: newPkgNames){
            if(path.replace("/", ".").contains(n)){
                return n;
            }
        }
        return null;
    }
    public static String toRoot(String str){
        if (!str.contains("...") && !str.contains("Map.Entry") && !str.contains("Version.Trajectory")){
            str = str.substring(str.lastIndexOf(".")+1);
        }
        str = str.contains(", ")?str.replace(", ", ","):str;
        return str;
    }
    public static String defaultPackage(String classSignature){
//        if(!classSignature.contains(".")){
//            classSignature = "default.package." + classSignature;
//        }
        return classSignature;
    }

    public static boolean isNestedClass(String filePath, String classSignature){
        filePath = filePath.replace("/", ".");
        filePath = filePath.substring(0, filePath.length()-5);
        return !filePath.contains(classSignature);
    }

    public static String sig2Name(String sig){
        return sig.substring(sig.lastIndexOf(".")+1);
    }
    public static String sig2Father(String sig){
        return sig.substring(0, sig.lastIndexOf("."));
    }
    public static void add_fatherClass(String filePath, String sig, HashMap<String, CodeBlock> mappings){
//        String father
//
//        Operator.Add_Class.apply(codeBlocks, mappings, null, commitTime, fatherSig);

    }
    public static String sig2Package(String filePath, String sig){
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.replace("/", ".");
        while (!filePath.contains(sig)){
            sig = sig.substring(0, sig.lastIndexOf("."));
        }
        return sig;
    }
//    private static void moveClass(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Operator type, CommitCodeChange commitTime, String oldName, String newName){
//        //update class.father, class.son, classTime
//        assert mappings.containsKey(oldName);
//        CodeBlock classBlock = mappings.get(oldName);
//        mappings.put(newName, classBlock);
//        ClassTime classTime = (ClassTime) classBlock.getLastHistory().clone();
//
//        //old Father
//        CodeBlock oldFather = classTime.getParentCodeBlock();
//        assert mappings.containsKey(newName.substring(0, newName.lastIndexOf(".")));
//        //new father
//        CodeBlock newFather = mappings.get(newName.substring(0, newName.lastIndexOf(".")));
//
//        //update classTime
//        classTime.setName(newName);
//        classTime.setTime(commitTime);
//        classTime.setRefactorType(type);
//        classTime.setParentCodeBlock(newFather);
//        commitTime.addCodeChange(classTime);
//        classBlock.addHistory(classTime);
//
//        //update old father
//        CodeBlockTime oldFatherTime = (CodeBlockTime) oldFather.getLastHistory().clone();
//        oldFather.addHistory(oldFatherTime);
//        commitTime.addCodeChange(oldFatherTime);
//        oldFatherTime.setTime(commitTime);
//        oldFatherTime.setRefactorType(type);
//        oldFatherTime.getClasses().remove(classBlock);
//
//        //update new father
//        CodeBlockTime newFatherTime = (CodeBlockTime) newFather.getLastHistory().clone();
//        newFather.addHistory(newFatherTime);
//        commitTime.addCodeChange(newFatherTime);
//        newFatherTime.setTime(commitTime);
//        newFatherTime.setRefactorType(type);
//        oldFatherTime.getClasses().add(classBlock);
//
//        //update son //done 递归
//    }
//    public static void renameClass(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Operator type, CommitCodeChange commitTime, String oldName, String newName){
//        //update classTime, class.son
//        assert mappings.containsKey(oldName);
//        CodeBlock classBlock = mappings.get(oldName);
//        mappings.put(newName, classBlock);
//        ClassTime classTime = (ClassTime) classBlock.getLastHistory().clone();
//
//        //update classTime
//        classTime.setName(newName);
//        classTime.setTime(commitTime);
//        classTime.setRefactorType(type);
//        commitTime.addCodeChange(classTime);
//        classBlock.addHistory(classTime);
//
//        //update sons
//        List<CodeBlock> classes = classTime.getClasses();//done 递归
//        List<CodeBlock> methods = classTime.getMethods();
//        List<CodeBlock> attributes = classTime.getAttributes();
//    }

//    public static HashMap<String, String> codeElement2Method(String codeElement) {
//        HashMap<String, String> res = new HashMap<>();
//        String returnType;
//        if(codeElement.lastIndexOf(":")>0){
//            returnType = codeElement.substring(codeElement.lastIndexOf(":")+1).replace(" ", "");
//        }else{
//            returnType = "";
//        }
//        res.put("RT", returnType);
//
//        String[] parameterList = cutString(codeElement, "(", ")").replace(", ", ",").split(",");
//        String name = codeElement.substring(codeElement.indexOf(" ")+1, codeElement.indexOf("("));
//        String parameterTypes = "(";
//        String parameters = "[";
//        if(cutString(codeElement, "(", ")").length()<1){//如果parameter为空
//            parameterTypes = "()";
//            parameters = "[]";
//        }else{
//            for(String p: parameterList){
////                System.out.println(codeElement);
////                System.out.println(parameterList.length);
////                System.out.println(parameterList.toString());
//                parameterTypes = parameterTypes + p.split(" ")[1] + ", ";
//                parameters = parameters + p.split(" ")[1] + " " + p.split(" ")[0] + ", ";
//            }
//            parameterTypes = parameterTypes.substring(0, parameterTypes.length()-2)+")";
//            parameters = parameters.substring(0, parameters.length()-2) + "]";
//        }
//
//        res.put("MN", returnType+"_"+name+parameterTypes);
//        res.put("PA", parameters);
//        res.put("PT", parameterTypes);
//        return res;
//    }

    public static LocalFileDiff findFile(List<LocalFileDiff> diffList, String filePath){
        for(LocalFileDiff f:diffList){
            if(f.getFilePath().equals(filePath)){
                return f;
            }
        }
        System.out.println("error");
        System.out.println(filePath);
        return null;
    }

    public static LocalFileDiff findOldFile(List<LocalFileDiff> diffList, String filePath){
        for(LocalFileDiff f:diffList){
            if(f.getOldFilePath().equals(filePath)){
                return f;
            }
        }
        System.out.println("error");
        System.out.println(filePath);
        return null;
    }

    public static void refactoredLine(Metric metric, List<LocalFileDiff> diffList, List<SideLocation> locations, char flag){
        if(flag=='L'){
            for(SideLocation l:locations){
                LocalFileDiff oldPath = findOldFile(diffList, l.getFilePath());
                for(int i=l.getStartLine(); i<=l.getEndLine(); i++){
                    oldPath.getOldCodeChangeLineLabel().put(i, OpeTypeEnum.R);
                }
            }
        }else if(flag=='R'){
            for(SideLocation l:locations){
                LocalFileDiff newPath = findFile(diffList, l.getFilePath());
                for(int i=l.getStartLine(); i<=l.getEndLine(); i++){
                    newPath.getCodeChangeLineLabel().put(i, OpeTypeEnum.R);
                }
            }
        }
    }
    public static void refactorFirstLine(Metric metric, List<LocalFileDiff> diffList, List<SideLocation> locations, char flag){
        if(flag=='L'){
            for(SideLocation l:locations){
                LocalFileDiff oldPath = findOldFile(diffList, l.getFilePath());
                String name = "";
                Component c;
                if(l.getCodeElementType().equals("METHOD_DECLARATION")){
                    name = l.parseMethodDeclaration().get("MN");
                    c = oldPath.findOldComponent(name);
                    if(c==null){
                        return;
                    }
                    oldPath.getOldCodeChangeLineLabel().put(c.getDeclarationLineNum(), OpeTypeEnum.R);
                }else if(l.getCodeElementType().equals("TYPE_DECLARATION")){
                    name = toRoot(l.getCodeElement());
                    c = oldPath.findOldComponent(name);
                    if(c==null){
                        return;
                    }
                    oldPath.getOldCodeChangeLineLabel().put(c.getDeclarationLineNum(), OpeTypeEnum.R);
                }else if(l.getCodeElementType().equals("FIELD_DECLARATION")){
                    name = l.parseAttributeOrParameter();
                    c = oldPath.findOldComponent(name);
                    if(c==null){
                        return;
                    }
                    oldPath.getOldCodeChangeLineLabel().put(c.getDeclarationLineNum(), OpeTypeEnum.R);
                }
                else{
                    oldPath.getOldCodeChangeLineLabel().put(l.getStartLine(), OpeTypeEnum.R);
                }

            }
        }else if(flag=='R'){
            for(SideLocation l:locations){
                LocalFileDiff newPath = findFile(diffList, l.getFilePath());
                String name = "";
                Component c;
                if(l.getCodeElementType().equals("METHOD_DECLARATION")){
                    name = l.parseMethodDeclaration().get("MN");
                    c = newPath.findComponent(name);
                    if(c==null){
                        return;
                    }
                    newPath.getCodeChangeLineLabel().put(c.getDeclarationLineNum(), OpeTypeEnum.R);
                }else if(l.getCodeElementType().equals("TYPE_DECLARATION")){
                    name = toRoot(l.getCodeElement());
                    c = newPath.findComponent(name);
                    if(c==null){
                        return;
                    }
                    newPath.getCodeChangeLineLabel().put(c.getDeclarationLineNum(), OpeTypeEnum.R);
                }else if(l.getCodeElementType().equals("FIELD_DECLARATION")){
                    name = l.parseAttributeOrParameter();
                    c = newPath.findComponent(name);
                    if(c==null){
                        return;
                    }
                    newPath.getCodeChangeLineLabel().put(c.getDeclarationLineNum(), OpeTypeEnum.R);
                }
                else{
                    newPath.getCodeChangeLineLabel().put(l.getStartLine(), OpeTypeEnum.R);
                }
            }
        }
    }

    public static void refactoredLine(Metric metric, List<LocalFileDiff> diffList, List<SideLocation> locations, char flag, OpeTypeEnum type){
        if(flag=='L'){
            for(SideLocation l:locations){
                LocalFileDiff oldPath = findOldFile(diffList, l.getFilePath());
                for(int i=l.getStartLine(); i<=l.getEndLine(); i++){
                    oldPath.getOldCodeChangeLineLabel().put(i, type);
                }
            }
        }else if(flag=='R'){
            for(SideLocation l:locations){
                LocalFileDiff newPath = findFile(diffList, l.getFilePath());
                for(int i=l.getStartLine(); i<=l.getEndLine(); i++){
                    newPath.getCodeChangeLineLabel().put(i, type);
                }
            }
        }
    }
}
