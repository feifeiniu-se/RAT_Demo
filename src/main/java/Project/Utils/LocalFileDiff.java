package Project.Utils;

import Constructor.Enums.OpeTypeEnum;
import Constructor.Visitors.DiffVisitor;
import lombok.Data;
import org.eclipse.jgit.diff.DiffEntry;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

@Data
public class LocalFileDiff {
    String sha;
    String filePath;
    String oldFilePath;
    String content;
    String oldContent;
    List<Component> components;
    List<Component> oldComponents;
    /*
    *   ADD,
        MODIFY,
        DELETE,
        RENAME,
        COPY;
    * */
    String status;
    String patch;
    List<Integer> codeChangeLineNum;
    HashMap<Integer, OpeTypeEnum> codeChangeLineLabel;
    List<Integer> oldCodeChangeLineNum;
    HashMap<Integer, OpeTypeEnum> oldCodeChangeLineLabel;

    public LocalFileDiff(DiffEntry entry){
        this.filePath = entry.getNewPath();
        this.oldFilePath = entry.getOldPath();
        this.status = entry.getChangeType().toString();
        this.components = new ArrayList<>();
        this.oldComponents = new ArrayList<>();
        this.codeChangeLineLabel = new HashMap<Integer, OpeTypeEnum>();
        this.oldCodeChangeLineLabel = new HashMap<Integer, OpeTypeEnum>();
    }

    public LocalFileDiff(String filePath, String content, String hashCode){
        this.filePath = filePath;
        this.content = content;
        this.sha = hashCode;
        this.status = "ADD";
        this.components = new ArrayList<>();
        this.codeChangeLineLabel = new HashMap<Integer, OpeTypeEnum>();
    }


    public HashMap<String, String> labelCode(){
        HashMap<String, String> map = new HashMap<>();
        String add = "";
        String del = "";
        String ref_a = "";
        String ref_d = "";
        String mov_a = "";
        String mov_d = "";
        String add_all = "";
        String del_all = "";


        Pattern annotationLinePattern = Pattern.compile("((//)|(/\\*+)|((^\\s)*\\*)|((^\\s)*\\*+/))+",
                Pattern.MULTILINE + Pattern.DOTALL);	// comment

        Pattern blankLinePattern = Pattern.compile("^\\s*$");	// tab space

        Pattern codeLinePattern = Pattern.compile("(?!import|package).+;\\s*(((//)|(/\\*+)).*)*",
                Pattern.MULTILINE + Pattern.DOTALL); // code line

        if(!codeChangeLineNum.isEmpty()){

            String[] lines = content.split("\n", -1);//split space after string https://blog.csdn.net/BrotherDong90/article/details/45168405

            for(int n: codeChangeLineNum){
                OpeTypeEnum type = codeChangeLineLabel.get(n);
                assert codeChangeLineLabel.containsKey(n);
                if(n>lines.length){
                    n=n-1;
                }
                String line = lines[n-1];

                if(blankLinePattern.matcher(line).find()||annotationLinePattern.matcher(line).find()){
                    if(blankLinePattern.matcher(line).find()){
                        if(!line.trim().isEmpty()){
                            System.out.println("1111: " + line);
                        }
                    }
                    continue;
                }
                add_all+=line;

                switch (type){
                   case A:
                       add+=line;
                       break;
                    case R:
                        ref_a+=line;
                        break;
                    case M:
                        mov_a+=line;
                        break;
                }
            }

        }
        if(!oldCodeChangeLineNum.isEmpty()){
            String[] oldLines = oldContent.split("\n", -1);
            for(int n: oldCodeChangeLineNum){
                OpeTypeEnum type = oldCodeChangeLineLabel.get(n);
                assert oldCodeChangeLineLabel.containsKey(n);
                if(n>oldLines.length){
                    n=n-1;
                }
                String line = oldLines[n-1];
                if(blankLinePattern.matcher(line).find()||annotationLinePattern.matcher(line).find()){
                    continue;
                }
                del_all+=line;

                switch (type){
                    case D:
                        del+=line;
                        break;
                    case R:
                        ref_d+=line;
                        break;
                    case M:
                        mov_d+=line;
                        break;
                }
            }
        }
        map.put("ADD",add);
        map.put("DEL", del);
        map.put("REF_A", ref_a);
        map.put("REF_D", ref_d);
        map.put("MOV_A", mov_a);
        map.put("MOV_D", mov_d);
        map.put("ADD_ALL", add_all);
        map.put("DEL_ALL", del_all);
        return map;
    }
    public void patchParser(){
        this.oldCodeChangeLineNum = getLineNum('-');
        this.codeChangeLineNum = getLineNum('+');
    }
    public void fileParser(){
        if (content != null) {
            this.components = getParserComponents(filePath, content);
            if(this.components==null){
                return;
            }
            for(Component c: this.components){
                c.setFilePath(this.filePath);
            }
        }
        if(oldContent!=null){
            this.oldComponents = getParserComponents(oldFilePath, oldContent);
            if(this.oldComponents==null){
                return;
            }
            for(Component c: this.oldComponents){
                c.setFilePath(this.oldFilePath);
            }
        }
    }
    private List<Component> getParserComponents(String filePath, String fileContent){

        if(fileContent == null){
            return null;
        }
        DiffVisitor diffVisitor = new DiffVisitor();
        diffVisitor.visit(filePath, fileContent);
        return diffVisitor.getComponents();
    }

