package Constructor.Enums;

import Project.RefactoringMiner.SideLocation;
import Project.Utils.Component;
import Project.Utils.LocalFileDiff;
import Project.Utils.Metric;

import java.util.List;

public class Utils {
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
    public static String cutString(String str, String start, String end){
        Integer s = str.indexOf(start);
        Integer e = str.indexOf(end);
        return str.substring(s+1, e);
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
    public static String toRoot(String str){
        return str.substring(str.lastIndexOf(".")+1);

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
