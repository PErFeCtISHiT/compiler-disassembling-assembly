/**
 * @author: pis
 * @description: 状态转换的操作
 * @date: create in 22:25 2018/11/8
 */
class Action {
    int id;//下一步动作的序号
    Operator type;//操作的类型
    Action(int id, Operator type) {
        this.id = id;
        this.type = type;
    }
    int subId;//error的子序号
}
enum Operator{
    shift,reduce,error,end//移入，规约，错误，结束
}