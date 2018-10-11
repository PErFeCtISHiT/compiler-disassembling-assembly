


import jdk.nashorn.internal.parser.Lexer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

/**
 * @author: pis
 * @description: lexer
 * @date: create in 20:45 2018/10/10
 */
public class MyLexer {
    private int line = 1;
    private char peek = ' ';
    private Hashtable<String, Word> words = new Hashtable<String, Word>();

    private void reverse(Word w) {
        words.put(w.lexeme, w);
    }

    private MyLexer() {
        reverse(new Word(Tag.PRIVATE, "private"));
        reverse(new Word(Tag.PROTECTED, "protected"));
        reverse(new Word(Tag.PUBLIC, "public"));
        reverse(new Word(Tag.STATIC, "static"));
        reverse(new Word(Tag.STRING, "String"));
        reverse(new Word(Tag.VOID, "void"));
    }

    private Token scan() {
        try {
            for (; ; peek = (char) System.in.read()) {
                if (peek == '\n') {
                    line++;
                } else if (peek != ' ' && peek != '\t')
                    break;
            }
            if (Character.isDigit(peek)) {
                int value = 0;
                while (Character.isDigit(peek)) {
                    value = 10 * value + Character.digit(peek, 10);
                    peek = (char) System.in.read();
                }
                return new Num(value);
            }
            if (Character.isLetter(peek)) {
                StringBuilder str = new StringBuilder();
                while (Character.isLetterOrDigit(peek)) {
                    str.append(peek);
                    peek = (char) System.in.read();
                }
                String s = str.toString();
                Word word = words.get(s);
                if (word != null)
                    return word;
                else {
                    Word word1 = new Word(Tag.ID, s);
                    words.put(s, word1);
                    return word1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Token t = new Token(peek);
        peek = ' ';
        return t;
    }
    public static void main(String[] args){
        try {
            String path = System.getProperty("user.dir") + "/resources/" + "test.java";
            FileInputStream fileInputStream = new FileInputStream(path);
            System.setIn(fileInputStream);
            MyLexer lexer = new MyLexer();
            Token token = lexer.scan();
            while (token.tag != ' ' && token.tag != 65535){
                System.out.println("token's tag: " + token.tag);
                if(token.tag >= 257){
                    Word word = (Word) token;
                    System.out.println(word.lexeme);
                }
                token = lexer.scan();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
