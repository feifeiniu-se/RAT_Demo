package Project.Utils;

import Constructor.Enums.OpeTypeEnum;
import lombok.Data;
import org.eclipse.jgit.diff.DiffEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.abs;

@Data
public class LocalDiffFile {
    String sha;
    String oldFilePath;
    String filePath;
    String patch;
    String content;
    String oldContent;
    List<Integer> codeChangeLineNum;
    List<Integer> oldCodeChangeLineNum;

    public LocalDiffFile(DiffEntry entry){
        this.filePath = entry.getNewPath();
        this.oldFilePath = entry.getOldPath();
    }

    public void patchParser(){
        this.oldCodeChangeLineNum = getLineNum('-');
        this.codeChangeLineNum = getLineNum('+');
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
