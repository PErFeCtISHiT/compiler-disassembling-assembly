import java.util.Stack;

/**
 * @author: pis
 * @description: 错误处理例程
 * @date: create in 23:29 2018/11/8
 */
class errorRoutine {
    static void error2(String str) {
        System.out.println("不该出现在这里的符号" + str);
    }

    private static void error1() {
        System.out.println("if与else之间为空");
    }

    static int dealError(Action action, String str, Stack<State> stateStack,int i) {
        switch (action.id){
            case 1:
                if(action.subId == 1)
                    stateStack.push(new State(5,"a"));
                else
                    stateStack.push(new State(10,"a"));
                error1();
                break;
            case 2:
                i++;
                error2(str);
                break;
            case 3:
                if(action.subId == 1){
                    stateStack.push(new State(4,";"));
                }
                else
                    stateStack.push(new State(9,";"));
                error3();
                break;
            case 4:
                error4(stateStack);
        }
        return i;
    }

    private static void error4(Stack<State> stateStack) {
        System.out.print("语法分析提前结束，栈中的符号");
        for(State state : stateStack){
            System.out.print(state.catalog + " ");
        }
        System.out.println("仍未匹配");
        System.exit(-1);
    }

    private static void error3() {
        System.err.println("语句间缺少;");
    }
}
