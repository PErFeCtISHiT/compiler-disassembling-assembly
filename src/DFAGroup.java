import java.util.HashSet;
import java.util.Set;

/**
 * @author: pis
 * @description: DFA分组，用来最小化一个DFA
 * @date: create in 16:16 2018/10/30
 */
class DFAGroup {
    DFAGroup() {
        this.states = new HashSet<>();
    }

    Set<DFAState> states;
    int id;
}
