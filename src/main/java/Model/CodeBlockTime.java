package Model;

import Constructor.Enums.OpeTypeEnum;
import Constructor.Enums.Operator;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Data
public abstract class CodeBlockTime implements Cloneable, Serializable {
    String name;
    CommitCodeChange time;
    CodeBlockTime pre = null;
    CodeBlockTime post = null;
    Operator refactorType;
    Set<CodeBlockTime> deriver = new HashSet<>();  // 父：两个类合并为一个类
    Set<CodeBlockTime> derivee = new HashSet<>();  // 子
    CodeBlock parentCodeBlock;
    CodeBlock owner;

    int oldStartLineNum = -1;
    int oldEndLineNum = -1;
    int newStartLineNum = -1;
    int newEndLineNum = -1;
    int oldDeclarationLineNum=-1;
    int newDeclarationLineNum=-1;
    HashMap<Integer, OpeTypeEnum> oldChangeLines;
    HashMap<Integer, OpeTypeEnum> newChangeLines;

    public abstract String getSignature();
    public abstract Set<CodeBlock> getPackages();
    public abstract Set<CodeBlock> getClasses();
    public abstract Set<CodeBlock> getMethods();
    public abstract Set<CodeBlock> getAttributes();
    abstract Set<CodeBlock> getParameterRetureType();
    abstract String getParameters();

    @Override
    public Object clone() {
        CodeBlockTime codeBlockTime = null;
        try {
            codeBlockTime = (CodeBlockTime) super.clone();
            codeBlockTime.setDeriver(new HashSet<>(deriver));
            codeBlockTime.setDerivee(new HashSet<>(derivee));
            codeBlockTime.setOldChangeLines(new HashMap<>());
            codeBlockTime.setNewChangeLines(new HashMap<>());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return codeBlockTime;
    }

    public Boolean containsNew(Integer i){
        return i>=newStartLineNum&&i<=newEndLineNum;
    }

    public Boolean containsOld(Integer i){
        return i>=oldStartLineNum&&i<=oldEndLineNum;
    }
}
