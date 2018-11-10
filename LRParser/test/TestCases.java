import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: pis
 * @description: 每个测试用例接收一组token序列，输出对应的语法分析树
 * @date: create in 23:59 2018/11/8
 */
public class TestCases {
    private Parser parser = new Parser();

    @Test
    public void TestCase1() throws FileNotFoundException {
        for (int i = 1; i < 7; i++) {
            System.setOut(new PrintStream("resources/output" + i + ".txt"));
            List<String> grammars = parser.parse(getToken("resources/input" + i + ".txt"));
            System.out.println("reduce list:");
            for (String s : grammars) {
                System.out.println(s);
            }
            System.out.println();
            System.out.println("parse tree:");
            ParseTreeNode root = parser.makeTree(grammars);
            if (root != null)
                root.printTree();
        }
    }

    /**
     * 产生测试用的token
     *
     * @param path 测试用例路径
     * @return token序列
     */
    private List<String> getToken(String path) {
        List<String> tokens = new ArrayList<>();
        File file = new File(path);
        try (FileReader fileReader = new FileReader(file)
             ; BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String str = bufferedReader.readLine();
            while (str != null && str.length() != 0) {
                String strs[] = str.split("\\s+");
                tokens.addAll(Arrays.asList(strs));
                str = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }
}
