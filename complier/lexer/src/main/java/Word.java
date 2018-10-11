

/**
 * @author: pis
 * @description: a kind of token
 * @date: create in 20:42 2018/10/10
 */
public class Word extends Token{
    public final String lexeme;
    public Word(int t,String s) {
        super(t);
        lexeme = s;
    }
}
