import java.util.*;

public class PersistentInt {

    private ArrayList<MyPair> data;
    PersistentInt() {
        data = new ArrayList<MyPair>();
        data.add(new MyPair(0, -1));
    }

    public void add(int x, int round) {
        remove(round);
        data.add(new MyPair(x, round));
    }

    public int get() {
        return data.get(data.size() - 1).getFirst();
    }

    public void remove(int round) {
        if (data.get(data.size() - 1).getSecond() == round) {
            data.remove(data.size() - 1);
        }
    }
}
