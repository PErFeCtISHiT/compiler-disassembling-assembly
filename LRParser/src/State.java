/**
 * @author: pis
 * @description: DFA状态
 * @date: create in 22:23 2018/11/8
 */
class State {
    int id;//状态号
    String catalog;//该状态的栈顶标识符

    State(int id, String catalog) {
        this.id = id;
        this.catalog = catalog;
    }
}
