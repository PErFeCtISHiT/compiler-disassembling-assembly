import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;

/**
 * @author: pis
 * @description: 每个测试用例接收一组正则表达式，若干组输入流，输出若干组token序列。
 * @date: create in 18:27 2018/10/30
 */
public class TestCases {
    @Test
    public void TestCase1() throws FileNotFoundException {
        System.setOut(new PrintStream("resources/out1.txt"));
        Set<DFAState> dfaStates = LexicalAnalyzer.REToDFA("resources/RE1.txt");
        assert dfaStates != null;
        List<Token> tokens = LexicalAnalyzer.generateTokens("resources/Stream1a.txt", dfaStates);
        System.out.println("tokens on stream 1a");
        for(Token token : tokens){
            System.out.println(token.str);
        }
        System.out.println();
        tokens = LexicalAnalyzer.generateTokens("resources/Stream1b.txt", dfaStates);
        System.out.println("tokens on stream 1b");
        for(Token token : tokens){
            System.out.println(token.str);
        }
        System.out.println();
    }
    @Test
    public void TestCase2() throws FileNotFoundException {
        System.setOut(new PrintStream("resources/out2.txt"));
        Set<DFAState> dfaStates = LexicalAnalyzer.REToDFA("resources/RE2.txt");
        assert dfaStates != null;
        List<Token> tokens = LexicalAnalyzer.generateTokens("resources/Stream2a.txt", dfaStates);
        System.out.println("tokens on stream 2a");
        for(Token token : tokens){
            System.out.println(token.str);
        }
        System.out.println();
        tokens = LexicalAnalyzer.generateTokens("resources/Stream2b.txt", dfaStates);
        System.out.println("tokens on stream 2b");
        for(Token token : tokens){
            System.out.println(token.str);
        }
        System.out.println();
    }
    @Test
    public void TestCase3() throws FileNotFoundException {
        System.setOut(new PrintStream("resources/out3.txt"));
        Set<DFAState> dfaStates = LexicalAnalyzer.REToDFA("resources/RE3.txt");
        assert dfaStates != null;
        List<Token> tokens = LexicalAnalyzer.generateTokens("resources/Stream3a.txt", dfaStates);
        System.out.println("tokens on stream 3a");
        for(Token token : tokens){
            System.out.println(token.str);
        }
        System.out.println();
        tokens = LexicalAnalyzer.generateTokens("resources/Stream3b.txt", dfaStates);
        System.out.println("tokens on stream 3b");
        for(Token token : tokens){
            System.out.println(token.str);
        }
        System.out.println();
    }
}
