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

    int oldStartLineNum;
    int oldEndLineNum;
    int newStartLineNum;
    int newEndLineNum;
    int oldDeclarationLineNum;
    int newDeclarationLineNum;
    HashMap<Integer, OpeTypeEnum> newChangeLines;
    HashMap<Integer, OpeTypeEnum> oldChangeLines;

    public abstract String getSignature();
    public abstract Set<CodeBlock> getPackages();
    public abstract Set<CodeBlock> getClasses();
    public abstract Set<CodeBlock> getMethods();
    public abstract Set<CodeBlock> getAttributes();
    abstract Set<CodeBlock> getParameterRetureType();
    abstract String getParameters();

    CodeBlockTime(){
        oldStartLineNum = -1;
        oldEndLineNum = -1;
        newStartLineNum = -1;
        newEndLineNum = -1;
        oldDeclarationLineNum = -1;
        newDeclarationLineNum = -1;

        newChangeLines = new HashMap<>();
        oldChangeLines = new HashMap<>();
    }

    @Override
    public Object clone() {
        CodeBlockTime codeBlockTime = null;
        try {
            codeBlockTime = (CodeBlockTime) super.clone();
            codeBlockTime.setDeriver(new HashSet<>(deriver));
            codeBlockTime.setDerivee(new HashSet<>(derivee));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return codeBlockTime;
    }

    public void setNewLineNum(int newStartLineNum, int newEndLineNum, int newDeclarationLineNum){
        this.newStartLineNum = newStartLineNum;
        this.newEndLineNum = newEndLineNum;
        this.newDeclarationLineNum = newDeclarationLineNum;
    }

    public void setOldLineNum(int oldStartLineNum, int oldEndLineNum, int oldDeclarationLineNum){
        this.oldStartLineNum = oldStartLineNum;
        this.oldEndLineNum = oldEndLineNum;
        this.oldDeclarationLineNum = oldDeclarationLineNum;
    }
}
