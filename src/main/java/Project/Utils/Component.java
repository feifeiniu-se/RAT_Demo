package Project.Utils;

import Constructor.Enums.CompTypeEnum;
import Constructor.Enums.OpeTypeEnum;
import lombok.Data;

import java.util.HashMap;


@Data
public class Component {
    String name;
    String filePath;
    CompTypeEnum type; // CLASS, METHOD, ATTRIBUTE, VARIABLE, PARAMETER
    int startLineNum;
    int endLineNum;
    int declarationLineNum;
    HashMap<Integer, OpeTypeEnum> changedLines;
    public Component(String name, CompTypeEnum type, int startLineNum, int endLineNum, int declarationLineNum){
        this.name = name;
        //todo filePath
        this.type = type;
        this.startLineNum = startLineNum;
        this.endLineNum = endLineNum;
        this.changedLines = new HashMap<>();
        this.declarationLineNum = declarationLineNum;
    }
    public Boolean contains(Integer i){
        return i>=startLineNum&&i<=endLineNum;
    }
}
