import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author: pis
 * @description: 语法分析树节点
 * @date: create in 20:42 2018/11/9
 */
class ParseTreeNode implements Comparable<ParseTreeNode> {
    ParseTreeNode leftSon = null;
    ParseTreeNode rightBrother = null;
    String data;
    private int level;//所在层级

    /**
     * 层级遍历打印这颗分析树
     */
    void printTree(){
        this.level = 1;
        int i = 1;
        Queue<ParseTreeNode> queue = new PriorityQueue<>();
        queue.offer(this);
        while (!queue.isEmpty()){
            ParseTreeNode node = queue.poll();
            if(node.level > i){
                System.out.println();
                i++;
            }
            System.out.print(node.data + "\t");
            ParseTreeNode son = node.leftSon;
            if(son != null){
                son.level = node.level + 1;
                queue.offer(son);
                ParseTreeNode brother = son.rightBrother;
                while (brother != null){
                    brother.level = son.level;
                    queue.offer(brother);
                    brother = brother.rightBrother;
                }
            }
        }
    }


    /**
     * 分析树节点并不需要这个方法，但是在进入PriorityQueue时需要转换成comparable类，因此实现了这个接口
     */
    @Override
    public int compareTo(ParseTreeNode o) {
        return 0;
    }
}
