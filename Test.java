import java.util.ArrayList;
import java.util.Stack;

public class Test {
    public static void main(String[] args) {
        ArrayList<Character> s = new ArrayList<>();
        s.add('a');
        s.add('b');
        s.add('c');
        s.add('d');
        s.add('e');

        s.set(0, 'x');
        System.out.println(s);
    }
}
