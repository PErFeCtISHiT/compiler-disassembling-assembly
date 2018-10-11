

/**
 * @author: pis
 * @description: a kind of token
 * @date: create in 20:39 2018/10/10
 */
public class Num extends Token{
    public final int value;
    public Num(int t) {
        super(Tag.NUM);
        value = t;
    }
}
