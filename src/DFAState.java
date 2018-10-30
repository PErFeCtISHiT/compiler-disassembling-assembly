import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author: pis
 * @description: DFA状态
 * @date: create in 13:18 2018/10/27
 */
class DFAState {
    DFAState() {
        for (char ch : Lex.letters) {
            DFATurn.put(ch, null);
        }
    }

    Set<RETreeNode> states;

    boolean isAcceptState() {//是否是接受状态
        for (char ch : Lex.letters) {
            if (DFATurn.get(ch) == this) {//自环也是接受状态
                return true;
            }

        }
        return attri == Attri.ACCEPT;
    }

    Attri attri = Attri.SIMPLE;
    Integer id;//状态号
    Map<Character, DFAState> DFATurn = new HashMap<>();//转换表(未最小化前)
    Map<Character,Integer> minDFAMap = new HashMap<>();//预备最小化DFA转换表
    Map<Character,DFAState> minDFATurn = new HashMap<>();//最小化的转换表
}
