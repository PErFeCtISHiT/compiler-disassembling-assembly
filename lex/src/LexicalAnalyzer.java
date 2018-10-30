import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * @author: pis
 * @description: 通过lex产生的词法分析器
 * @date: create in 19:05 2018/10/27
 */
class LexicalAnalyzer {

    static Set<DFAState> REToDFA(String path) {
        Lex lex = new Lex();
        return lex.getDFAStates(path);
    }

    static List<Token> generateTokens(String s, Set<DFAState> dfaStates) {
        List<Token> tokens = new ArrayList<>();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(s);
            System.setIn(fileInputStream);
            Token token = scan(dfaStates);
            while (!token.str.equals("!")) {
                tokens.add(token);
                token = scan(dfaStates);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    private static Token scan(Set<DFAState> dfaStates) {
        DFAState state = null;
        for (DFAState dfaState : dfaStates) {
            if (dfaState.attri == Attri.START) {
                state = dfaState;
                break;
            }
        }
        try {
            char peek = (char) System.in.read();
            StringBuilder str = new StringBuilder();
            if (peek == '!' || peek == 65535)
                return new Token("!");
            for (; peek != '!'; peek = (char) System.in.read()) {
                if (peek != ' ' && peek != '\t' && peek != '\n' && peek != '\r') {
                    if (Lex.letters.contains(peek)) {//合法字符
                        assert state != null;
                        DFAState nextState = state.DFATurn.get(peek);
                        if (nextState != null) {
                            state = nextState;
                            str.append(peek);
                        } else {
                            if (state.isAcceptState()) {
                                return new Token(str.toString());
                            }
                            else {
                                System.err.println("遇到不符合词法规范的字符");
                                System.exit(-1);
                            }
                        }
                    } else {
                        System.err.println("遇到不符合词法规范的字符");
                        System.exit(-1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert state != null;
        if (!state.isAcceptState()) {
            System.err.println("遇到不符合词法规范的字符");
        }
        return new Token("!");
    }

}
