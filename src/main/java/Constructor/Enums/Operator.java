package Constructor.Enums;


import Model.*;
import Project.RefactoringMiner.Refactoring;
import Project.RefactoringMiner.SideLocation;
import Project.Utils.DiffFile;

import javax.security.auth.callback.PasswordCallback;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static Constructor.Utils.cutString;
import static Constructor.Utils.*;

public enum Operator {
    Add_Package {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //create new codeBlock, update mapping, commitCodeChange
            CodeBlock codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Package);
            mappings.put(name, codeBlock);//更新mapping， codeblocks， commitcodechange
            PackageTime packageTime = new PackageTime(name, commitTime, Operator.Add_Package, codeBlock);
            codeBlocks.add(codeBlock);
        }
    },
    Remove_Package {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {

        }
    },
    Add_Class {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String signature, List<DiffFile> diffList) {
            //create codeblock, create codeblocktime, mapping update
            String name = sig2Name(signature);
            String fatherSig = sig2Father(signature);
            if (mappings.containsKey(fatherSig)) {
                CodeBlock codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Class);
                ClassTime classTime = new ClassTime(name, commitTime, Operator.Add_Class, codeBlock, mappings.get(fatherSig));
                mappings.put(signature, codeBlock);
                codeBlocks.add(codeBlock);
            }
        }
    },
    Remove_Class {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //use mapping to find codeblock, create codeblocktime,
        }
    },
    Add_Method {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {

        }
    },
    Remove_Method {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {

        }
    },
    Add_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {

        }
    },
    Remove_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {

        }
    },
    Rename_Package {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //Rename Package A to B
            //update package name, update mappings from package, class, method, attribute, etc.
            String[] des = r.getDescription().split(" ");
            String oldPkgName = des[2];
            String newPkgName = des[4];
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            //package signature 变更
            //org.jboss.jms.shared.tx， oldPkgName=org.jboss.jms.shared, 所以我们从类名中获取路径
            if (!mappings.containsKey(oldPkgName)) {
                String firstClassName = left.get(0).getCodeElement();
                if(firstClassName.contains(".")){
                    oldPkgName = firstClassName.substring(0, firstClassName.lastIndexOf("."));
                } else {
                    oldPkgName = "";
                }
            }
            assert (mappings.containsKey(oldPkgName));
            CodeBlock pkgBlock = mappings.get(oldPkgName);
            //update mappings for class, method and attribute.

            assert left.size() == right.size();
            for (int i = 0; i < left.size(); i++) {
                String oldClassSig = left.get(i).getCodeElement();
                String newClassSig = right.get(i).getCodeElement();
                assert mappings.containsKey(oldClassSig);
                CodeBlock classBlock = mappings.get(oldClassSig);
                classBlock.updateMappings(mappings, oldClassSig, newClassSig);
            }
            //update packageTime information
            PackageTime pkgTime = (PackageTime) pkgBlock.getLastHistory();
            pkgTime.setName(newPkgName);
            pkgTime.setTime(commitTime);
            pkgTime.setRefactorType(Operator.Rename_Package);
            mappings.put(newPkgName, pkgBlock);
            System.out.println(r.getType());
        }
    },
    Move_Package {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //use mappings to find codeblock, create packagetime, update mapping package, its classes, etc.
//            String[] tmp = r.getDescription().split(" ");
//            String oldPkgName = tmp[2];
//            String newPkgName = tmp[4];
//            //update package name
//            if(mappings.get(oldPkgName)==null){
//                //如果不是根包的话，就从移动的类的codeElement中截取包名
//                String tmp1 = r.getLeftSideLocations().get(0).getCodeElement();
//                oldPkgName = tmp1.substring(0, tmp1.lastIndexOf("."));
//            }
//            assert (mappings.containsKey(oldPkgName));
//            CodeBlock pkgBlock = mappings.get(oldPkgName);
////            pkgBlock.updateMappings(mappings, oldPkgName, newPkgName);//这里不能更新
//            PackageTime pkgTime = (PackageTime) pkgBlock.getLastHistory();
//            pkgTime.setTime(commitTime);
//            pkgTime.setRefactorType(Operator.Move_Package);
//
//
//            assert pkgTime.getParentCodeBlock()==null;//如果是内部类的迁移 还需要进行迁移
//
//            CodeBlock pkgBlockNew;
//            PackageTime pkgTimeNew;
//            if(mappings.containsKey(newPkgName)){
//                pkgBlockNew= mappings.get(newPkgName);
//                pkgTimeNew = (PackageTime) pkgBlock.getLastHistory();
//                pkgTimeNew.setTime(commitTime);
//                pkgTimeNew.setRefactorType(Operator.Move_Package);
//                commitTime.addCodeChange(pkgTimeNew);
//                pkgBlockNew.addHistory(pkgTimeNew);
//            }else{
//                pkgBlockNew = new CodeBlock(codeBlocks.size()+1, CodeBlockType.Package);
//                pkgTimeNew = new PackageTime(newPkgName, commitTime, Operator.Move_Package, pkgBlockNew);
//                mappings.put(newPkgName, pkgBlockNew);
//                codeBlocks.add(pkgBlockNew);
//            }
//
//            //move left files to right files ≈ move class
//            List<SideLocation> left = r.getLeftSideLocations();
//            List<SideLocation> right = r.getRightSideLocations();
//            assert left.size()==right.size();

//            for(int i=0; i<left.size(); i++){
//                String oldSig = left.get(i).getCodeElement();
//                String newSig = right.get(i).getCodeElement();
//                System.out.println(oldSig);
//                System.out.println(oldPkgName);
//                assert oldSig.contains(oldPkgName);
//                assert newSig.contains(newPkgName);
//                CodeBlock classBlock = mappings.get(oldSig);
//                classBlock.updateMappings(mappings, oldSig, newSig);
//                ClassTime classTime = (ClassTime) classBlock.getLastHistory();
//                //update classTime
//                classTime.setTime(commitTime);
//                classTime.setRefactorType(Operator.Move_Package);
//                classTime.setParentCodeBlock(pkgBlockNew);
//                pkgTimeNew.getClasses().add(classBlock);
//                pkgTime.getClasses().remove(classBlock);
//
//
//            }
            //根据leftSide和rightSide 将所有的类进行移动
            System.out.println(r.getDescription());
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == right.size();

            for (int i = 0; i < left.size(); i++) {
                String oldSig = left.get(i).getCodeElement();
                String newSig = right.get(i).getCodeElement();
                String oldPkgName = sig2Father(oldSig);
                String newFatherName = sig2Father(newSig);
                assert mappings.containsKey(oldSig);
                assert mappings.containsKey(oldPkgName);
                //remove from old package
                CodeBlock pkgBlockOld = mappings.get(oldPkgName);
                PackageTime pkgTimeOld = (PackageTime) pkgBlockOld.getLastHistory();
                pkgTimeOld.setTime(commitTime);
                pkgTimeOld.setRefactorType(Operator.Move_Package);

                //add to new package
                if (!mappings.containsKey(newFatherName)) {
                    if (isNestedClass(right.get(i).getFilePath(), newSig)) {//如果是内部类 就需要逐级新建包 类
                        if (!mappings.containsKey(sig2Package(right.get(i).getFilePath(), newSig))) {
                            Operator.Add_Package.apply(codeBlocks, mappings, null, commitTime, sig2Package(right.get(i).getFilePath(), newSig), diffList);//增加包节点
                        }
                        Operator.Add_Class.apply(codeBlocks, mappings, null, commitTime, newFatherName, diffList);

                    } else {
                        Operator.Add_Package.apply(codeBlocks, mappings, null, commitTime, newFatherName, diffList);
                    }

                }

                CodeBlock fatherBlockNew;
                CodeBlockTime fatherTimeNew;

                fatherBlockNew = mappings.get(newFatherName);
                fatherTimeNew = (CodeBlockTime) fatherBlockNew.getLastHistory();
                fatherTimeNew.setTime(commitTime);
                fatherTimeNew.setRefactorType(Operator.Move_Package);

                CodeBlock classBlock = mappings.get(oldSig);
                classBlock.updateMappings(mappings, oldSig, newSig);
                ClassTime classTime = (ClassTime) classBlock.getLastHistory();
                //update classTime
                classTime.setTime(commitTime);
                classTime.setRefactorType(Operator.Move_Package);
                classTime.setParentCodeBlock(fatherBlockNew);
                fatherTimeNew.getClasses().add(classBlock);
                pkgTimeOld.getClasses().remove(classBlock);
            }

            System.out.println(r.getType());
        }
    },
    Split_Package {
        //文件重命名
        //done create new pkgBlock, update mappings of class, method, etc
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
//            String[] newPkgNames = cutString(r.getDescription(), "[", "]").replace(" ", "").split(",");
//            String oldPkgName = r.getDescription().split(" ")[2];
//            assert mappings.containsKey(oldPkgName);
//            CodeBlock oldPkgBlock = mappings.get(oldPkgName);
//            PackageTime oldPkgTime = (PackageTime) oldPkgBlock.getLastHistory();
//            oldPkgTime.setTime(commitTime);
//            commitTime.addCodeChange(oldPkgTime);
//            oldPkgTime.setRefactorType(Operator.Split_Package);
//            oldPkgBlock.addHistory(oldPkgTime);
//
//            //先判断新的包是否存在 如果存在就更新 不存在就新建
//            for (String pkgName : newPkgNames) {
//                CodeBlock pkgBlock;
//                PackageTime pkgTime;
//                if (mappings.containsKey(pkgName)) {
//                    pkgBlock = mappings.get(pkgName);
//                    pkgTime = (PackageTime) pkgBlock.getLastHistory();
//                    pkgTime.setTime(commitTime);
//                    pkgTime.setRefactorType(Operator.Split_Package);
//
//
//                } else {
//                    pkgBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Package);
//                    pkgTime = new PackageTime(pkgName, commitTime, Operator.Split_Package, pkgBlock);
//                    mappings.put(pkgName, pkgBlock);
//                    codeBlocks.add(pkgBlock);
//                }
//                pkgTime.getDeriver().add(oldPkgTime);
//                oldPkgTime.getDerivee().add(pkgTime);
//            }
//
//            //firstly, update signature of methods, attributes in class, then update className
//            List<SideLocation> left = r.getLeftSideLocations();
//            List<SideLocation> right = r.getRightSideLocations();
//            assert left.size() == right.size();
//            for (int i = 0; i < left.size(); i++) {
//                String oldClassName = left.get(i).getCodeElement();
//                String newClassName = right.get(i).getCodeElement();
//                assert mappings.containsKey(oldClassName);
//                CodeBlock classBlock = mappings.get(oldClassName);
//                classBlock.updateMappings(mappings, oldClassName, newClassName);
//
//                //remove from old package, add to new package, update classTime information
//                oldPkgTime.getClasses().remove(classBlock);
//
//                ClassTime classTime = (ClassTime) classBlock.getLastHistory();
//                classTime.setTime(commitTime);
//                classTime.setRefactorType(Operator.Split_Package);
//                classTime.setName(newClassName.substring(newClassName.lastIndexOf(".") + 1));
//
//
//                String parentSig = newClassName.substring(0, newClassName.lastIndexOf("."));
//                if (!mappings.containsKey(parentSig)) {//如果parentSig还是不存在，说明是包中包，就直接新建新的包，暂时忽略包中包
//                    CodeBlock pkgBlockInner = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Package);
//                    PackageTime pkgTimeInner = new PackageTime(parentSig, commitTime, Operator.Split_Package, pkgBlockInner);
//                    mappings.put(parentSig, pkgBlockInner);
//                    codeBlocks.add(pkgBlockInner);
//                }
//                classTime.setParentCodeBlock(mappings.get(parentSig));
//                mappings.get(parentSig).getLastHistory().getClasses().add(classBlock);
//            }
            //todo 没有了package的继承关系
            //左右一个一个对照着来
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == right.size();
            for (int i = 0; i < left.size(); i++) {
                String oldClassSig = left.get(i).getCodeElement();
                String newClassSig = right.get(i).getCodeElement();
                assert !isNestedClass(left.get(i).getFilePath(), oldClassSig);
                assert mappings.containsKey(oldClassSig);
                CodeBlock classBlock = mappings.get(oldClassSig);
                //update mappings
                classBlock.updateMappings(mappings, oldClassSig, newClassSig);
                assert mappings.containsKey(sig2Father(oldClassSig));
                if (!mappings.containsKey(sig2Father(newClassSig))) {
                    if (isNestedClass(right.get(i).getFilePath(), newClassSig)) {//如果是内部类 就需要逐级新建包 类
                        if (!mappings.containsKey(sig2Package(right.get(i).getFilePath(), newClassSig))) {
                            Operator.Add_Package.apply(codeBlocks, mappings, null, commitTime, sig2Package(right.get(i).getFilePath(), newClassSig), diffList);//增加包节点
                        }
                        Operator.Add_Class.apply(codeBlocks, mappings, null, commitTime, sig2Father(newClassSig), diffList);

                    } else {
                        Operator.Add_Package.apply(codeBlocks, mappings, null, commitTime, sig2Father(newClassSig), diffList);
                    }

                }
                assert mappings.containsKey(sig2Father(newClassSig));
                CodeBlock oldFather = mappings.get(sig2Father(oldClassSig));
                CodeBlock newFather = mappings.get(sig2Father(newClassSig));
                PackageTime oldFatherTime = (PackageTime) oldFather.getLastHistory();
                oldFatherTime.setTime(commitTime);
                oldFatherTime.setRefactorType(Operator.Split_Package);
                oldFatherTime.getClasses().remove(classBlock);

                CodeBlockTime newFatherTime = (CodeBlockTime) newFather.getLastHistory();
                newFatherTime.setTime(commitTime);
                newFatherTime.setRefactorType(Split_Package);
                newFatherTime.getClasses().add(classBlock);


                //move class from old package to new package
                ClassTime classTime = (ClassTime) classBlock.getLastHistory();
                classTime.setName(sig2Name(newClassSig));
                classTime.setTime(commitTime);
                classTime.setRefactorType(Operator.Split_Package);
                classTime.setParentCodeBlock(newFather);
            }

            System.out.println(r.getType());
        }
    },
    Merge_Package {
        //文件重命名
        //move classes from old package to new package, update mappings for class, method, etc
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
//            String[] oldPkgNames = cutString(r.getDescription(), "[", "]").replace(" ", "").split(",");
//            String[] desc = r.getDescription().split(" ");
//            String newPkgName = desc[desc.length - 1];
//
//            //create new package
//            CodeBlock newPkgBlock;
//            PackageTime newPkgTime;
//            if (mappings.containsKey(newPkgName)) {
//                newPkgBlock = mappings.get(newPkgName);
//                newPkgTime = (PackageTime) newPkgBlock.getLastHistory();
//                newPkgTime.setName(newPkgName);
//                newPkgTime.setTime(commitTime);
//                commitTime.addCodeChange(newPkgTime);
//                newPkgTime.setRefactorType(Operator.Merge_Package);
//                newPkgBlock.addHistory(newPkgTime);
//            } else {
//                newPkgBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Package);
//                newPkgTime = new PackageTime(newPkgName, commitTime, Operator.Merge_Package, newPkgBlock);
//                mappings.put(newPkgName, newPkgBlock);
//                codeBlocks.add(newPkgBlock);
//            }
//            //find old packages, update deriver&derivee relation
//            for (String pkgName : oldPkgNames) {
//                assert mappings.containsKey(pkgName);
//                CodeBlock oldPkgBock = mappings.get(pkgName);
//                PackageTime oldPkgTime = (PackageTime) oldPkgBock.getLastHistory();
//                oldPkgTime.setTime(commitTime);
//                oldPkgTime.setRefactorType(Operator.Merge_Package);
//                oldPkgTime.getDerivee().add(newPkgTime);
//                newPkgTime.getDeriver().add(oldPkgTime);
//                commitTime.addCodeChange(oldPkgTime);
//                oldPkgBock.addHistory(oldPkgTime);
//            }

            //updating classes
//            List<SideLocation> left = r.getLeftSideLocations();
//            List<SideLocation> right = r.getRightSideLocations();
//            assert left.size() == right.size();
//            for (int i = 0; i < left.size(); i++) {
//                String oldClassSig = left.get(i).getCodeElement();
//                String newClassSig = right.get(i).getCodeElement();
//                assert mappings.containsKey(oldClassSig);
//                CodeBlock classBlock = mappings.get(oldClassSig);
//                //update mappings
//                classBlock.updateMappings(mappings, oldClassSig, newClassSig);
//                //move class from old package to new package
//                ClassTime classTime = (ClassTime) classBlock.getLastHistory();
//                String parentSig = newClassSig.substring(0, newClassSig.lastIndexOf("."));
//                classTime.setName(newClassSig.substring(newClassSig.lastIndexOf(".") + 1));
//                classTime.setTime(commitTime);
//
//                classTime.setRefactorType(Operator.Merge_Package);
//
//                assert mappings.containsKey(parentSig);
//                classTime.setParentCodeBlock(mappings.get(parentSig));
//            }

            //todo 没有了package的继承关系
            //左右一个一个对照着来
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == right.size();
            for (int i = 0; i < left.size(); i++) {
                String oldClassSig = left.get(i).getCodeElement();
                String newClassSig = right.get(i).getCodeElement();
                assert !isNestedClass(left.get(i).getFilePath(), oldClassSig);
                assert mappings.containsKey(oldClassSig);
                CodeBlock classBlock = mappings.get(oldClassSig);
                //update mappings
                classBlock.updateMappings(mappings, oldClassSig, newClassSig);
                assert mappings.containsKey(sig2Father(oldClassSig));
                if (!mappings.containsKey(sig2Father(newClassSig))) {
                    Operator.Add_Package.apply(codeBlocks, mappings, null, commitTime, sig2Father(newClassSig), diffList);
                }
                assert mappings.containsKey(sig2Father(newClassSig));
                CodeBlock oldFather = mappings.get(sig2Father(oldClassSig));
                CodeBlock newFather = mappings.get(sig2Father(newClassSig));
                PackageTime oldFatherTime = (PackageTime) oldFather.getLastHistory();
                oldFatherTime.setTime(commitTime);
                oldFatherTime.setRefactorType(Operator.Merge_Package);
                oldFatherTime.getClasses().remove(classBlock);

                PackageTime newFatherTime = (PackageTime) newFather.getLastHistory();
                newFatherTime.setTime(commitTime);
                newFatherTime.setRefactorType(Operator.Merge_Package);
                newFatherTime.getClasses().add(classBlock);


                //move class from old package to new package
                ClassTime classTime = (ClassTime) classBlock.getLastHistory();
                classTime.setName(sig2Name(newClassSig));
                classTime.setTime(commitTime);
                classTime.setRefactorType(Operator.Merge_Package);
                classTime.setParentCodeBlock(newFather);
            }

            System.out.println(r.getType());

        }
    },
    Change_Type_Declaration_Kind {//interface class, if the name should change, just update the name, no other changes

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            assert r.getLeftSideLocations().size() == r.getRightSideLocations().size();
            assert r.getLeftSideLocations().size() == 1;
            String nameOld = r.getLeftSideLocations().get(0).getCodeElement();
            String nameNew = r.getRightSideLocations().get(0).getCodeElement();
            if (nameOld.equals(nameNew)) {
                return;//nothing changes, return
            }
            //change name
            assert mappings.containsKey(nameOld);
            CodeBlock classBlock = mappings.get(nameOld);
            ClassTime classTime = (ClassTime) classBlock.getLastHistory();
            classTime.setName(nameNew);
            mappings.put(nameNew, classBlock);
            classTime.setTime(commitTime);
            classTime.setRefactorType(Operator.Change_Type_Declaration_Kind);

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Collapse_Hierarchy {//较为复杂的 将一个具体的类的内容移到接口类中进行实现

        //todo
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Extract_Superclass {
        //add a new class, the last filepath on the rightfilepath is the new superclass
        //在这里暂时没有方法和属性的移动，pull up method/attribute 一般是对应的从原来类中的方法、属性移到新的superClass中
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String[] oldNames = cutString(r.getDescription(), "[", "]").replace(" ", "").split(",");

            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            String newSig = right.get(right.size() - 1).getCodeElement();
            String fatherSig = sig2Father(newSig);

            assert left.size() == right.size() - 1;
            assert left.size() == oldNames.length;
            //add new class
            System.out.println(r.getDescription());
//            assert !mappings.containsKey(newSig);
            CodeBlock classBlock = mappings.get(newSig);
            ClassTime classTime = (ClassTime) classBlock.getLastHistory();
            classTime.setRefactorType(Operator.Extract_Superclass);
            for (int i = 0; i < left.size(); i++) {
                assert mappings.containsKey(left.get(i).getCodeElement());
                CodeBlock oldClassBlock = mappings.get(left.get(i).getCodeElement());
                ClassTime oldClassTime = (ClassTime) oldClassBlock.getLastHistory();
                oldClassTime.setTime(commitTime);
                oldClassTime.setRefactorType(Operator.Extract_Superclass);
                oldClassTime.getDerivee().add(classTime);
                classTime.getDeriver().add(oldClassTime);
            }

            refactorFirstLine(r, mappings, diffList, r.leftFilter("original sub-type declaration"), 'L');
            refactorFirstLine(r, mappings, diffList, r.rightFilter("sub-type declaration after extraction"), 'R');
            refactoredLine(diffList, r.rightFilter("extracted super-type declaration"), 'R');
            System.out.println(r.getType());
        }
    },
    Extract_Interface {
        //done 文件重命名 这个有点特殊
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //左边、右边前几个分别是original类的声明，右边最后一个是新的interface
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();

            String classSig = right.get(right.size() - 1).getCodeElement();
            assert left.size() == right.size() - 1;
//            assert !mappings.containsKey(className);
            //create new className
            CodeBlock classBlock = mappings.get(classSig);
            ClassTime classTime = (ClassTime) classBlock.getLastHistory();
            classTime.setRefactorType(Operator.Extract_Interface);
            //derive and deriver relation
            for (int i = 0; i < left.size(); i++) {
                String oldName = left.get(i).getCodeElement();
                String newName = right.get(i).getCodeElement();
                System.out.println(r.getDescription());
                assert mappings.containsKey(oldName);
                CodeBlock originalClassBlock = mappings.get(oldName);
//                if (!oldName.equals(newName)) {
//                    originalClassBlock.updateMappings(mappings, oldName, newName);// todo 如果真的不一样，还需要更新classTime
//                }
                mappings.put(newName, originalClassBlock);
                ClassTime originalClassTime = (ClassTime) originalClassBlock.getLastHistory();
                originalClassTime.setTime(commitTime);
                originalClassTime.setRefactorType(Operator.Extract_Interface);
                originalClassTime.getDerivee().add(classTime);
                classTime.getDeriver().add(originalClassTime);
            }

            refactoredLine(diffList, r.rightFilter("extracted super-type declaration"), 'R');
            refactorFirstLine(r, mappings, diffList, r.leftFilter("original sub-type declaration"), 'L');
            refactorFirstLine(r, mappings, diffList, r.rightFilter("sub-type declaration after extraction"), 'R');
            System.out.println(r.getType());
        }
    },
    Extract_Class { // TODO 移动的方法 也会在后边的move method方法中出现 所以需要注意

        @Override
        //将原来代码中的一些方法抽出来，放到新建的类中。新建一个类，将一些方法从旧的类中移到新的类中
        //左边第一个是original类抽取前的声明，右边第一个是original类抽取后的声明，第二个是抽取的类的声明。剩下所有是抽取的方法以及属性，需要进行迁移
        //create new classBlock, move classes & methods & attributes from old class to new class
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == 1;
            String newSig = right.get(1).getCodeElement();
            String originSigOld = left.get(0).getCodeElement();
            String originSigNew = right.get(0).getCodeElement();
            String newFatherName = sig2Father(newSig);
            String newClassName = newSig.substring(newSig.lastIndexOf(".") + 1);

//            System.out.println(commitTime.getCommitID());
            System.out.println(r.getDescription());
            assert mappings.containsKey(originSigOld);
            CodeBlock oldClassBlock = mappings.get(originSigOld);

            //add new classBlock
            CodeBlock classBlock = mappings.get(newSig);
            ClassTime classTime = (ClassTime) classBlock.getLastHistory();
            classTime.setRefactorType(Operator.Extract_Class);
            //需要注意内部类的迁移

            //create oldClassTime
            ClassTime oldClassTime = (ClassTime) oldClassBlock.getLastHistory();
            oldClassTime.setTime(commitTime);
            oldClassTime.setRefactorType(Extract_Class);
            oldClassTime.getDerivee().add(classTime);
            classTime.getDeriver().add(oldClassTime);

            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.R);
            List<SideLocation> right1 = r.rightFilter("type declaration after extraction");
            List<SideLocation> right2 = r.rightFilter("extracted type declaration");
            refactoredLine(mappings, diffList, right1, 'R', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, right2, 'R', OpeTypeEnum.R);
            System.out.println(r.getType());
        }
    },
    Extract_Subclass {
        @Override
        // 跟extract class几乎差不多
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == 1;
            String newSig = right.get(1).getCodeElement();
            String originSigOld = left.get(0).getCodeElement();
            String originSigNew = right.get(0).getCodeElement();

//            System.out.println(r.getDescription());
//            assert originSigOld.equals(originSigNew);//如果新旧名字不一样 就需要更新名字
            assert mappings.containsKey(originSigOld);

//            assert !mappings.containsKey(newSig);
            //add new classBlock
            CodeBlock classBlock = mappings.get(newSig);
            ClassTime classTime = (ClassTime) classBlock.getLastHistory();
            classTime.setRefactorType(Operator.Extract_Subclass);
            //需要注意内部类的迁移

            //create oldClassTime
            CodeBlock oldClassBlock = mappings.get(originSigOld);
            ClassTime oldClassTime = (ClassTime) oldClassBlock.getLastHistory();
            oldClassTime.setTime(commitTime);
            oldClassTime.setRefactorType(Extract_Class);
            oldClassTime.getDerivee().add(classTime);
            classTime.getDeriver().add(oldClassTime);

            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.R);
            List<SideLocation> right1 = r.rightFilter("type declaration after extraction");
            List<SideLocation> right2 = r.rightFilter("extracted type declaration");
            refactoredLine(mappings, diffList, right1, 'R', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, right2, 'R', OpeTypeEnum.R);
            System.out.println(r.getType());
        }
    },
    Merge_Class {//merge methods & attributes in two or more classes to one new class

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
//            update class, target class, move method and attribute from original class to target class; update mapping
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert right.size() == 1;
            String newSig = right.get(0).getCodeElement();
            System.out.println(r.getDescription());
            //new classBlock, new classTime
            CodeBlock newClassBlock;
            ClassTime newClassTime;
            newClassBlock = mappings.get(newSig);
            newClassTime = (ClassTime) newClassBlock.getLastHistory();
            newClassTime.setTime(commitTime);
            newClassTime.setRefactorType(Operator.Merge_Class);//deriver, sons
            //old classes, move method, attribute, etc from old class to new class, update mappings
            for (int i = 0; i < left.size(); i++) {
                String oldSig = left.get(i).getCodeElement();
                assert mappings.containsKey(oldSig);
                CodeBlock oldClassBlock = mappings.get(oldSig);
                ClassTime oldClassTime = (ClassTime) oldClassBlock.getLastHistory();
                oldClassTime.setTime(commitTime);
                oldClassTime.setRefactorType(Operator.Merge_Class);
                oldClassTime.getDerivee().add(newClassTime);
                newClassTime.getDeriver().add(oldClassTime);

                //move sons
                newClassTime.getClasses().addAll(oldClassTime.getClasses());
                newClassTime.getMethods().addAll(oldClassTime.getMethods());
                newClassTime.getAttributes().addAll(oldClassTime.getAttributes());

                //change sons' parentBlock
                if (!(oldClassTime.getClasses() == null)) {
                    for (CodeBlock sonBlock : oldClassTime.getClasses()) {
                        sonBlock.updateMappings(mappings, sonBlock.getLastHistory().getSignature(), sonBlock.getLastHistory().getSignature().replace(oldSig, newSig));
                        ClassTime sonTime = (ClassTime) sonBlock.getLastHistory();
                        sonTime.setTime(commitTime);
                        sonTime.setRefactorType(Operator.Merge_Class);
                        sonTime.setParentCodeBlock(newClassBlock);
                    }
                }
                if (!(oldClassTime.getMethods() == null)) {
                    for (CodeBlock sonBlock : oldClassTime.getMethods()) {
                        sonBlock.updateMappings(mappings, sonBlock.getLastHistory().getSignature(), sonBlock.getLastHistory().getSignature().replace(oldSig, newSig));
                        MethodTime sonTime = (MethodTime) sonBlock.getLastHistory();
                        sonTime.setTime(commitTime);
                        sonTime.setRefactorType(Operator.Merge_Class);
                        sonTime.setParentCodeBlock(newClassBlock);
                    }
                }
                if (!(oldClassTime.getAttributes() == null)) {
                    for (CodeBlock sonBlock : oldClassTime.getAttributes()) {
                        sonBlock.updateMappings(mappings, sonBlock.getLastHistory().getSignature(), sonBlock.getLastHistory().getSignature().replace(oldSig, newSig));
                        AttributeTime sonTime = (AttributeTime) sonBlock.getLastHistory();
                        sonTime.setTime(commitTime);
                        sonTime.setRefactorType(Operator.Merge_Class);
                        sonTime.setParentCodeBlock(newClassBlock);
                    }
                }
                //clear all sons from old class
                oldClassTime.setClasses(new HashSet<>());
                oldClassTime.setMethods(new HashSet<>());
                oldClassTime.setAttributes(new HashSet<>());
            }

            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Move_Class {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //update class, class.father, class.son
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == right.size();
            assert left.size() == 1;
            String oldSig = left.get(0).getCodeElement();
            String newSig = right.get(0).getCodeElement();
            System.out.println(r.getDescription());

            assert mappings.containsKey(oldSig);

            CodeBlock classBlock = mappings.get(oldSig);
            classBlock.updateMappings(mappings, oldSig, newSig);//update mappings
            ClassTime classTime = (ClassTime) classBlock.getLastHistory();
            //update classTime
            classTime.setName(newSig.substring(newSig.lastIndexOf(".") + 1));
            classTime.setTime(commitTime);
            classTime.setRefactorType(Operator.Move_Class);

            //move class from old package to new package
            CodeBlock oldFather = classTime.getParentCodeBlock();//old Father

            //if the new father doesn't exist, it means this class is an inner class, and the father is a new class, we have to add the new class
            String fatherSigNew = sig2Father(newSig);//new father name
            if (!mappings.containsKey(fatherSigNew)) {//如果父亲节点不存在，就逐级新建类， 假设父亲包节点已经存在
                assert mappings.containsKey(sig2Package(right.get(0).getFilePath(), fatherSigNew)); // 假设包已经存在mapping中了
                Operator.Add_Class.apply(codeBlocks, mappings, null, commitTime, fatherSigNew, diffList);
            }
            assert mappings.containsKey(fatherSigNew);
            CodeBlock newFather = mappings.get(fatherSigNew);
            CodeBlockTime newFatherTime = (CodeBlockTime) newFather.getLastHistory();
            newFatherTime.setTime(commitTime);
            newFatherTime.setRefactorType(Operator.Move_Class);

            //update old father
            CodeBlockTime oldFatherTime = (CodeBlockTime) oldFather.getLastHistory();
            oldFatherTime.setTime(commitTime);
            oldFatherTime.setRefactorType(Operator.Move_Class);
            if(oldFatherTime.getClasses() != null){
                oldFatherTime.getClasses().remove(classBlock);
            }


            //update new father
            if(newFatherTime.getClasses() != null){
                newFatherTime.getClasses().add(classBlock);
            }
            classTime.setParentCodeBlock(newFather);

            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Rename_Class {
        @Override
        // update class name, update mappings of methods, attributes, etc.
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == right.size();
            assert left.size() == 1;
            System.out.println(r.getDescription());
            String oldName = left.get(0).getCodeElement();
            String newName = right.get(0).getCodeElement();
            assert mappings.containsKey(oldName);
            CodeBlock classBlock = mappings.get(oldName);
            //updating mappings
//            System.out.println(oldName);
            classBlock.updateMappings(mappings, oldName, newName);

            ClassTime classTime = (ClassTime) classBlock.getLastHistory();
            //update classTime
            classTime.setName(newName.substring(newName.lastIndexOf(".") + 1));
            classTime.setTime(commitTime);
            classTime.setRefactorType(Operator.Rename_Class);

            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
//            System.out.println(commitTime.getCommitID());
        }
    },
    Move_And_Rename_Class {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == right.size();
            assert left.size() == 1;
            String oldSig = left.get(0).getCodeElement();
            String newSig = right.get(0).getCodeElement();

            System.out.println(r.getDescription());

            String tmp = sig2Father(oldSig) + "." + newSig.substring(newSig.lastIndexOf(".")+1);//old package + new class name
            String tmp1 = sig2Father(newSig) + "." + oldSig.substring(oldSig.lastIndexOf(".")+1);//new package + old class name

            assert mappings.containsKey(oldSig);
            CodeBlock classBlock = mappings.get(oldSig);
            //update mappings
            classBlock.updateMappings(mappings, oldSig, tmp);
            classBlock.updateMappings(mappings, oldSig, tmp1);
            classBlock.updateMappings(mappings, oldSig, newSig);

            ClassTime classTime = (ClassTime) classBlock.getLastHistory();
            //update classTime
            classTime.setName(newSig.substring(newSig.lastIndexOf(".") + 1));
            classTime.setTime(commitTime);
            classTime.setRefactorType(Operator.Move_And_Rename_Class);

            //move class from old package to new package
            //old Father
            CodeBlock oldFather = classTime.getParentCodeBlock();

            CodeBlock newFather;
            CodeBlockTime newFatherTime;
            newFather = mappings.get(sig2Father(newSig));
            newFatherTime = (CodeBlockTime) newFather.getLastHistory();
            newFatherTime.setTime(commitTime);
            newFatherTime.setRefactorType(Operator.Move_And_Rename_Class);

            //update old father
            CodeBlockTime oldFatherTime = (CodeBlockTime) oldFather.getLastHistory();
            oldFatherTime.setTime(commitTime);
            oldFatherTime.setRefactorType(Operator.Move_And_Rename_Class);
            oldFatherTime.getClasses().remove(classBlock);

            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Extract_Method {
        //add method 从现有方法的代码中抽取部分生成新的方法，methodB derived from methodA，
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();

//            System.out.println(commitTime.getCommitID());
            System.out.println(r.getDescription());
            HashMap<String, String> oldMethod = r.getLeftSideLocations().get(0).parseMethodDeclaration();//parse the method name
            HashMap<String, String> newMethod = r.getRightSideLocations().get(0).parseMethodDeclaration();
            HashMap<String, String> oldMethodNew;
//            if (r.getRightSideLocations().get(r.getLeftSideLocations().size()).getCodeElement().equals("source method declaration after extraction")) {
//                oldMethodNew = r.getRightSideLocations().get(r.getLeftSideLocations().size()).parseMethodDeclaration();
//            } else {
//                oldMethodNew = r.rightFilter("source method declaration after extraction").get(0).parseMethodDeclaration();
//            }

            oldMethodNew = r.rightFilter("source method declaration after extraction").get(0).parseMethodDeclaration();
            assert mappings.containsKey(className + ":" + oldMethod.get("MN"));
            assert mappings.containsKey(className);
            CodeBlock oldMethodBlock = mappings.get(className + ":" + oldMethod.get("MN"));
            CodeBlock classBlock = mappings.get(className);
            //create new methodBlock
            CodeBlock newMethodBlock = mappings.get(className + ":" + newMethod.get("MN"));
//            if(newMethodBlock == null){
//                System.out.println(className + ":" + newMethod.get("MN"));
//                mappings.entrySet().stream().filter(p -> p.getKey().contains("org.apache.maven.tools.plugin.util.PluginUtils")).forEach(System.out::println);
//            }
            MethodTime methodTime = (MethodTime) newMethodBlock.getLastHistory();
            methodTime.setRefactorType(Operator.Extract_Method);
            //add new method to class
            //在创建新的method时就已经更新了
            //create methodblock for old method
            MethodTime oldMethodTime = (MethodTime) oldMethodBlock.getLastHistory();
            oldMethodTime.setTime(commitTime);
            oldMethodTime.setRefactorType(Operator.Extract_Method);
            oldMethodTime.getDerivee().add(methodTime);
            methodTime.getDeriver().add(oldMethodTime);
            List<SideLocation> left = r.leftFilter("extracted code from source method declaration");
            List<SideLocation> right = r.rightFilter("extracted code to extracted method declaration");
            refactoredLine(mappings, diffList, left, 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, right, 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Inline_Method {
        //与extract method相反，delete 方法，将方法的内容合并到已有的方法中 左边第一个是被inline方法的声明，左边倒数第二个是target method 原来的声明 右边第一个是target method的新声明
        //deriver, derivee, parentBlock.method remove
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
//            assert r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath());
            HashMap<String, String> inlinedMethod = r.getLeftSideLocations().get(0).parseMethodDeclaration();//parse the method name
            HashMap<String, String> methodNameOld = r.leftFilter("target method declaration before inline").get(0).parseMethodDeclaration();
            HashMap<String, String> methodNameNew = r.getRightSideLocations().get(0).parseMethodDeclaration();
            assert mappings.containsKey(className + ":" + inlinedMethod.get("MN"));
            assert mappings.containsKey(className);
//            System.out.println(className+":"+methodNameOld);
            assert mappings.containsKey(className + ":" + methodNameOld.get("MN"));
            CodeBlock inlinedMethodBlock = mappings.get(className + ":" + inlinedMethod.get("MN"));
            CodeBlock classBlock = mappings.get(className);
            CodeBlock targetMethodBlock = mappings.get(className + ":" + methodNameOld.get("MN"));
            if (!methodNameNew.get("MN").equals(methodNameOld.get("MN"))) {
                mappings.put(className + ":" + methodNameNew.get("MN"), targetMethodBlock);
            }
            //remove inlined methodBlock
            MethodTime inlinedMethodTime = (MethodTime) inlinedMethodBlock.getLastHistory();
            inlinedMethodTime.setTime(commitTime);
            inlinedMethodTime.setRefactorType(Operator.Inline_Method);

            //derive relation
            MethodTime targetMethodTime = (MethodTime) targetMethodBlock.getLastHistory();
            targetMethodTime.setTime(commitTime);
            targetMethodTime.setRefactorType(Operator.Inline_Method);
            targetMethodTime.getDeriver().add(inlinedMethodTime);
            inlinedMethodTime.getDerivee().add(targetMethodTime);

            //remove from class
            ClassTime classTime = (ClassTime) classBlock.getLastHistory();
            classTime.setTime(commitTime);
            classTime.setRefactorType(Operator.Inline_Method);
            classTime.getMethods().remove(inlinedMethodBlock);
            List<SideLocation> left = r.leftFilter("inlined code from inlined method declaration");
            List<SideLocation> right = r.rightFilter("inlined code in target method declaration");
            refactoredLine(mappings, diffList, left, 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, right, 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Pull_Up_Method {
        //move method from one class to super class,将几个子类中的方法移到超类中，跨文件，涉及方法的移动，还可能修改名字，但是不影响文件数目,一般情况下 一个refactoring只涉及一个类
        //相当于是move method //note be careful of move and rename things
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //find method, oldclass and newclass from mappings, change parentBlock from oldclass to newClass
            String oldClass = r.getFirstClassName();
            String newClass = r.getLastClassName();

            assert r.getLeftSideLocations().size() == r.getRightSideLocations().size();
            assert r.getRightSideLocations().size() == 1;
            HashMap<String, String> oldMethod = r.getLeftSideLocations().get(0).parseMethodDeclaration();//parse the method name
            HashMap<String, String> newMethod = r.getRightSideLocations().get(0).parseMethodDeclaration();

            System.out.println(r.getDescription());
            System.out.println(oldClass + ":" + oldMethod.get("MN"));

            assert mappings.containsKey(oldClass + ":" + oldMethod.get("MN"));
            assert mappings.containsKey(oldClass);
            CodeBlock methodBlock = mappings.get(oldClass + ":" + oldMethod.get("MN"));
            mappings.put(newClass + ":" + oldMethod.get("MN"), methodBlock);
            CodeBlock classBlockOld = mappings.get(oldClass);//original class
            assert classBlockOld.equals(methodBlock.getLastHistory().getParentCodeBlock());

            assert mappings.containsKey(newClass);
            CodeBlock classBlockNew = mappings.get(newClass);

            // create new methodTime, update parentBlock; create two classTime for oldClassBlock and newClassBlock, move from oldTime to newTime
            MethodTime methodTimeNew = (MethodTime) methodBlock.getLastHistory();
            methodTimeNew.setName(newMethod.get("MN"));
            methodTimeNew.setParameters(newMethod.get("PA"));
            methodTimeNew.setTime(commitTime);
            methodTimeNew.setRefactorType(Operator.Pull_Up_Method);
            methodTimeNew.setParentCodeBlock(classBlockNew);
            mappings.put(newClass + ":" + newMethod.get("MN"), methodBlock);

            //remove from oldClass
            ClassTime oldClassTimeNew = (ClassTime) classBlockOld.getLastHistory();
            oldClassTimeNew.setTime(commitTime);
            oldClassTimeNew.setRefactorType(Operator.Pull_Up_Method);
            assert oldClassTimeNew.getMethods().contains(methodBlock);
            oldClassTimeNew.getMethods().remove(methodBlock);
            //add to newClass
            ClassTime newClassTimeNew = (ClassTime) classBlockNew.getLastHistory();
            newClassTimeNew.setTime(commitTime);
            newClassTimeNew.setRefactorType(Operator.Pull_Up_Method);
            newClassTimeNew.getMethods().add(methodBlock);

            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Push_Down_Method {
        @Override
        //将父类中的方法 移到子类中去，一般会在不同的文件之间进行移动，甚至还有rename pull_Up_method的对立 move and rename method
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //find method, oldclass and newclass from mappings, create new codeBlock to the new class, the original method remains unchange
            String oldClass = r.getFirstClassName();
            String newClass = r.getLastClassName();
            assert r.getLeftSideLocations().size() == r.getRightSideLocations().size();
            assert r.getRightSideLocations().size() == 1;
            HashMap<String, String> oldMethod = r.getLeftSideLocations().get(0).parseMethodDeclaration();//parse the method name
            HashMap<String, String> newMethod = r.getRightSideLocations().get(0).parseMethodDeclaration();

//            System.out.println(r.getDescription());
//            mappings.get(oldClass).getHistory().forEach(e->System.out.println(e.getRefactorType()));
//            mappings.get(oldClass).getLastHistory().getMethods().forEach(e->System.out.println("HA： "+":" + e.getLastHistory().getName()));

            assert mappings.containsKey(oldClass + ":" + oldMethod.get("MN"));
            assert mappings.containsKey(oldClass);
            assert mappings.containsKey(newClass);
            CodeBlock methodBlock = mappings.get(oldClass + ":" + oldMethod.get("MN"));
            CodeBlock classBlockOld = mappings.get(oldClass);//original class
            CodeBlock classBlockNew = mappings.get(newClass);
//            assert classBlockOld.getLastHistory().getMethods().contains(methodBlock);

            //create new methodBlock to the new class, the original method remains unchanged
            CodeBlock methodBlockNew = mappings.get(newClass + ":" + newMethod.get("MN"));
            MethodTime methodTimeNew = (MethodTime) methodBlockNew.getLastHistory();
            methodTimeNew.setRefactorType(Operator.Push_Down_Method);
            //add derive relation
            MethodTime methodTimeOld = (MethodTime) methodBlock.getLastHistory();
            methodTimeOld.setTime(commitTime);
            methodTimeOld.setRefactorType(Operator.Push_Down_Method);
            methodTimeOld.getDerivee().add(methodTimeNew);
            methodTimeNew.getDeriver().add(methodTimeOld);

            //add to newClass
            ClassTime newClassTimeNew = (ClassTime) classBlockNew.getLastHistory();
            newClassTimeNew.setTime(commitTime);
            newClassTimeNew.setRefactorType(Operator.Push_Down_Method);
            newClassTimeNew.getMethods().add(methodBlockNew);
            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Extract_And_Move_Method {
        @Override
        //可能从多个方法中提取出一个新的方法， 涉及新建一个methodBlock，有多个derived from，并且移到了一个新的类中，跨文件
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String oldClassName = r.getFirstClassName();
            String newClassName = r.getLastClassName();
            assert oldClassName.contains(".");
            HashMap<String, String> oldMethod = r.getLeftSideLocations().get(0).parseMethodDeclaration();//parse the method name
            HashMap<String, String> deriveeMethod = r.getRightSideLocations().get(0).parseMethodDeclaration();
            HashMap<String, String> oldMethodNew = r.getRightSideLocations().get(r.getLeftSideLocations().size()).parseMethodDeclaration();
            System.out.println(r.getDescription());
            System.out.println(oldClassName + ":" + oldMethod.get("MN"));
            assert mappings.containsKey(oldClassName + ":" + oldMethod.get("MN"));
            assert mappings.containsKey(newClassName);
            CodeBlock oldMethodBlock = mappings.get(oldClassName + ":" + oldMethod.get("MN"));
            CodeBlock newClassBlock = mappings.get(newClassName);
            if (!oldMethod.get("MN").equals(oldMethodNew.get("MN"))) {
                mappings.put(oldClassName + ":" + oldMethodNew.get("MN"), oldMethodBlock);
            }
            //create new methodBlock
            CodeBlock newMethodBlock = mappings.get(newClassName + ":" + deriveeMethod.get("MN"));
            MethodTime methodTime = (MethodTime) newMethodBlock.getLastHistory();
            methodTime.setRefactorType(Operator.Extract_And_Move_Method);

            //create methodblock for old method
            MethodTime oldMethodTime = (MethodTime) oldMethodBlock.getLastHistory();
            oldMethodTime.setTime(commitTime);
            oldMethodTime.setRefactorType(Operator.Extract_Method);
            oldMethodTime.getDerivee().add(methodTime);
            methodTime.getDeriver().add(oldMethodTime);
            refactoredLine(mappings, diffList, r.leftFilter("extracted code from source method declaration"), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.rightFilter("extracted code to extracted method declaration"), 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Move_And_Inline_Method {// move & inline(inverse of extract method)

        //move inlined method from old class to new class, then inline method to target method, in new class.
        // left.get(0) is the declaration of inlined method, left.get(right.size()) is the declaration of target method before inline.
        //right.get(0) is the declaration of target method after inline
        //move inline method, inline to targe method
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String oldClassName = r.getFirstClassName();
            String[] tmp = r.getDescription().substring(0, r.getDescription().indexOf(" & ")).split(" ");
            String newClassName = tmp[tmp.length - 1];
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert oldClassName.contains(".");
            assert newClassName.contains(".");
            System.out.println(r.getDescription());

            HashMap<String, String> inlinedMethodInfo = left.get(0).parseMethodDeclaration();//parse the method name
            HashMap<String, String> targetMethodInfoOld = left.get(right.size()).parseMethodDeclaration();
            HashMap<String, String> targetMethodInfoNew = right.get(0).parseMethodDeclaration();

            assert mappings.containsKey(oldClassName);
            assert mappings.containsKey(newClassName);
            assert mappings.containsKey(oldClassName + ":" + inlinedMethodInfo.get("MN"));
            assert mappings.containsKey(newClassName + ":" + targetMethodInfoOld.get("MN"));
            CodeBlock oldClass = mappings.get(oldClassName);
            CodeBlock newClass = mappings.get(newClassName);
            CodeBlock inlinedMethod = mappings.get(oldClassName + ":" + inlinedMethodInfo.get("MN"));
            CodeBlock targetMethod = mappings.get(newClassName + ":" + targetMethodInfoOld.get("MN"));

            mappings.put(newClassName + ":" + inlinedMethodInfo.get("MN"), inlinedMethod);
            mappings.put(newClassName + ":" + targetMethodInfoNew.get("MN"), targetMethod);

            // move inlined method from oldClass to newClass
            //note db46f71b4d1ba606daa5ca832f48a50a60b178cd 把同一个方法inline到了多个不同的类的方法中 所以旧方法的parent还不能变
            MethodTime inlinedMethodTime = (MethodTime) inlinedMethod.getLastHistory();
            inlinedMethodTime.setTime(commitTime);
            inlinedMethodTime.setRefactorType(Operator.Move_And_Inline_Method);
            //remove from oldClass to newClass (note inlined method life end, so don't add to newClass.son)
            ClassTime oldClassTime = (ClassTime) oldClass.getLastHistory();
            oldClassTime.setTime(commitTime);
            oldClassTime.setRefactorType(Operator.Move_And_Inline_Method);
            // oldClass may don't contain inlinedMethod anymore, because method already has been inline before
            oldClassTime.getMethods().remove(inlinedMethod);

            //inline method to target method
            MethodTime targetMethodTime = (MethodTime) targetMethod.getLastHistory();
            targetMethodTime.setName(targetMethodInfoNew.get("MN"));
            targetMethodTime.setTime(commitTime);
            targetMethodTime.setRefactorType(Operator.Move_And_Inline_Method);
            targetMethodTime.getDeriver().add(inlinedMethodTime);
            inlinedMethodTime.getDerivee().add(targetMethodTime);

            List<SideLocation> left1 = r.leftFilter("inlined code from inlined method declaration");
            List<SideLocation> left2 = r.leftFilter("deleted statement in inlined method declaration");
            right = r.rightFilter("inlined code in target method declaration");
            refactoredLine(mappings, diffList, left1, 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, left2, 'L', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, right, 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Move_And_Rename_Method {//跨文件

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //find method, oldclass and newclass from mappings, change parentBlock from oldclass to newClass, update method name
            String oldClass = r.getFirstClassName();
            String newClass = r.getLastClassName();
            System.out.println(r.getDescription());
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert oldClass.contains(".");
            assert newClass.contains(".");
            assert left.size() == right.size();
            assert left.size() == 1;
            HashMap<String, String> oldMethod = left.get(0).parseMethodDeclaration();//parse the method name
            HashMap<String, String> newMethod = right.get(0).parseMethodDeclaration();
            String oldSig = oldClass + ":" + oldMethod.get("MN");
            String newSig = newClass + ":" + newMethod.get("MN");
            assert mappings.containsKey(oldSig);
            assert mappings.containsKey(oldClass);
            assert mappings.containsKey(newClass);
            CodeBlock methodBlock = mappings.get(oldSig);
            CodeBlock classBlockOld = mappings.get(oldClass);//original class
            CodeBlock classBlockNew = mappings.get(newClass);

            mappings.put(newClass + ":" + oldMethod.get("MN"), methodBlock);//note, this is necessary, because for refactoringMiner, they will use the new class
            mappings.put(newSig, methodBlock);

            if (methodBlock.getLastHistory().getParentCodeBlock().equals(classBlockNew)) {
                return;
            }// 如果已经移动过了，就结束本次；如果还没有移动过，就进行迁移
            assert classBlockOld.equals(methodBlock.getLastHistory().getParentCodeBlock());

            // create new methodTime, update parentBlock; create two classTime for oldClassBlock and newClassBlock, move from oldTime to newTime
            MethodTime methodTimeNew = (MethodTime) methodBlock.getLastHistory();
            methodTimeNew.setName(newMethod.get("MN"));
            methodTimeNew.setParameters(newMethod.get("PA"));
            methodTimeNew.setTime(commitTime);
            methodTimeNew.setRefactorType(Operator.Move_And_Rename_Method);
            methodTimeNew.setParentCodeBlock(classBlockNew);

            //remove from oldClass
            ClassTime oldClassTimeNew = (ClassTime) classBlockOld.getLastHistory();
            oldClassTimeNew.setTime(commitTime);
            oldClassTimeNew.setRefactorType(Operator.Move_And_Rename_Method);
            assert oldClassTimeNew.getMethods().contains(methodBlock);
            oldClassTimeNew.getMethods().remove(methodBlock);
            //add to newClass
            ClassTime newClassTimeNew = (ClassTime) classBlockNew.getLastHistory();
            newClassTimeNew.setTime(commitTime);
            newClassTimeNew.setRefactorType(Operator.Move_And_Rename_Method);
            newClassTimeNew.getMethods().add(methodBlock);
            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Move_Method {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //find method, oldclass and newclass from mappings, change parentBlock from oldclass to newClass
            String oldClass = r.getFirstClassName();
            String newClass = r.getLastClassName();
            System.out.println(r.getDescription());
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert oldClass.contains(".");
            assert newClass.contains(".");
            assert left.size() == right.size();
            assert left.size() == 1;
            HashMap<String, String> oldMethod = left.get(0).parseMethodDeclaration();//parse the method name
            HashMap<String, String> newMethod = right.get(0).parseMethodDeclaration();
            String oldSig = oldClass + ":" + oldMethod.get("MN");
            String newSig = newClass + ":" + newMethod.get("MN");
            assert mappings.containsKey(oldSig);
            assert mappings.containsKey(oldClass);
            assert mappings.containsKey(newClass);
            CodeBlock methodBlock = mappings.get(oldSig);
            CodeBlock classBlockOld = mappings.get(oldClass);//original class
            CodeBlock classBlockNew = mappings.get(newClass);
            mappings.put(newSig, methodBlock);
            mappings.put(newClass + ":" + oldMethod.get("MN"), methodBlock);
            if (methodBlock.getLastHistory().getParentCodeBlock().equals(classBlockNew)) {
                return;
            }// 如果已经在extractmethod的时候移动过了，就结束本次；如果还没有移动过，就进行迁移
//            assert classBlockOld.equals(methodBlock.getLastHistory().getParentCodeBlock());

            // create new methodTime, update parentBlock; create two classTime for oldClassBlock and newClassBlock, move from oldTime to newTime
            MethodTime methodTimeNew = (MethodTime) methodBlock.getLastHistory();
            methodTimeNew.setName(newMethod.get("MN"));
            methodTimeNew.setParameters(newMethod.get("PA"));
            methodTimeNew.setTime(commitTime);
            methodTimeNew.setRefactorType(Operator.Move_Method);
            methodTimeNew.setParentCodeBlock(classBlockNew);

            //remove from oldClass
            ClassTime oldClassTimeNew = (ClassTime) classBlockOld.getLastHistory();
            oldClassTimeNew.setTime(commitTime);
            oldClassTimeNew.setRefactorType(Operator.Move_Method);
//            assert oldClassTimeNew.getMethods().contains(methodBlock);
            oldClassTimeNew.getMethods().remove(methodBlock);
            //add to newClass
            ClassTime newClassTimeNew = (ClassTime) classBlockNew.getLastHistory();
            newClassTimeNew.setTime(commitTime);
            newClassTimeNew.setRefactorType(Operator.Move_Method);
            newClassTimeNew.getMethods().add(methodBlock);
            refactoredLine(mappings, diffList, r.getLeftSideLocations(), 'L', OpeTypeEnum.M);
            refactoredLine(mappings, diffList, r.getRightSideLocations(), 'R', OpeTypeEnum.M);
            System.out.println(r.getType());
        }
    },
    Change_Return_Type { //trival 只需要修改返回类型 一般不跨文件

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            System.out.println(r.getDescription());
            String className = r.getLastClassName();
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == right.size();
            assert left.size() == 2;
//            System.out.println(commitTime.getCommitID());
//
            HashMap<String, String> oldMethod = left.get(1).parseMethodDeclaration();//parse the method name
            HashMap<String, String> newMethod = right.get(1).parseMethodDeclaration();
            String newSig = className + ":" + newMethod.get("MN");
            assert mappings.containsKey(className + ":" + oldMethod.get("MN"));
            CodeBlock methodBlock = mappings.get(className + ":" + oldMethod.get("MN"));

            MethodTime methodTimeNew = (MethodTime) methodBlock.getLastHistory();
            methodTimeNew.setName(newMethod.get("MN"));
            methodTimeNew.setParameters(newMethod.get("PA"));
            //todo return type
            methodTimeNew.setTime(commitTime);
            methodTimeNew.setRefactorType(Operator.Change_Return_Type);
            mappings.put(newSig, methodBlock);
            refactoredLine(diffList, r.leftFilter("original return type"), 'L');
            refactoredLine(diffList, r.rightFilter("changed return type"), 'R');
            System.out.println(r.getType());
        }
    },
    Rename_Method {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
            System.out.println(r.getDescription());
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == right.size();
            assert left.size() == 1;
//            assert left.get(0).getFilePath().equals(right.get(0).getFilePath());
            HashMap<String, String> oldMethod = left.get(0).parseMethodDeclaration();//parse the method name
            HashMap<String, String> newMethod = right.get(0).parseMethodDeclaration();
            String oldSig = className + ":" + oldMethod.get("MN");
            String newSig = className + ":" + newMethod.get("MN");
            assert mappings.containsKey(oldSig);
            CodeBlock codeBlock = mappings.get(oldSig);
            MethodTime methodTimeNew = (MethodTime) codeBlock.getLastHistory();
            methodTimeNew.setName(newMethod.get("MN"));
            methodTimeNew.setParameters(newMethod.get("PA"));
            methodTimeNew.setTime(commitTime);
            methodTimeNew.setRefactorType(Operator.Rename_Method);
            mappings.put(newSig, codeBlock);
            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Parameterize_Variable {//方法名级别

        @Override
        //把方法中的一个变量 变为方法的参数 不跨文件，仅需要修改方法的名字
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
            System.out.println(r.getDescription());
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == right.size();
            assert left.size() == 2;
//            assert left.get(0).getFilePath().equals(right.get(0).getFilePath());
            HashMap<String, String> oldMethod = left.get(1).parseMethodDeclaration();//parse the method name
            HashMap<String, String> newMethod = right.get(1).parseMethodDeclaration();
            String oldSig = className + ":" + oldMethod.get("MN");
            String newSig = className + ":" + newMethod.get("MN");
            assert mappings.containsKey(oldSig);
            CodeBlock methodBlock = mappings.get(oldSig);
            MethodTime methodTimeNew = (MethodTime) methodBlock.getLastHistory();
            methodTimeNew.setName(newMethod.get("MN"));
            methodTimeNew.setParameters(newMethod.get("PA"));
            //todo return type
            methodTimeNew.setTime(commitTime);
            methodTimeNew.setRefactorType(Operator.Parameterize_Variable);
            mappings.put(newSig, methodBlock);
            refactoredLine(mappings, diffList, r.leftFilter("original variable declaration"), 'L', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, r.rightFilter("renamed variable declaration"), 'R', OpeTypeEnum.R);
            System.out.println(r.getType());
        }
    },
    Merge_Parameter {//把一个方法的参数进行合并，但是可能会有移动 左右两边的最后一个 分别是旧新方法的声明

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            System.out.println(r.getDescription());
//            assert left.get(0).getFilePath().equals(right.get(0).getFilePath());
            HashMap<String, String> oldMethod = left.get(left.size() - 1).parseMethodDeclaration();//parse the method name
            HashMap<String, String> newMethod = right.get(right.size() - 1).parseMethodDeclaration();
            String oldSig = className + ":" + oldMethod.get("MN");
            String newSig = className + ":" + newMethod.get("MN");
            assert mappings.containsKey(oldSig);
            CodeBlock codeBlock = mappings.get(oldSig);
            MethodTime methodTimeNew = (MethodTime) codeBlock.getLastHistory();
            methodTimeNew.setName(newMethod.get("MN"));
            methodTimeNew.setParameters(newMethod.get("PA"));
            methodTimeNew.setTime(commitTime);
            methodTimeNew.setRefactorType(Operator.Merge_Parameter);
            mappings.put(newSig, codeBlock);
            refactoredLine(mappings, diffList, r.leftFilter("merged variable declaration"), 'L', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, r.rightFilter("new variable declaration"), 'R', OpeTypeEnum.R);
            System.out.println(r.getType());
        }
    },
    Split_Parameter {//method name change, parameterList change 左右两边的最后一个分别是旧、新方法的声明

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            System.out.println(r.getDescription());
//            assert left.get(0).getFilePath().equals(right.get(0).getFilePath());
            HashMap<String, String> oldMethod = left.get(left.size() - 1).parseMethodDeclaration();//parse the method name
            HashMap<String, String> newMethod = right.get(right.size() - 1).parseMethodDeclaration();
            String oldSig = className + ":" + oldMethod.get("MN");
            String newSig = className + ":" + newMethod.get("MN");
            assert mappings.containsKey(oldSig);
            CodeBlock codeBlock = mappings.get(oldSig);
            MethodTime methodTimeNew = (MethodTime) codeBlock.getLastHistory();
            methodTimeNew.setName(newMethod.get("MN"));
            methodTimeNew.setParameters(newMethod.get("PA"));
            methodTimeNew.setTime(commitTime);
            methodTimeNew.setRefactorType(Operator.Split_Parameter);
            mappings.put(newSig, codeBlock);
            refactoredLine(mappings, diffList, r.leftFilter("original variable declaration"), 'L', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, r.rightFilter("split variable declaration"), 'R', OpeTypeEnum.R);
            System.out.println(r.getType());
        }
    },
    Change_Parameter_Type {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
            System.out.println(r.getDescription());
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert right.size() == 2;
            assert left.size() == 2;
//            assert left.get(0).getFilePath().equals(right.get(0).getFilePath());
            HashMap<String, String> oldMethod = left.get(1).parseMethodDeclaration();
            HashMap<String, String> newMethod = right.get(1).parseMethodDeclaration();
            System.out.println(className + ":" + oldMethod.get("MN"));
            assert mappings.containsKey(className + ":" + oldMethod.get("MN"));
            CodeBlock codeBlock = mappings.get(className + ":" + oldMethod.get("MN"));
            mappings.put(className + ":" + newMethod.get("MN"), codeBlock);
            MethodTime methodTime = (MethodTime) codeBlock.getLastHistory();
            methodTime.setName(newMethod.get("MN"));
            methodTime.setTime(commitTime);
            methodTime.setRefactorType(Operator.Change_Parameter_Type);
            methodTime.setParameters(newMethod.get("PA"));//update parameterType
//            todo parameterType
            refactoredLine(mappings, diffList, r.leftFilter("original variable declaration"), 'L', OpeTypeEnum.R);
            refactoredLine(mappings, diffList, r.rightFilter("changed-type variable declaration"), 'R', OpeTypeEnum.R);
            System.out.println(r.getType());
        }
    },
    Add_Parameter {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            System.out.println(r.getDescription());
            assert right.size() == 2;
            assert left.size() == 1;
//            assert left.get(0).getFilePath().equals(right.get(0).getFilePath());

            HashMap<String, String> oldMethod = left.get(0).parseMethodDeclaration();
            HashMap<String, String> newMethod = right.get(1).parseMethodDeclaration();

//            System.out.println(commitTime.getCommitID());
//            System.out.println(r.getDescription());
//            System.out.println(className + ":" + oldMethod.get("MN"));
            assert mappings.containsKey(className + ":" + oldMethod.get("MN"));
            CodeBlock codeBlock = mappings.get(className + ":" + newMethod.get("MN"));
            MethodTime methodTime = (MethodTime) codeBlock.getLastHistory();
            methodTime.setName(newMethod.get("MN"));
            methodTime.setTime(commitTime);
            methodTime.setRefactorType(Operator.Add_Parameter);
            methodTime.setParameters(newMethod.get("PA"));//update parameterType//todo return type
            refactorFirstLine(r, mappings, diffList, r.leftFilter("original method declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("added parameter"), 'R');
            System.out.println(r.getType());
        }
    },
    Remove_Parameter {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
            System.out.println(r.getDescription());
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert right.size() == 1;
            assert left.size() == 2;
//            assert left.get(0).getFilePath().equals(right.get(0).getFilePath());
            HashMap<String, String> oldMethod = left.get(1).parseMethodDeclaration();
            HashMap<String, String> newMethod = right.get(0).parseMethodDeclaration();
            assert mappings.containsKey(className);
            CodeBlock codeBlock = mappings.get(className + ":" + oldMethod.get("MN"));
            assert codeBlock != null;
            mappings.put(className + ":" + newMethod.get("MN"), codeBlock);

            MethodTime methodTime = (MethodTime) codeBlock.getLastHistory();
            methodTime.setName(newMethod.get("MN"));
            methodTime.setTime(commitTime);
            methodTime.setRefactorType(Operator.Remove_Parameter);
            methodTime.setParameters(newMethod.get("PA"));//update parameterType
            refactoredLine(diffList, r.leftFilter("removed parameter"), 'L');
            refactorFirstLine(r, mappings, diffList, r.leftFilter("method declaration with removed parameter"), 'R');
            System.out.println(r.getType());
        }
    },
    Reorder_Parameter {// only change method name & method parameterList, parameterType 左边和右边的最后一个分别是旧、新方法的声明

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
            System.out.println(r.getDescription());
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
//            assert left.get(0).getFilePath().equals(right.get(0).getFilePath());
            HashMap<String, String> oldMethod = left.get(left.size() - 1).parseMethodDeclaration();//parse the method name
            HashMap<String, String> newMethod = right.get(right.size() - 1).parseMethodDeclaration();
            String oldSig = className + ":" + oldMethod.get("MN");
            String newSig = className + ":" + newMethod.get("MN");
            assert mappings.containsKey(oldSig);
            CodeBlock codeBlock = mappings.get(oldSig);
            MethodTime methodTimeNew = (MethodTime) codeBlock.getLastHistory();
            methodTimeNew.setName(newMethod.get("MN"));
            methodTimeNew.setParameters(newMethod.get("PA"));
            methodTimeNew.setTime(commitTime);
            methodTimeNew.setRefactorType(Operator.Reorder_Parameter);
            mappings.put(newSig, codeBlock);
            refactoredLine(diffList, r.leftFilter("original parameter declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("reordered parameter declaration"), 'R');
            System.out.println(r.getType());
        }
    },
    Parameterize_Attribute {//把一个attribute变成一个参数，同时修改属性和方法的参数 一般不跨项目

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert right.size() == 2;
            assert left.size() == 2;
            String oldAttri = left.get(0).parseAttributeOrParameter();
            String newAttri = right.get(0).parseAttributeOrParameter();
            HashMap<String, String> oldMethod = left.get(1).parseMethodDeclaration();
            HashMap<String, String> newMethod = right.get(1).parseMethodDeclaration();

            //update attributeTime
//            System.out.println(r.getDescription());
            assert mappings.containsKey(className + ":" + oldAttri);
            mappings.put(className + ":" + newAttri, mappings.get(className + ":" + oldAttri));// update signature
            AttributeTime attributeTime = (AttributeTime) mappings.get(className + ":" + oldAttri).getLastHistory();//create new attributeTimeBlock
            attributeTime.setName(newAttri);
            attributeTime.setTime(commitTime);
            attributeTime.setRefactorType(Operator.Parameterize_Attribute);

            //update method
            assert mappings.containsKey(className + ":" + oldMethod.get("MN"));
            CodeBlock methodBlock = mappings.get(className + ":" + oldMethod.get("MN"));
            MethodTime methodTimeNew = (MethodTime) methodBlock.getLastHistory();
            methodTimeNew.setName(newMethod.get("MN"));
            methodTimeNew.setParameters(newMethod.get("PA"));
            methodTimeNew.setTime(commitTime);
            methodTimeNew.setRefactorType(Operator.Parameterize_Attribute);
            mappings.put(className + ":" + newMethod.get("MN"), methodBlock);

            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("renamed variable declaration"), 'R');
            System.out.println(r.getType());
        }
    },
    Pull_Up_Attribute {//把子类中的属性 移到父类中 跨文件

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String oldClass = r.getFirstClassName();
            String newClass = r.getLastClassName();
            assert r.getRightSideLocations().size() == 1;
            String oldAttri = r.getLeftSideLocations().get(0).parseAttributeOrParameter();//parse the attribute name
            String newAttri = r.getRightSideLocations().get(0).parseAttributeOrParameter();

            assert mappings.containsKey(oldClass + ":" + oldAttri);
            assert mappings.containsKey(oldClass);
            CodeBlock attriBlock = mappings.get(oldClass + ":" + oldAttri);
            CodeBlock classBlockOld = mappings.get(oldClass);//original class

//            assert classBlockOld.equals(attriBlock.getLastHistory().getParentCodeBlock());

            //if newClass doesn't exist, create; otherwise find from mappings
            CodeBlock classBlockNew; //new class, find from mappings or create
            classBlockNew = mappings.get(newClass);
            // create new attributeTime, update parentBlock; create two classTime for oldClassBlock and newClassBlock, move from oldTime to newTime
            AttributeTime attriTime = (AttributeTime) attriBlock.getLastHistory();
            attriTime.setName(newAttri);
            attriTime.setTime(commitTime);
            attriTime.setRefactorType(Operator.Pull_Up_Attribute);
            attriTime.setParentCodeBlock(classBlockNew);
            mappings.put(newClass + ":" + oldAttri, attriBlock);
            mappings.put(newClass + ":" + newAttri, attriBlock);

            //remove from oldClass
            ClassTime oldClassTimeNew = (ClassTime) classBlockOld.getLastHistory();
            oldClassTimeNew.setTime(commitTime);
            oldClassTimeNew.setRefactorType(Operator.Pull_Up_Attribute);
//            assert oldClassTimeNew.getAttributes().contains(attriBlock);
            oldClassTimeNew.getAttributes().remove(attriBlock);
            //add to newClass
            ClassTime newClassTimeNew = (ClassTime) classBlockNew.getLastHistory();
            newClassTimeNew.setTime(commitTime);
            newClassTimeNew.setRefactorType(Operator.Pull_Up_Attribute);
            newClassTimeNew.getAttributes().add(attriBlock);

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Push_Down_Attribute {// move attribute from father class to son class, normally cross class files. ≈move attribute

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String oldClass = r.getFirstClassName();
            String newClass = r.getLastClassName();
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            System.out.println(r.getDescription());
            assert left.size() == right.size();
            assert left.size() == 1;
            String oldAttri = left.get(0).parseAttributeOrParameter();//parse the attribute name
            String newAttri = right.get(0).parseAttributeOrParameter();
            String oldSig = oldClass + ":" + oldAttri;
            String newSig = newClass + ":" + newAttri;
            assert mappings.containsKey(oldSig);
            assert mappings.containsKey(oldClass);
            assert mappings.containsKey(newClass);
            CodeBlock attriBlock = mappings.get(oldSig);
            CodeBlock classBlockOld = mappings.get(oldClass);//original class
            CodeBlock classBlockNew = mappings.get(newClass);
            mappings.put(newSig, attriBlock);
            mappings.put(newClass + ":" + oldAttri, attriBlock);
            if (attriBlock.getLastHistory().getParentCodeBlock().equals(classBlockNew)) {
                return;
            }// 如果已经在前边移动过了，就结束本次；如果还没有移动过，就进行迁移
//            assert classBlockOld.equals(attriBlock.getLastHistory().getParentCodeBlock());

            // create new attributeTime, update parentBlock; create two classTime for oldClassBlock and newClassBlock, move from oldTime to newTime
            AttributeTime attriTime = (AttributeTime) attriBlock.getLastHistory();
            attriTime.setName(newAttri);
            attriTime.setTime(commitTime);
            attriTime.setRefactorType(Operator.Push_Down_Method);
            attriTime.setParentCodeBlock(classBlockNew);

            //remove from oldClass
            ClassTime oldClassTimeNew = (ClassTime) classBlockOld.getLastHistory();
            oldClassTimeNew.setTime(commitTime);
            oldClassTimeNew.setRefactorType(Operator.Push_Down_Method);
            assert oldClassTimeNew.getAttributes().contains(attriBlock);
            oldClassTimeNew.getAttributes().remove(attriBlock);
            //add to newClass
            ClassTime newClassTimeNew = (ClassTime) classBlockNew.getLastHistory();
            newClassTimeNew.setTime(commitTime);
            newClassTimeNew.setRefactorType(Operator.Push_Down_Method);
            newClassTimeNew.getAttributes().add(attriBlock);

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Move_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String oldClass = r.getFirstClassName();
            String newClass = r.getLastClassName();
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            System.out.println(r.getDescription());
            assert left.size() == right.size();
            assert left.size() == 1;
            String oldAttri = left.get(0).parseAttributeOrParameter();//parse the method name
            String newAttri = right.get(0).parseAttributeOrParameter();
            String oldSig = oldClass + ":" + oldAttri;
            String newSig = newClass + ":" + newAttri;
            assert mappings.containsKey(oldSig);
            assert mappings.containsKey(oldClass);
            assert mappings.containsKey(newClass);
            CodeBlock attriBlock = mappings.get(oldSig);
            CodeBlock classBlockOld = mappings.get(oldClass);//original class
            CodeBlock classBlockNew = mappings.get(newClass);
            mappings.put(newClass + ":" + oldAttri, attriBlock);
            mappings.put(newSig, attriBlock);
            if (attriBlock.getLastHistory().getParentCodeBlock().equals(classBlockNew)) {
                return;
            }// 如果已经在前边移动过了，就结束本次；如果还没有移动过，就进行迁移
//            assert classBlockOld.equals(attriBlock.getLastHistory().getParentCodeBlock());

            // create new attributeTime, update parentBlock; create two classTime for oldClassBlock and newClassBlock, move from oldTime to newTime
            AttributeTime attriTime = (AttributeTime) attriBlock.getLastHistory();
            attriTime.setName(newAttri);
            attriTime.setTime(commitTime);
            attriTime.setRefactorType(Operator.Move_Attribute);
            attriTime.setParentCodeBlock(classBlockNew);

            //remove from oldClass
            ClassTime oldClassTimeNew = (ClassTime) classBlockOld.getLastHistory();
            oldClassTimeNew.setTime(commitTime);
            oldClassTimeNew.setRefactorType(Operator.Move_Attribute);
//            assert oldClassTimeNew.getAttributes().contains(attriBlock);
            oldClassTimeNew.getAttributes().remove(attriBlock);
            //add to newClass
            ClassTime newClassTimeNew = (ClassTime) classBlockNew.getLastHistory();
            newClassTimeNew.setTime(commitTime);
            newClassTimeNew.setRefactorType(Operator.Move_Attribute);
            newClassTimeNew.getAttributes().add(attriBlock);

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Rename_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
            System.out.println(r.getDescription());
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == right.size();
            assert left.size() == 1;
//            assert left.get(0).getFilePath().equals(right.get(0).getFilePath());
            String oldName = left.get(0).parseAttributeOrParameter();
            String newName = right.get(0).parseAttributeOrParameter();
            String returnType = newName.substring(0, newName.indexOf("_"));//todo 返回值类型
            assert mappings.containsKey(className + ":" + oldName);
            CodeBlock attriBlock = mappings.get(className + ":" + oldName);
            mappings.put(className + ":" + newName, attriBlock);// update signature
            AttributeTime attributeTime = (AttributeTime) attriBlock.getLastHistory();//create new attributeTimeBlock
            mappings.put(attributeTime.getParentCodeBlock().getLastHistory().getSignature() + ":" + newName, attriBlock);
            attributeTime.setName(newName);
            attributeTime.setTime(commitTime);
            attributeTime.setRefactorType(Operator.Rename_Attribute);

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Merge_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //create new attribute(update class.attribute), derive from oldAttribute A, B, C, etc.
            String className = r.getLastClassName();
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            System.out.println(r.getDescription());
            assert right.size() == 1;
            String attriNameNew = right.get(0).parseAttributeOrParameter();

            //create new codeBlock
            assert mappings.containsKey(className);
            CodeBlock attriBlock = mappings.get(className + ":" + attriNameNew);
            AttributeTime attributeTime = (AttributeTime) attriBlock.getLastHistory();
            attributeTime.setRefactorType(Merge_Attribute);

            //derive from old attributes
            for (int i = 0; i < left.size(); i++) {
                String oldAttriName = left.get(i).parseAttributeOrParameter();
                assert mappings.containsKey(className + ":" + oldAttriName);
                CodeBlock oldAttriBlock = mappings.get(className + ":" + oldAttriName);
                AttributeTime attributeTimeOld = (AttributeTime) oldAttriBlock.getLastHistory();
                attributeTimeOld.setTime(commitTime);
                attributeTimeOld.setRefactorType(Operator.Merge_Attribute);
                attributeTimeOld.getDerivee().add(attributeTime);
                attributeTime.getDeriver().add(attributeTimeOld);
            }
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Split_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == 1;
            System.out.println(r.getDescription());
//            assert left.get(0).getFilePath().equals(right.get(0).getFilePath());//假设文件没有变化 如果有变化 就需要把classBlock的更新换到for循环里边进行
            String oldAttriName = left.get(0).parseAttributeOrParameter();

            //create new attributeTime
            assert mappings.containsKey(className);
            assert mappings.containsKey(className + ":" + oldAttriName);
            CodeBlock oldAttriBlock = mappings.get(className + ":" + oldAttriName);
            CodeBlock classBlock = mappings.get(className);

            AttributeTime oldAttriTime = (AttributeTime) oldAttriBlock.getLastHistory();
            oldAttriTime.setTime(commitTime);
            oldAttriTime.setRefactorType(Operator.Split_Attribute);

            //new derivee attributes
            for (int i = 0; i < right.size(); i++) {
                String newAttriName = right.get(i).parseAttributeOrParameter();
                CodeBlock attriBlock = mappings.get(className + ":" + newAttriName);
                AttributeTime attriTime = (AttributeTime) attriBlock.getLastHistory();
                attriTime.setRefactorType(Split_Attribute);

                attriTime.getDeriver().add(oldAttriTime);
                oldAttriTime.getDerivee().add(attriTime);
            }

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Change_Attribute_Type {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert left.size() == right.size();
            assert left.size() == 1;
            String oldName = left.get(0).parseAttributeOrParameter();
            String newName = right.get(0).parseAttributeOrParameter();
            String returnType = newName.substring(0, newName.indexOf("_"));//todo 返回值类型
            System.out.println(r.getDescription());
            System.out.println(className + ":" + oldName);
//            mappings.get(className).getHistory().forEach(e->System.out.println("Class: "+e.getRefactorType()+":::"+e.getSignature()));
//            mappings.get(className).getLastHistory().getAttributes().forEach(e->e.getHistory().forEach(x-> System.out.println("attribute: "+x.getRefactorType()+":::"+x.getSignature())));
            assert mappings.containsKey(className + ":" + oldName);
            mappings.put(className + ":" + newName, mappings.get(className + ":" + oldName));// update signature
            AttributeTime attributeTime = (AttributeTime) mappings.get(className + ":" + oldName).getLastHistory();//create new attributeTimeBlock
            attributeTime.setName(newName);
            attributeTime.setTime(commitTime);
            attributeTime.setRefactorType(Operator.Change_Attribute_Type);
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Extract_Attribute {//涉及增加新的attribute

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String className = r.getLastClassName();
            System.out.println(r.getDescription());
//            assert r.getLeftSideLocations().get(0).getFilePath().equals(r.getRightSideLocations().get(0).getFilePath());
            assert mappings.containsKey(className);
            CodeBlock classBlock = mappings.get(className);
            String attriName = r.getRightSideLocations().get(0).parseAttributeOrParameter();
            assert !mappings.containsKey(className + ":" + attriName);
            CodeBlock attriBlock = mappings.get(className + ":" + attriName);;
            AttributeTime attributeTime = (AttributeTime) attriBlock.getLastHistory();
            attributeTime.setRefactorType(Operator.Extract_Attribute);
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Encapsulate_Attribute {//应该是只增加了一个get方法,同时可能也增加了set方法

        @Override
        //Attribute encapsulation is useful when you have an attribute that is affected by several different methods,
        // each of which needs that attribute to be in a known state. To prevent programmers from changing the attribute
        // in the 4GL code, you can make the attribute private so that programmers can only access it from the object's methods.
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //add new method
            String className = r.getLastClassName();
            assert r.getRightSideLocations().size() < 4;
            assert mappings.containsKey(className);
            CodeBlock classBlock = mappings.get(className);
            ClassTime classTime = (ClassTime) classBlock.getLastHistory();
            classTime.setTime(commitTime);
            classTime.setRefactorType(Operator.Encapsulate_Attribute);

            for (int i = 1; i < r.getRightSideLocations().size(); i++) {
                HashMap<String, String> info = r.getRightSideLocations().get(i).parseMethodDeclaration();
                CodeBlock methodBlock = mappings.get(className + ":" + info.get("MN"));
                MethodTime methodTime = (MethodTime) methodBlock.getLastHistory();
                methodTime.setRefactorType(Operator.Encapsulate_Attribute);
            }
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Inline_Attribute {//remove_attribute, 去掉属性，直接使用属性的值,从旧的类中移除

        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //remove from oldClass
            String className = r.getLastClassName();
            String attriName = r.getLeftSideLocations().get(0).parseAttributeOrParameter();
            assert mappings.containsKey(className);
            assert mappings.containsKey(className + ":" + attriName);
            CodeBlock classBlock = mappings.get(className);
            CodeBlock attriBlock = mappings.get(className + ":" + attriName);
            //remove from class
            ClassTime classTime = (ClassTime) classBlock.getLastHistory();
            classTime.setTime(commitTime);
            classTime.setRefactorType(Operator.Inline_Attribute);
            classTime.getAttributes().remove(attriBlock);
            // add new attributeTime
            AttributeTime attributeTime = (AttributeTime) attriBlock.getLastHistory();
            attributeTime.setTime(commitTime);
            attributeTime.setRefactorType(Operator.Inline_Attribute);

            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Move_And_Rename_Attribute {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            String oldClass = r.getFirstClassName();
            String newClass = r.getLastClassName();
            List<SideLocation> left = r.getLeftSideLocations();
            List<SideLocation> right = r.getRightSideLocations();
            assert oldClass.contains(".");
            assert newClass.contains(".");
            assert left.size() == right.size();
            assert left.size() == 1;
            String oldAttri = left.get(0).parseAttributeOrParameter();//parse the method name
            String newAttri = right.get(0).parseAttributeOrParameter();
            String oldSig = oldClass + ":" + oldAttri;
            String newSig = newClass + ":" + newAttri;
            assert mappings.containsKey(oldSig);
            assert mappings.containsKey(oldClass);
            assert mappings.containsKey(newClass);
            CodeBlock attriBlock = mappings.get(oldSig);
            CodeBlock classBlockOld = mappings.get(oldClass);//original class
            CodeBlock classBlockNew = mappings.get(newClass);
            mappings.put(newSig, attriBlock);
            mappings.put(newClass + ":" + oldAttri, attriBlock);
            if (attriBlock.getLastHistory().getParentCodeBlock().equals(classBlockNew)) {
                return;
            }// 如果已经在前边移动过了，就结束本次；如果还没有移动过，就进行迁移
            assert classBlockOld.equals(attriBlock.getLastHistory().getParentCodeBlock());

            // create new attributeTime, update parentBlock; create two classTime for oldClassBlock and newClassBlock, move from oldTime to newTime
            AttributeTime attriTime = (AttributeTime) attriBlock.getLastHistory();
            attriTime.setName(newAttri);
            attriTime.setTime(commitTime);
            attriTime.setRefactorType(Operator.Move_Attribute);
            attriTime.setParentCodeBlock(classBlockNew);

            //remove from oldClass
            ClassTime oldClassTimeNew = (ClassTime) classBlockOld.getLastHistory();
            oldClassTimeNew.setTime(commitTime);
            oldClassTimeNew.setRefactorType(Operator.Move_Attribute);
//            assert oldClassTimeNew.getAttributes().contains(attriBlock);
            oldClassTimeNew.getAttributes().remove(attriBlock);
            //add to newClass
            ClassTime newClassTimeNew = (ClassTime) classBlockNew.getLastHistory();
            newClassTimeNew.setTime(commitTime);
            newClassTimeNew.setRefactorType(Operator.Move_Attribute);
            newClassTimeNew.getAttributes().add(attriBlock);
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Replace_Attribute {
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
            System.out.println(r.getType());
        }
    },
    Replace_Attribute_With_Variable {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //不太确定到底是啥 应该是涉及attribute的弃用 （但是有时候不删除attribute） 影响不大
            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("original variable declaration"), 'R');
            System.out.println(r.getType());
        }
    },
    Replace_Anonymous_With_Lambda {
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.leftFilter("anonymous class declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("lambda expression"), 'R');
            System.out.println(r.getType());
        }
    },//TODO 暂时没找到

    //todo 欠缺的添加
    Add_Class_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> right = r.rightFilter("added annotation");
            refactoredLine(diffList, right, 'R');
        }
    },
    Remove_Class_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("removed annotation");
            refactoredLine(diffList, left, 'L');
        }
    },
    Modify_Class_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("original annotation");
            List<SideLocation> right = r.rightFilter("modified annotation");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },

    Add_Class_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Remove_Class_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Change_Class_Access_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Add_Method_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> right = r.rightFilter("added annotation");
            refactoredLine(diffList, right, 'R');
        }
    },
    Remove_Method_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("removed annotation");
            refactoredLine(diffList, left, 'L');
        }
    },
    Modify_Method_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("original annotation");
            List<SideLocation> right = r.rightFilter("modified annotation");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },
    Add_Method_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Remove_Method_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Change_Method_Access_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactorFirstLine(r, mappings, diffList, r.getLeftSideLocations(), 'L');
            refactorFirstLine(r, mappings, diffList, r.getRightSideLocations(), 'R');
        }
    },
    Add_Thrown_Exception_Type{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> right = r.rightFilter("added thrown exception type");
            refactoredLine(diffList, right, 'R');
            refactorFirstLine(r, mappings, diffList, r.leftFilter("original method declaration"), 'L');
            refactorFirstLine(r, mappings, diffList, r.rightFilter("method declaration with added thrown exception type"), 'R');
        }
    },
    Remove_Thrown_Exception_Type{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.leftFilter("removed thrown exception type"), 'L');
            refactorFirstLine(r, mappings, diffList, r.leftFilter("original method declaration"), 'L');
            refactorFirstLine(r, mappings, diffList, r.rightFilter("method declaration with removed thrown exception type"), 'R');
        }
    },
    Change_Thrown_Exception_Type{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.leftFilter("original exception type"), 'L');
            refactoredLine(diffList, r.rightFilter("changed exception type"), 'R');
        }
    },
    Rename_Parameter{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("renamed variable declaration"), 'R');
        }
    },
    Add_Parameter_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("variable declaration with added annotation"), 'R');
            refactoredLine(diffList, r.rightFilter("added annotation"), 'R');
        }
    },
    Remove_Parameter_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.leftFilter("removed annotation"), 'L');
            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("variable declaration with removed annotation"), 'R');
        }
    },
    Modify_Parameter_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            //todo no example
        }
    },
    Add_Parameter_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("variable declaration with added modifier"), 'R');
        }
    },
    Remove_Parameter_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("variable declaration with removed modifier"), 'R');
        }
    },
    Localize_Parameter{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("renamed variable declaration"), 'R');
        }
    },
    Add_Attribute_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Remove_Attribute_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Modify_Attribute_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Add_Attribute_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Remove_Attribute_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },
    Change_Attribute_Access_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.getLeftSideLocations(), 'L');
            refactoredLine(diffList, r.getRightSideLocations(), 'R');
        }
    },

    Extract_Variable{
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList){
            List<SideLocation> left = r.leftFilter("statement with the initializer of the extracted variable");
            List<SideLocation> right = r.rightFilter("extracted variable declaration");
            List<SideLocation> right2 = r.rightFilter("statement with the name of the extracted variable");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
            refactoredLine(diffList, right2, 'R');

        }
    },
    Change_Variable_Type{
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("changed-type variable declaration");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },
    Inline_Variable{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("inlined variable declaration");
            List<SideLocation> left2 = r.leftFilter("statement with the name of the inlined variable");
            List<SideLocation> right = r.rightFilter("statement with the initializer of the inlined variable");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, left2, 'L');
            refactoredLine(diffList, right, 'R');

        }
    },
    Rename_Variable{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("renamed variable declaration");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },
    Merge_Variable{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("merged variable declaration");
            List<SideLocation> right = r.rightFilter("new variable declaration");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');

        }
    },
    Split_Variable{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("split variable declaration");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },
    Add_Variable_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("added annotation");
            List<SideLocation> right2 = r.rightFilter("variable declaration with added annotation");
            assert right2!=null;
            refactoredLine(diffList,left, 'L');
            refactoredLine(diffList, right, 'R');
            refactoredLine(diffList, right2, 'R');
        }
    },
    Remove_Variable_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("removed annotation");
            List<SideLocation> left2 = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("variable declaration with removed annotation");
            refactoredLine(diffList,left, 'L');
            refactoredLine(diffList, right, 'R');
            refactoredLine(diffList, left2, 'L');
        }
    },
    Modify_Variable_Annotation{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("original annotation");
            List<SideLocation> left2 = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("modified annotation");
            List<SideLocation> right2 = r.rightFilter("variable declaration with modified annotation");
            refactoredLine(diffList,left, 'L');
            refactoredLine(diffList,left2, 'L');
            refactoredLine(diffList, right, 'R');
            refactoredLine(diffList, right2, 'R');
        }
    },
    Add_Variable_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("variable declaration with added modifier");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },
    Remove_Variable_Modifier{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("original variable declaration");
            List<SideLocation> right = r.rightFilter("variable declaration with removed modifier");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    },
    Replace_Variable_With_Attribute{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            refactoredLine(diffList, r.leftFilter("original variable declaration"), 'L');
            refactoredLine(diffList, r.rightFilter("renamed variable declaration"), 'R');
        }
    },
    Replace_Loop_With_Pipeline{
        @Override
        public void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList) {
            List<SideLocation> left = r.leftFilter("original code");
            List<SideLocation> right = r.rightFilter("pipeline code");
            refactoredLine(diffList, left, 'L');
            refactoredLine(diffList, right, 'R');
        }
    }
    ;
    public abstract void apply(List<CodeBlock> codeBlocks, HashMap<String, CodeBlock> mappings, Refactoring r, CommitCodeChange commitTime, String name, List<DiffFile> diffList);

}
