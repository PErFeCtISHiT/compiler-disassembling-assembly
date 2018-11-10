import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author: pis
 * @description: LR(1)分析器
 * @date: create in 23:11 2018/11/8
 */
class Parser {
    private Action parsingTable[][] = new Action[16][6];
    private Grammar grammars[] = new Grammar[5];

    Parser() {//初始化转换表和文法
        File file = new File("resources/grammar.txt");
        try (FileReader fileReader = new FileReader(file)
             ; BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String str = bufferedReader.readLine();
            int i = 0;
            while (str != null && str.length() != 0) {
                String strs[] = str.split("\\s+");
                Grammar grammar = new Grammar();
                grammar.left = strs[0];
                grammar.right = new String[strs.length - 1];
                System.arraycopy(strs, 1, grammar.right, 0, strs.length - 1);
                grammars[i] = grammar;
                i++;
                str = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new File("resources/turn.txt");
        try (FileReader fileReader = new FileReader(file)
             ; BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String str = bufferedReader.readLine();
            int i = 0;
            while (str != null && str.length() != 0) {
                String strs[] = str.split("\\s+");
                for (int j = 0; j < 6; j++) {
                    String s = strs[j];
                    Action action;
                    switch (s.charAt(0)) {
                        case 's':
                            action = new Action(Integer.valueOf(s.substring(1)), Operator.shift);
                            parsingTable[i][j] = action;
                            break;
                        case 'r':
                            action = new Action(Integer.valueOf(s.substring(1)), Operator.reduce);
                            parsingTable[i][j] = action;
                            break;
                        case 'e':
                            if (s.length() == 2) {//没有子状态的error
                                action = new Action(Integer.valueOf(s.substring(1)), Operator.error);
                            } else {
                                action = new Action(Integer.valueOf(s.substring(1, 2)), Operator.error);
                                action.subId = Integer.valueOf(s.substring(3));
                            }
                            parsingTable[i][j] = action;
                            break;
                        case 'A':
                            action = new Action(-1, Operator.end);
                            parsingTable[i][j] = action;
                            break;
                        default:
                            action = new Action(Integer.valueOf(s), Operator.shift);
                            parsingTable[i][j] = action;
                    }
                }
                i++;
                str = bufferedReader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param tokens 序列
     * @return 规约序列
     */
    List<String> parse(List<String> tokens) {
        tokens.add("$");//给输入加上结束标识

        Stack<State> stateStack = new Stack<>();
        stateStack.push(new State(0, "$"));//初始化栈
        List<String> outPut = new ArrayList<>();
        int i = 0;//输入符号的读头
        while (true) {
            String str = tokens.get(i);
            //定位状态转换
            int x = stateStack.peek().id;
            int y = -1;
            boolean f = true;//是否调用过错误例程
            switch (str) {
                case "if":
                    y = 0;
                    break;
                case ";":
                    y = 1;
                    break;
                case "else":
                    y = 2;
                    break;
                case "a":
                    y = 3;
                    break;
                case "$":
                    y = 4;
                    break;
                case "S":
                    y = 5;
                    break;
                default:
                    errorRoutine.error2(str);
                    f = false;
                    i++;
            }
            if (f) {
                Action action = parsingTable[x][y];
                switch (action.type) {
                    case shift:
                        stateStack.push(new State(action.id, str));
                        i++;
                        break;
                    case reduce:
                        Grammar grammar = grammars[action.id];
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(grammar.left).append("->");
                        for (int j = 0; j < grammar.right.length; j++) {
                            stringBuilder.append(grammar.right[j]);
                            stateStack.pop();
                        }
                        outPut.add(stringBuilder.toString());
                        State state = new State(parsingTable[stateStack.peek().id][5].id, grammar.left);
                        stateStack.push(state);
                        break;
                    case error:
                        if(action.id == 1){
                            outPut.add("S->a");
                        }
                        i = errorRoutine.dealError(action, str, stateStack, i);
                        break;
                    case end:
                        return outPut;
                }
            }
        }
    }

    /**
     * 产生语法分析树
     *
     * @param grammars 语法规约串
     * @return 语法分析树
     */
    ParseTreeNode makeTree(List<String> grammars) {
        ParseTreeNode root = null;
        Stack<ParseTreeNode> stack = new Stack<>();
        for (String str : grammars) {
            root = new ParseTreeNode();
            root.data = str;
            ParseTreeNode parseTreeNode = root;
            int f = 0;
            for (int i = 3; i < str.length(); i++) {
                if (str.charAt(i) == 'S') {//遇到S，产生子节点
                    if (f == 0) {
                        parseTreeNode.leftSon = stack.pop();
                        parseTreeNode = parseTreeNode.leftSon;
                        f = 1;
                    } else {
                        parseTreeNode.rightBrother = stack.pop();
                        parseTreeNode = parseTreeNode.rightBrother;
                    }
                }
            }
            stack.push(root);
        }
        return root;
    }
}