    public Component findOldComponent(String name){
        if(this.oldComponents==null){
            return null;
        }
        for(Component c: this.oldComponents){
            if(c.getName().equals(name)){
                return c;
            }
        }
        System.out.println("error");
        System.out.println(name);
        return null;
    }
    public Component findComponent(String name){
        if(this.components==null){
            return null;
        }
        for(Component c:this.components){
            if(c.getName().equals(name)){
                return c;
            }
        }
        System.out.println("error");
        System.out.println(name);
        return null;
    }

    public void label(Metric metric){
        for(Integer i: codeChangeLineNum){
            if(!codeChangeLineLabel.containsKey(i)){
                codeChangeLineLabel.put(i, OpeTypeEnum.A);
            }
        }
        for(Integer i: oldCodeChangeLineNum){
            if(!oldCodeChangeLineLabel.containsKey(i)){
                oldCodeChangeLineLabel.put(i,OpeTypeEnum.D);
            }
        }
        label_(metric, components, codeChangeLineNum, codeChangeLineLabel, '+');
        label_(metric, oldComponents, oldCodeChangeLineNum, oldCodeChangeLineLabel, '-');
    }

    private void label_(Metric metric, List<Component> cmps, List<Integer> nums, HashMap<Integer, OpeTypeEnum> lineLabel, char flag) {
        for(Integer i: nums){
            OpeTypeEnum type = lineLabel.get(i);

            lineSwitch(metric, type);
            if(cmps==null){
                continue;
            }
            for(Component c: cmps){
                if (c.contains(i)){
                    c.changedLines.put(i, type);
                }
            }
        }

        if(cmps==null){
            return;
        }
        for(Component c: cmps.stream().filter((Component cm)->cm.changedLines.size()>0).collect(Collectors.toList())){
            switch(c.type){
                case C:
                    classSwitch(metric, c);
                    break;
                case M:
                    methodSwitch(metric, c);
                    break;
                case A:
                    attributeSwitch(metric, c);
                    break;
            }
        }
    }

