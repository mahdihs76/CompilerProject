import java.util.ArrayList;
import java.util.Stack;

public class Test {
    public static void main(String[] args) {
        Stack<Integer> s = new Stack<Integer>();
        s.push(1);
        s.push(2);
        s.push(3);
        s.push(4);
        s.push(5);
        System.out.println(s.get(s.size() - 1));
    }
}
