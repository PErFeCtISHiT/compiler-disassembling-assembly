import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author: pis
 * @description: 接收正则表达式转换为DFA状态集
 * @date: create in 9:39 2018/10/27
 */
class Lex {
    static Set<Character> letters = new HashSet<>();//标识符表

    Lex() {
        for (char c = 'a'; c <= 'c'; c++) {
            letters.add(c);
        }
        letters.add('#');
    }

    Set<DFAState> getDFAStates(String path) {


        File REFile = new File(path);
        StringBuilder REStr = new StringBuilder();
        try (FileReader fileReader = new FileReader(REFile);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String str = bufferedReader.readLine();
            while (str != null && str.length() != 0) {
                REStr.append('(').append(str).append(')').append("|");
                str = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (REStr.charAt(REStr.length() - 1) == '|') {
            REStr.deleteCharAt(REStr.length() - 1);
        }
        String suffix = infixToSuffix(REStr.toString());//后缀表达式
        if (suffix == null) {
            System.err.println("正则表达式含有非法字符");
            System.exit(-1);
            return null;
        }
        RETreeNode root = createRETree(suffix);//正则分析树

        Set<DFAState> dfaStates = createDFAState(root);//得到DFA状态集
        dfaStates = minDFA(dfaStates);//得到最少状态的DFA
        System.out.println("DFA states");
        int id = 1;
        for (DFAState dfaState : dfaStates) {
            dfaState.id = id;
            id++;
        }
        for (DFAState dfaState : dfaStates) {
            System.out.println("id: " + dfaState.id);
            System.out.println("type: " + dfaState.attri);
            System.out.println("turn :");
            for (char ch : letters) {
                DFAState state = dfaState.DFATurn.get(ch);
                System.out.print(ch + ": ");
                if (state != null)
                    System.out.println(state.id);
                else
                    System.out.println();

            }
            System.out.println();
        }
        System.out.println();
        return dfaStates;

    }

    /**
     * @param str 中缀表达式
     * @return 后缀表达式
     */
    private static String infixToSuffix(String str) {
        StringBuilder ret = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isLetterOrDigit(ch) || ch == '#') {
                ret.append(ch);
            } else if (ch == '(') {
                stack.push(ch);
            } else if (ch == '|') {
                if (!stack.isEmpty()) {
                    while (!stack.isEmpty() && stack.peek() != '(') {
                        ret.append(stack.pop());
                    }
                }
                stack.push(ch);
            } else if (ch == '&') {
                if (!stack.isEmpty()) {
                    while (!stack.isEmpty() && stack.peek() != '|' && stack.peek() != '(') {
                        ret.append(stack.pop());
                    }
                }
                stack.push(ch);
            } else if (ch == '*') {
                if (!stack.isEmpty()) {
                    while (!stack.isEmpty() && (stack.peek() != '|' && stack.peek() != '&' && stack.peek() != '(')) {
                        ret.append(stack.pop());
                    }
                }
                stack.push(ch);
            } else if (ch == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    ret.append(stack.pop());
                }
                if (!stack.isEmpty())
                    stack.pop();
            } else
                return null;
        }
        while (!stack.isEmpty()) {
            ret.append(stack.pop());
        }
        return ret.toString();
    }

    /**
     * @param str 后缀表达式
     * @return 正则语法树
     */
    private static RETreeNode createRETree(String str) {
        Stack<RETreeNode> reTreeNodeStack = new Stack<>();
        int tempPos = 1;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            Set<RETreeNode> firstPos = new HashSet<>();
            Set<RETreeNode> lastPos = new HashSet<>();
            if (Character.isLetterOrDigit(ch) || ch == '#') {
                RETreeNode treeNode = new RETreeNode(NodeType.SYNC, ch);
                treeNode.nullable = false;
                firstPos.add(treeNode);
                treeNode.firstPos = firstPos;
                lastPos.add(treeNode);
                treeNode.lastPos = lastPos;
                treeNode.pos = tempPos;
                tempPos++;
                reTreeNodeStack.push(treeNode);
            } else if (ch == '|') {
                RETreeNode treeNode = new RETreeNode(NodeType.OR, ch);
                treeNode.right = reTreeNodeStack.pop();
                treeNode.left = reTreeNodeStack.pop();
                treeNode.nullable = treeNode.left.nullable || treeNode.right.nullable;
                firstPos.addAll(treeNode.left.firstPos);
                firstPos.addAll(treeNode.right.firstPos);
                treeNode.firstPos = firstPos;
                lastPos.addAll(treeNode.left.lastPos);
                lastPos.addAll(treeNode.right.lastPos);
                treeNode.lastPos = lastPos;
                reTreeNodeStack.push(treeNode);
            } else if (ch == '&') {
                RETreeNode treeNode = new RETreeNode(NodeType.CAT, ch);
                treeNode.right = reTreeNodeStack.pop();
                treeNode.left = reTreeNodeStack.pop();
                treeNode.nullable = treeNode.left.nullable && treeNode.right.nullable;
                firstPos.addAll(treeNode.left.firstPos);
                if (treeNode.left.nullable)
                    firstPos.addAll(treeNode.right.firstPos);
                treeNode.firstPos = firstPos;
                lastPos.addAll(treeNode.right.lastPos);
                if (treeNode.right.nullable)
                    lastPos.addAll(treeNode.left.lastPos);
                treeNode.lastPos = lastPos;
                reTreeNodeStack.push(treeNode);
            } else if (ch == '*') {
                RETreeNode treeNode = new RETreeNode(NodeType.STAR, ch);
                treeNode.left = reTreeNodeStack.pop();
                treeNode.nullable = true;
                firstPos.addAll(treeNode.left.firstPos);
                treeNode.firstPos = firstPos;
                lastPos.addAll(treeNode.left.lastPos);
                treeNode.lastPos = lastPos;
                reTreeNodeStack.push(treeNode);
            }
        }
        RETreeNode root = reTreeNodeStack.pop();
        preOrder(root);
        return root;
    }


    /**
     * 先序遍历产生一个位置节点的followPos
     *
     * @param reTreeNode 当前节点
     */
    private static void preOrder(RETreeNode reTreeNode) {
        if (reTreeNode == null)
            return;
        if (reTreeNode.type == NodeType.CAT) {
            Set<RETreeNode> follows = new HashSet<>();
            for (RETreeNode node : reTreeNode.right.firstPos) {
                if (node.type == NodeType.SYNC) {
                    follows.add(node);
                }
            }
            for (RETreeNode node : reTreeNode.left.lastPos) {
                if (node.type == NodeType.SYNC)
                    node.followPos.addAll(follows);
            }
        } else if (reTreeNode.type == NodeType.STAR) {
            Set<RETreeNode> follows = new HashSet<>();
            for (RETreeNode node : reTreeNode.firstPos) {
                if (node.type == NodeType.SYNC)
                    follows.add(node);
            }
            for (RETreeNode node : reTreeNode.lastPos) {
                node.followPos.addAll(follows);
            }
        }
        preOrder(reTreeNode.left);
        preOrder(reTreeNode.right);
    }

    /**
     * @param root 正则分析树的根节点
     * @return DFA状态集
     */
    private static Set<DFAState> createDFAState(RETreeNode root) {
        Set<DFAState> dfaStates = new HashSet<>();
        List<DFAState> unMarkedStates = new ArrayList<>();//未标记的状态


        DFAState dfaState = new DFAState();
        dfaState.states = root.firstPos;
        dfaState.attri = Attri.START;//开始状态
        unMarkedStates.add(dfaState);
        while (!unMarkedStates.isEmpty()) {
            DFAState oldState = unMarkedStates.remove(0);
            dfaStates.add(oldState);
            for (char ch : letters) {
                Set<RETreeNode> reTreeNodes = new HashSet<>();
                for (RETreeNode reTreeNode : oldState.states) {
                    if (reTreeNode.value == ch) {
                        reTreeNodes.addAll(reTreeNode.followPos);
                    }
                }
                if (reTreeNodes.size() == 0)
                    continue;
                boolean contain = false;
                DFAState newDFAState = null;
                for (DFAState state1 : dfaStates) {
                    if (state1.states.equals(reTreeNodes)) {
                        contain = true;
                        newDFAState = state1;
                    }
                }
                if (!contain) {
                    for (DFAState state1 : unMarkedStates) {
                        if (state1.states.equals(reTreeNodes)) {
                            contain = true;
                            newDFAState = state1;
                        }
                    }
                }
                if (!contain) {
                    newDFAState = new DFAState();
                    newDFAState.states = reTreeNodes;
                    for (RETreeNode reTreeNode : reTreeNodes) {
                        if (reTreeNode.followPos.size() == 0)
                            newDFAState.attri = Attri.ACCEPT;
                    }
                    unMarkedStates.add(newDFAState);
                }
                oldState.DFATurn.put(ch, newDFAState);
            }
        }
        Integer id = 1;
        Set<DFAState> emptyState = new HashSet<>();//删除空状态
        for (DFAState dfaState1 : dfaStates) {
            if (dfaState1.states.size() == 0)
                emptyState.add(dfaState1);
            dfaState1.id = id;
            id++;
        }
        dfaStates.removeAll(emptyState);
        return dfaStates;
    }

    /**
     * @param dfaStates DFA状态集
     * @return 具有最少状态的DFA
     */
    private static Set<DFAState> minDFA(Set<DFAState> dfaStates) {
        Set<DFAGroup> divide = new HashSet<>();
        DFAGroup group1 = new DFAGroup();
        group1.id = 1;
        DFAGroup group2 = new DFAGroup();
        group2.id = 2;
        for (DFAState dfaState : dfaStates) {
            if (dfaState.attri == Attri.ACCEPT) {
                group1.states.add(dfaState);
            } else
                group2.states.add(dfaState);
        }
        divide.add(group1);
        divide.add(group2);
        Set<DFAGroup> newDivide = new HashSet<>();
        while (true) {
            for (DFAGroup group : divide) {
                for (DFAState state : group.states) {
                    if (!findAGroupToMove(state, group, divide, newDivide)) {
                        DFAGroup addGroup = new DFAGroup();
                        addGroup.id = newDivide.size() + 1;
                        addGroup.states.add(state);
                        newDivide.add(addGroup);
                    }
                }
            }
            if (divide.size() == newDivide.size())
                break;
            divide = newDivide;
            newDivide = new HashSet<>();
        }
        Set<DFAState> ret = new HashSet<>();
        for (DFAGroup group : newDivide) {
            DFAState state = group.states.iterator().next();
            state.id = group.id;
            for(char ch : letters){
                DFAState stateTurn = state.DFATurn.get(ch);
                if(stateTurn != null){
                    for(DFAGroup group3 : newDivide){
                        if(group3.states.contains(stateTurn)){
                            state.minDFAMap.put(ch,group3.id);
                        }
                    }
                }
                else
                    state.minDFAMap.put(ch,-1);
            }
            ret.add(state);
        }
        for(DFAState state : ret){
            for(char ch : letters){
                int i = state.minDFAMap.get(ch);
                boolean b = false;
                for(DFAState state1 : ret){
                    if(state1.id == i){
                        state.minDFATurn.put(ch,state1);
                        b = true;
                        break;
                    }
                }
                if(!b){
                    state.minDFATurn.put(ch,null);
                }
            }
        }
        return ret;
    }

    /**
     * 寻找一个状态能否加到新的划分里
     *
     * @param state     当前状态
     * @param group     状态所在分组
     * @param divide    旧的划分
     * @param newDivide 新的划分
     * @return 是否添加
     */
    private static boolean findAGroupToMove(DFAState state, DFAGroup group, Set<DFAGroup> divide, Set<DFAGroup> newDivide) {
        for (DFAGroup newGroup : newDivide) {
            DFAState state1 = newGroup.states.iterator().next();
            boolean b = true;
            if (!group.states.contains(state1)) {
                b = false;
            }
            if (b) {
                for (char ch : letters) {
                    DFAState stateTurn = state.DFATurn.get(ch);
                    DFAState state1Turn = state1.DFATurn.get(ch);
                    if (!inSameGroup(stateTurn, state1Turn, divide)
                            && (stateTurn != null || state1Turn != null)) {//发现不在同一组
                        b = false;
                        break;
                    }
                }
            }
            if (b) {
                newGroup.states.add(state);
                return true;
            }
        }
        return false;
    }

    /**
     * @param stateTurn  状态1的转换状态
     * @param state1Turn 状态2的转换状态
     * @param divide     划分
     * @return 两个状态是否属于同一划分
     */
    private static boolean inSameGroup(DFAState stateTurn, DFAState state1Turn, Set<DFAGroup> divide) {
        for (DFAGroup dfaStates : divide) {
            if (dfaStates.states.contains(stateTurn) && dfaStates.states.contains(state1Turn)) {
                return true;
            }
        }
        return false;
    }
}
