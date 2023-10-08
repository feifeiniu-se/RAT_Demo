package Project.Utils;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Data
// our defined metric
public class Metric {
    public Set<String> typeCount = new HashSet<>();//number of types
    public int refactCount = 0;//number of refactorings
    public int aCount = 0;
    public int dCount = 0;
    public int mCount = 0;
    public int rCount = 0;

//    Code structure
//    C Class
//    M Method
//    A Attribute

//    Operation
//    M Move
//    R Rename
//    A Add
//    D Delete
//    E Edit


    //todo define types of metrics
    public Set<Component> C_M = new HashSet<>();
    public Set<Component> C_R = new HashSet<>();
    public Set<Component> C_A = new HashSet<>();
    public Set<Component> C_D = new HashSet<>();
    public Set<Component> C_E = new HashSet<>();
    public Set<Component> M_M = new HashSet<>();
    public Set<Component> M_R = new HashSet<>();
    public Set<Component> M_A = new HashSet<>();
    public Set<Component> M_D = new HashSet<>();
    public Set<Component> M_E = new HashSet<>();
    public Set<Component> A_M = new HashSet<>();
    public Set<Component> A_R = new HashSet<>();
    public Set<Component> A_A = new HashSet<>();
    public Set<Component> A_D = new HashSet<>();

    public HashMap<Component, Component> renamePairs = new HashMap<>();
    public String count(){
        String res = "";

        res = res + this.refactCount + " " + this.typeCount.size() + " ; " + this.aCount + " " + this.dCount + " " + this.mCount + " " + this.rCount + " ; " +
                this.A_A.size() + " " + this.A_D.size() + " " + this.A_R.size() + " " + this.A_M.size() + " ; " +
                this.C_A.size() + " " + this.C_D.size() + " " + this.C_R.size() + " " + this.C_M.size() + " " + this.C_E.size() + " ; " +
                    this.M_A.size() + " " + this.M_D.size() + " " + this.M_R.size() + " " + this.M_M.size() + " " + this.M_E.size();
        return res;
    }

}
