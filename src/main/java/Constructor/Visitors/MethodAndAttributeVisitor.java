package Constructor.Visitors;

import Constructor.Enums.CodeBlockType;
import Constructor.Enums.Operator;
import Model.AttributeTime;
import Model.CodeBlock;
import Model.CommitCodeChange;
import Model.MethodTime;
import gr.uom.java.xmi.*;

import java.util.*;
import java.util.stream.Collectors;

public class MethodAndAttributeVisitor {
    private List<CodeBlock> codeBlocks;
    private HashMap<String, CodeBlock> mappings;
    private CommitCodeChange commitCodeChange;

    public void methodAAttributeVisitor(Map<String, String> javaFileContents, Set<String> repositoryDirectories, List<CodeBlock> codeBlocks, List<CommitCodeChange> codeChange, HashMap<String, CodeBlock> mappings){
        this.codeBlocks = codeBlocks;
        this.mappings = mappings;
        this.commitCodeChange = codeChange.get(codeChange.size() - 1); //获得当前commit的内容

        //调用RefactoringMiner的ASTReader进行分析
        UMLModel model = (new ASTReader(javaFileContents, repositoryDirectories)).getUmlModel();


        for(UMLClass umlClass: model.getClassList()){
            for(UMLOperation umlOperation : umlClass.getOperations()){
                methodVisitor(umlOperation, commitCodeChange, umlClass.getName());
            }
            for(UMLAttribute umlAttribute: umlClass.getAttributes()){
                attributeVisitor(umlAttribute, commitCodeChange, umlClass.getName());
            }
        }
    }

    private void methodVisitor(UMLOperation umlOperation, CommitCodeChange commitTime, String signature) {
        //根据operation生成方法的名称与方法参数类型列表
        StringBuilder sb = new StringBuilder();
        StringBuilder parameterTypes = new StringBuilder();

        UMLParameter returnParameter = umlOperation.getReturnParameter();
        if (returnParameter != null) {
            sb.append(returnParameter).append("_");
        }

        sb.append(umlOperation.getName());

        List<UMLParameter> parameters = new ArrayList(umlOperation.getParameters());
        parameters.remove(returnParameter);
        sb.append("(");

        for(int i = 0; i < parameters.size(); ++i) {
            UMLParameter parameter = parameters.get(i);
            if (parameter.getKind().equals("in")) {
                String parameterStr = parameter.toString();
                parameterTypes.append(parameterStr.substring(parameterStr.indexOf(" ")+1));
                if (i < parameters.size() - 1) {
                    parameterTypes.append(", ");
                }
            }
        }

        sb.append(parameterTypes);
        sb.append(")");

        String methodName = sb.toString();
        String signature_method = signature + ":" + methodName;

        if (!mappings.containsKey(signature_method)) {
            CodeBlock codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Method);
            mappings.put(signature_method, codeBlock);
            codeBlocks.add(codeBlock);
            String className = umlOperation.getClassName();
            CodeBlock classBlock = mappings.get(className);
            if(classBlock == null){
                mappings.keySet().stream().filter(p -> p.contains("JavaExtractorTestOne"))
                        .forEach(System.out::println);
                System.out.println();
                System.out.println(signature_method);
            }
            MethodTime methodTime = new MethodTime(methodName, commitTime, Operator.Add_Method, codeBlock, classBlock, parameterTypes.toString());
        }
    }

    private void attributeVisitor(UMLAttribute umlAttribute, CommitCodeChange commitTime, String signature){
        String attributeName = umlAttribute.getType() + "_" + umlAttribute.getName();
        String signature_attribute = signature + ":" + attributeName;

        if (!mappings.containsKey(signature_attribute)) {
            CodeBlock codeBlock = new CodeBlock(codeBlocks.size() + 1, CodeBlockType.Attribute);
            mappings.put(signature_attribute, codeBlock);
            codeBlocks.add(codeBlock);
            String className = umlAttribute.getClassName();
            CodeBlock classBlock = mappings.get(className);
            AttributeTime attriTime = new AttributeTime(attributeName, commitTime, Operator.Add_Attribute, codeBlock, classBlock);
        }
    }
}