    private OpeTypeEnum componentLable(Component c){
        HashMap<OpeTypeEnum, Integer> res = new HashMap<>();
        res.put(OpeTypeEnum.A, 0);
        res.put(OpeTypeEnum.D, 0);
        res.put(OpeTypeEnum.M, 0);
        res.put(OpeTypeEnum.R, 0);
        res.put(OpeTypeEnum.E, 0);
        for(Map.Entry<Integer, OpeTypeEnum> entry:c.getChangedLines().entrySet()){
            OpeTypeEnum type = entry.getValue();
            res.put(type, res.get(type)+1);
        }
        int len = c.endLineNum - c.startLineNum;

        if(res.get(OpeTypeEnum.M)>=len || res.get(OpeTypeEnum.M)==c.getChangedLines().size()){
            return OpeTypeEnum.M;
        }else if(res.get(OpeTypeEnum.R)>=len || res.get(OpeTypeEnum.R)==c.getChangedLines().size()){
            return OpeTypeEnum.R;
        }else if((res.get(OpeTypeEnum.R)+res.get(OpeTypeEnum.M))>=c.getChangedLines().size()){
            return OpeTypeEnum.R;
        }else if(res.get(OpeTypeEnum.A)>=len){
            return OpeTypeEnum.A;
        }else if(res.get(OpeTypeEnum.D)>=len){
            return OpeTypeEnum.D;
        }else{
            return OpeTypeEnum.E;
        }
    }
    private void attributeSwitch(Metric metric, Component c){
        OpeTypeEnum type = componentLable(c);
        switch(type){
            case A:
                metric.A_A.add(c);
                break;
            case D:
                metric.A_D.add(c);
                break;
            case M:
                metric.A_M.add(c);
                break;
            case R:
                metric.A_R.add(c);
                break;
        }

    }
    private void methodSwitch(Metric metric, Component c) {
        OpeTypeEnum type = componentLable(c);
        switch(type){
            case A:
                metric.M_A.add(c);
                break;
            case D:
                metric.M_D.add(c);
                break;
            case M:
                metric.M_M.add(c);
                break;
            case R:
                metric.M_R.add(c);
                break;
            case E:
                metric.M_E.add(c);
                break;
        }
    }

    private void classSwitch(Metric metric, Component c) {
        OpeTypeEnum type = componentLable(c);
        switch (type){
            case A:
                metric.C_A.add(c);
                break;
            case D:
                metric.C_D.add(c);
                break;
            case M:
                metric.C_M.add(c);
            case R:
                metric.C_R.add(c);
                break;
            case E:
                metric.C_E.add(c);
                break;
        }

    }

    private void lineSwitch(Metric metric, OpeTypeEnum type){
        switch(type){
            case A:
                metric.aCount += 1;
                break;
            case D:
                metric.dCount += 1;
                break;
            case M:
                metric.mCount += 1;
                break;
            case R:
                metric.rCount += 1;
                break;
        }
    }
    private List<Integer> getLineNum(char separator) {
        List<Integer> resultLines = new ArrayList<Integer>(); // line number of deleted lines (the left line number of commit)<d1, d2, d3>
        patch = patch.replace("* @@", "");// this is for some special cases in bcel
        patch = patch.replace("#@@", "");// this is for codec
        String[] blocks = patch.split("@@"); //split the patch into blocks according to @@
        for (int i = 0; i < (blocks.length - 1) / 2; i++) { //different blocks of code change
            Integer startLine = 0;
            Integer currentLine;
            String firstLine = blocks[i * 2 + 1]; //numbers
            String[] lines = blocks[i * 2 + 2].split("\\n"); // code changes, split change block into lines
            String[] numbers = firstLine.split("\\s+");
            for (String n : numbers) {
                if (n.startsWith(Character.toString(separator))) {
                    startLine = Integer.valueOf(n.split(",")[0]);
                }
            } //obtain the number of start line and total lines

            currentLine = abs(startLine);
            for (int j = 1; j < lines.length; j++) {
                if (!lines[j].startsWith("-") && !lines[j].startsWith("+")) {
                    currentLine = currentLine + 1;
                } else if (lines[j].startsWith(Character.toString(separator))) {
                    resultLines.add(currentLine.intValue());
                    currentLine = currentLine + 1;
                }
            }
        }
        return resultLines;
    }

}
