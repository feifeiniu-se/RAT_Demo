package Constructor.Enums;

import Project.RefactoringMiner.Refactoring;
import Project.RefactoringMiner.SideLocation;
import Project.Utils.Component;
import Project.Utils.LocalFileDiff;
import Project.Utils.Metric;

import java.util.List;

import static Constructor.Utils.*;

public enum ComponentOperator {

    Move_Source_Folder{
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r){

        }
    },
    Move_Package{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r){
            //todo
        }
    },
    Rename_Package{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r){
            //todo
        }
    },
    Split_Package{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r){
            //todo
        }
    },
    Merge_Package{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r){
            //todo
        }
    },
    Change_Type_Declaration_Kind {//interface class, if the name should change, just update the name, no other changes

        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');

        }
    },
    Collapse_Hierarchy {

        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
            SideLocation ls = r.getLeftSideLocations().get(0);
            SideLocation rs = r.getRightSideLocations().get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.getCodeElement()), findFile(diffList, rs.getFilePath()).findComponent(rs.getCodeElement()));
        }
    },
    Extract_Superclass {
        //add a new class, the last filepath on the rightfilepath is the new superclass
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactorFirstLine(metric, diffList, r.leftFilter("original sub-type declaration"), 'L');
            refactorFirstLine(metric, diffList, r.rightFilter("sub-type declaration after extraction"), 'R');
            refactoredLine(metric, diffList, r.rightFilter("extracted super-type declaration"), 'R');
        }
    },
    Extract_Interface {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.rightFilter("extracted super-type declaration"), 'R');
            refactorFirstLine(metric, diffList, r.leftFilter("original sub-type declaration"), 'L');
            refactorFirstLine(metric, diffList, r.rightFilter("sub-type declaration after extraction"), 'R');
        }
    },
    Extract_Class { // be careful it may also in move methods

        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.R);
            List<SideLocation> right = r.rightFilter("type declaration after extraction");
            List<SideLocation> right2 = r.rightFilter("extracted type declaration");
            refactoredLine(metric, diffList, right, 'R', OpeTypeEnum.R);
            refactoredLine(metric, diffList, right2, 'R', OpeTypeEnum.R);
        }
    },
    Extract_Subclass {
        @Override
        // almost the same with extract class
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.R);
            List<SideLocation> right = r.rightFilter("type declaration after extraction");
            List<SideLocation> right2 = r.rightFilter("extracted type declaration");
            refactoredLine(metric, diffList, right, 'R', OpeTypeEnum.R);
            refactoredLine(metric, diffList, right2, 'R', OpeTypeEnum.R);
        }
    },
    Merge_Class {//merge methods & attributes in two or more classes to one new class

        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
        }
    },
    Move_Class {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            SideLocation ls = r.getLeftSideLocations().get(0);
            SideLocation rs = r.getRightSideLocations().get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.getCodeElement()), findFile(diffList, rs.getFilePath()).findComponent(rs.getCodeElement()));

        }
    },
    Rename_Class {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactorFirstLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(metric, diffList, r.getRightSideLocations(), 'R');
            SideLocation ls = r.getLeftSideLocations().get(0);
            SideLocation rs = r.getRightSideLocations().get(0);
            metric.getRenamePairs().put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.getCodeElement()), findFile(diffList, rs.getFilePath()).findComponent(rs.getCodeElement()));
        }
    },
    Move_And_Rename_Class {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            refactorFirstLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(metric, diffList, r.getRightSideLocations(), 'R');
            SideLocation ls = r.getLeftSideLocations().get(0);
            SideLocation rs = r.getRightSideLocations().get(0);
            Component lc = findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.getCodeElement());
            Component rc = findFile(diffList, rs.getFilePath()).findComponent(rs.getCodeElement());
            metric.renamePairs.put(lc, rc);
        }
    },
    Add_Class_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> right = r.rightFilter("added annotation");
            refactoredLine(metric, diffList, right, 'R');
        }
    },
    Remove_Class_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("removed annotation");
            refactoredLine(metric, diffList, left, 'L');
        }
    },
    Modify_Class_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("original annotation");
            List<SideLocation> right = r.rightFilter("modified annotation");
            refactoredLine(metric, diffList, left, 'L');
            refactoredLine(metric, diffList, right, 'R');
        }
    },
    Add_Class_Modifier{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactorFirstLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Remove_Class_Modifier{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactorFirstLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Change_Class_Access_Modifier{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactorFirstLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Extract_Method  {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("extracted code from source method declaration");
            List<SideLocation> right = r.rightFilter("extracted code to extracted method declaration");
            refactoredLine(metric, diffList, left, 'L', OpeTypeEnum.M);
            refactoredLine(metric, diffList, right, 'R', OpeTypeEnum.M);
        }
    },
    Inline_Method {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("inlined code from inlined method declaration");
            List<SideLocation> right = r.rightFilter("inlined code in target method declaration");
            refactoredLine(metric, diffList, left, 'L', OpeTypeEnum.M);
            refactoredLine(metric, diffList, right, 'R', OpeTypeEnum.M);
        }
    },
    Pull_Up_Method {//相当于move
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
        }
    },
    Push_Down_Method {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            //相当于move
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
        }
    },
    Extract_And_Move_Method {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("extracted code from source method declaration"), 'L', OpeTypeEnum.M);
            refactoredLine(metric, diffList, r.rightFilter("extracted code to extracted method declaration"), 'R', OpeTypeEnum.M);
        }
    },
    Move_And_Inline_Method {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("inlined code from inlined method declaration");
            List<SideLocation> left2 = r.leftFilter("deleted statement in inlined method declaration");
            List<SideLocation> right = r.rightFilter("inlined code in target method declaration");
            refactoredLine(metric, diffList, left, 'L', OpeTypeEnum.M);
            refactoredLine(metric, diffList, left2, 'L', OpeTypeEnum.R);
            refactoredLine(metric, diffList, right, 'R', OpeTypeEnum.M);
        }
    },
    Move_And_Rename_Method {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            SideLocation ls = r.getLeftSideLocations().get(0);
            SideLocation rs = r.getRightSideLocations().get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseMethodDeclaration().get("MN")), findFile(diffList, rs.getFilePath()).findComponent(rs.parseMethodDeclaration().get("MN")));

        }
    },
    Move_Method {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            //todo https://github.com/apache/commons-collections/commit/67ea7c77421096fed1e1bd147613015d3b2d3c13
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            SideLocation ls = r.getLeftSideLocations().get(0);
            SideLocation rs = r.getRightSideLocations().get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseMethodDeclaration().get("MN")), findFile(diffList, rs.getFilePath()).findComponent(rs.parseMethodDeclaration().get("MN")));
        }
    },
    Change_Return_Type {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original return type"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("changed return type"), 'R');
            SideLocation ls = r.getLeftSideLocations().get(1);
            SideLocation rs = r.getRightSideLocations().get(1);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseMethodDeclaration().get("MN")), findFile(diffList, rs.getFilePath()).findComponent(rs.parseMethodDeclaration().get("MN")));
        }
    },
    Rename_Method {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactorFirstLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(metric, diffList, r.getRightSideLocations(), 'R');
            SideLocation ls = r.getLeftSideLocations().get(0);
            SideLocation rs = r.getRightSideLocations().get(0);
            metric.getM_M().add(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseMethodDeclaration().get("MN")));
            metric.getM_M().add(findFile(diffList, rs.getFilePath()).findComponent(rs.parseMethodDeclaration().get("MN")));
        }
    },
    Add_Method_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> right = r.rightFilter("added annotation");
            refactoredLine(metric, diffList, right, 'R');
        }
    },
    Remove_Method_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("removed annotation");
            refactoredLine(metric, diffList, left, 'L');
        }
    },
    Modify_Method_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("original annotation");
            List<SideLocation> right = r.rightFilter("modified annotation");
            refactoredLine(metric, diffList, left, 'L');
            refactoredLine(metric, diffList, right, 'R');
        }
    },
    Add_Method_Modifier{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactorFirstLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Remove_Method_Modifier{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactorFirstLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Change_Method_Access_Modifier{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactorFirstLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Add_Thrown_Exception_Type{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> right = r.rightFilter("added thrown exception type");
            refactoredLine(metric, diffList, right, 'R');
            refactorFirstLine(metric, diffList, r.leftFilter("original method declaration"), 'L');
            refactorFirstLine(metric, diffList, r.rightFilter("method declaration with added thrown exception type"), 'R');
        }
    },
    Remove_Thrown_Exception_Type{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("removed thrown exception type"), 'L');
            refactorFirstLine(metric, diffList, r.leftFilter("original method declaration"), 'L');
            refactorFirstLine(metric, diffList, r.rightFilter("method declaration with removed thrown exception type"), 'R');
        }
    },
    Change_Thrown_Exception_Type{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original exception type"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("changed exception type"), 'R');
        }
    },
    Parameterize_Variable {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original variable declaration"), 'L', OpeTypeEnum.R);
            refactoredLine(metric, diffList, r.rightFilter("renamed variable declaration"), 'R', OpeTypeEnum.R);
            SideLocation ls = r.leftFilter("original method declaration").get(0);
            SideLocation rs = r.rightFilter("method declaration with renamed variable").get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseMethodDeclaration().get("MN")), findFile(diffList, rs.getFilePath()).findComponent(rs.parseMethodDeclaration().get("MN")));
        }
    },
    Merge_Parameter {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("merged variable declaration"), 'L', OpeTypeEnum.R);
            refactoredLine(metric, diffList, r.rightFilter("new variable declaration"), 'R', OpeTypeEnum.R);
            SideLocation ls = r.leftFilter("original method declaration").get(0);
            SideLocation rs = r.rightFilter("method declaration with merged variables").get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseMethodDeclaration().get("MN")), findFile(diffList, rs.getFilePath()).findComponent(rs.parseMethodDeclaration().get("MN")));
        }
    },
    Split_Parameter {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original variable declaration"), 'L', OpeTypeEnum.R);
            refactoredLine(metric, diffList, r.rightFilter("split variable declaration"), 'R', OpeTypeEnum.R);
            SideLocation ls = r.leftFilter("original method declaration").get(0);
            SideLocation rs = r.rightFilter("method declaration with split variable").get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseMethodDeclaration().get("MN")), findFile(diffList, rs.getFilePath()).findComponent(rs.parseMethodDeclaration().get("MN")));
        }
    },
    Change_Parameter_Type {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original variable declaration"), 'L', OpeTypeEnum.R);
            refactoredLine(metric, diffList, r.rightFilter("changed-type variable declaration"), 'R', OpeTypeEnum.R);
            SideLocation ls = r.leftFilter("original method declaration").get(0);
            SideLocation rs = r.rightFilter("method declaration with changed variable type").get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseMethodDeclaration().get("MN")), findFile(diffList, rs.getFilePath()).findComponent(rs.parseMethodDeclaration().get("MN")));
        }
    },
    Add_Parameter {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactorFirstLine(metric, diffList, r.leftFilter("original method declaration"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("added parameter"), 'R');
            SideLocation ls = r.leftFilter("original method declaration").get(0);
            SideLocation rs = r.rightFilter("method declaration with added parameter").get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseMethodDeclaration().get("MN")), findFile(diffList, rs.getFilePath()).findComponent(rs.parseMethodDeclaration().get("MN")));

        }
    },
    Remove_Parameter {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("removed parameter"), 'L');
            refactorFirstLine(metric, diffList, r.leftFilter("method declaration with removed parameter"), 'R'); //todo
            SideLocation ls = r.leftFilter("original method declaration").get(0);
            SideLocation rs = r.rightFilter("method declaration with removed parameter").get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseMethodDeclaration().get("MN")), findFile(diffList, rs.getFilePath()).findComponent(rs.parseMethodDeclaration().get("MN")));
        }
    },
    Reorder_Parameter {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original parameter declaration"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("reordered parameter declaration"), 'R');
            SideLocation ls = r.leftFilter("original method declaration").get(0);
            SideLocation rs = r.rightFilter("method declaration with reordered parameters").get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseMethodDeclaration().get("MN")), findFile(diffList, rs.getFilePath()).findComponent(rs.parseMethodDeclaration().get("MN")));
        }
    },
    Parameterize_Attribute {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("renamed variable declaration"), 'R');
            SideLocation ls = r.leftFilter("original method declaration").get(0);
            SideLocation rs = r.rightFilter("method declaration with renamed variable").get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseMethodDeclaration().get("MN")), findFile(diffList, rs.getFilePath()).findComponent(rs.parseMethodDeclaration().get("MN")));

        }
    },
    Rename_Parameter{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("renamed variable declaration"), 'R');
        }
    },
    Add_Parameter_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("variable declaration with added annotation"), 'R');
            refactoredLine(metric, diffList, r.rightFilter("added annotation"), 'R');
        }
    },
    Remove_Parameter_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("removed annotation"), 'L');
            refactoredLine(metric, diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("variable declaration with removed annotation"), 'R');
        }
    },
    Modify_Parameter_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            //todo no example
        }
    },
    Add_Parameter_Modifier{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("variable declaration with added modifier"), 'R');
        }
    },
    Remove_Parameter_Modifier{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("variable declaration with removed modifier"), 'R');
        }
    },
    Localize_Parameter{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("renamed variable declaration"), 'R');
            SideLocation ls = r.leftFilter("original method declaration").get(0);
            SideLocation rs = r.rightFilter("method declaration with renamed variable").get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseMethodDeclaration().get("MN")), findFile(diffList, rs.getFilePath()).findComponent(rs.parseMethodDeclaration().get("MN")));

        }
    },
    Pull_Up_Attribute {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Push_Down_Attribute {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Move_Attribute {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Rename_Attribute {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Merge_Attribute {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Split_Attribute {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Change_Attribute_Type {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Extract_Attribute {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Encapsulate_Attribute {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Inline_Attribute {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Move_And_Rename_Attribute {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
            SideLocation ls = r.getLeftSideLocations().get(0);
            SideLocation rs = r.getRightSideLocations().get(0);
            metric.renamePairs.put(findOldFile(diffList, ls.getFilePath()).findOldComponent(ls.parseAttributeOrParameter()), findFile(diffList, rs.getFilePath()).findComponent(rs.parseAttributeOrParameter()));
        }
    },
    Replace_Attribute_With_Variable {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("original variable declaration"), 'R');
        }
    },
    Replace_Anonymous_With_Lambda {
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("anonymous class declaration"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("lambda expression"), 'R');
        }
    },
    Replace_Attribute_With_Attribute{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            //todo not found yet
        }
    },
    Replace_Attribute{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r){
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Add_Attribute_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Remove_Attribute_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Modify_Attribute_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Add_Attribute_Modifier{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Remove_Attribute_Modifier{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Change_Attribute_Access_Modifier{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(metric, diffList, r.getRightSideLocations(), 'R');
        }
    },

    Extract_Variable{
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r){
            List<SideLocation> left = r.leftFilter("statement with the initializer of the extracted variable");
            List<SideLocation> right = r.rightFilter("extracted variable declaration");
            List<SideLocation> right2 = r.rightFilter("statement with the name of the extracted variable");
            refactoredLine(metric, diffList, left, 'L');
            refactoredLine(metric, diffList, right, 'R');
            refactoredLine(metric, diffList, right2, 'R');

        }
    },
    Change_Variable_Type{
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("changed-type variable declaration");
            refactoredLine(metric, diffList, left, 'L');
            refactoredLine(metric, diffList, right, 'R');
        }
    },
    Inline_Variable{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("inlined variable declaration");
            List<SideLocation> left2 = r.leftFilter("statement with the name of the inlined variable");
            List<SideLocation> right = r.rightFilter("statement with the initializer of the inlined variable");
            refactoredLine(metric, diffList, left, 'L');
            refactoredLine(metric, diffList, left2, 'L');
            refactoredLine(metric, diffList, right, 'R');

        }
    },
    Rename_Variable{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("renamed variable declaration");
            refactoredLine(metric, diffList, left, 'L');
            refactoredLine(metric, diffList, right, 'R');
        }
    },
    Merge_Variable{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("merged variable declaration");
            List<SideLocation> right = r.rightFilter("new variable declaration");
            refactoredLine(metric, diffList, left, 'L');
            refactoredLine(metric, diffList, right, 'R');

        }
    },
    Split_Variable{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("split variable declaration");
            refactoredLine(metric, diffList, left, 'L');
            refactoredLine(metric, diffList, right, 'R');
        }
    },
    Add_Variable_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("added annotation");
            List<SideLocation> right2 = r.rightFilter("variable declaration with added annotation");
            assert right2!=null;
            refactoredLine(metric, diffList,left, 'L');
            refactoredLine(metric, diffList, right, 'R');
            refactoredLine(metric, diffList, right2, 'R');
        }
    },
    Remove_Variable_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("removed annotation");
            List<SideLocation> left2 = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("variable declaration with removed annotation");
            refactoredLine(metric, diffList,left, 'L');
            refactoredLine(metric, diffList, right, 'R');
            refactoredLine(metric, diffList, left2, 'L');
        }
    },
    Modify_Variable_Annotation{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("original annotation");
            List<SideLocation> left2 = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("modified annotation");
            List<SideLocation> right2 = r.rightFilter("variable declaration with modified annotation");
            refactoredLine(metric, diffList,left, 'L');
            refactoredLine(metric, diffList,left2, 'L');
            refactoredLine(metric, diffList, right, 'R');
            refactoredLine(metric, diffList, right2, 'R');
        }
    },
    Add_Variable_Modifier{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("variable declaration with added modifier");
            refactoredLine(metric, diffList, left, 'L');
            refactoredLine(metric, diffList, right, 'R');
        }
    },
    Remove_Variable_Modifier{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("variable declaration with removed modifier");
            refactoredLine(metric, diffList, left, 'L');
            refactoredLine(metric, diffList, right, 'R');
        }
    },
    Replace_Variable_With_Attribute{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            refactoredLine(metric, diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(metric, diffList, r.rightFilter("renamed variable declaration"), 'R');
        }
    },
    Replace_Loop_With_Pipeline{
        @Override
        public void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r) {
            List<SideLocation> left = r.leftFilter("original code");
            List<SideLocation> right = r.rightFilter("pipeline code");
            refactoredLine(metric, diffList, left, 'L');
            refactoredLine(metric, diffList, right, 'R');
        }
    };

    public abstract void apply(Metric metric, List<LocalFileDiff> diffList, Refactoring r);

}
