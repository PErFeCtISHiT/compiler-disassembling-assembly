import java.util.HashSet;
import java.util.Set;

/**
 * @author: pis
 * @description: 正则表达式的语法树结点
 * @date: create in 9:23 2018/10/27
 */
class RETreeNode {
    RETreeNode(NodeType type, char value) {
        this.type = type;
        this.value = value;
        this.left = null;
        this.right = null;
        if(type == NodeType.SYNC)
            followPos = new HashSet<>();
    }

    NodeType type;//star,or,cat,或者普通节点
    char value;//普通节点的值
    RETreeNode left;
    RETreeNode right;
    boolean nullable;
    Set<RETreeNode> firstPos;
    Set<RETreeNode> lastPos;
    Set<RETreeNode> followPos;
    int pos;//普通节点位置
}
