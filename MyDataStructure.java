import java.util.ArrayList;
import java.util.List;

public class MyDataStructure {
    private final int N;
    private IndexableSkipList skipList;
    private ChainedHashTable<Integer, AbstractSkipList.Node> hash;
    private int size;

    /*
     * You may add any members that you wish to add.
     * Remember that all the data-structures you use must be YOUR implementations,
     * except for the List and its implementation for the operation Range(low, high).
     */

    /***
     * This function is the Init function described in Part 4.
     *
     * @param N The maximal number of items expected in the DS.
     */
    public MyDataStructure(int N) {
        this.N = N;
        // I choose the k, so I won't need to rehash ever.
        int k = (int)(Math.log((4.0 /3)*N)/Math.log(2)) + 1; // = math.ceil(log_2(n))
        // I choose the best max load factor from the experiment
        this.hash = new ChainedHashTable<>(new ModularHash(), k,0.75);
        // I choose the best probability from the experiment
        this.skipList = new IndexableSkipList(0.5);
        this.size = 0;
    }

    public boolean insert(int value) {
        // Check the limit size of the data structure
        if(size == N){
            throw new ArrayIndexOutOfBoundsException("There should not be more than " + this.N + " item in the data structure");
        }
        // Use skip-list implementation to check if 'value' already exists in the data structure
        AbstractSkipList.Node node = this.skipList.insert(value);
        if(node == null){
            return false;
        }

        this.hash.insert(value,node);
        size++;
        return true;
    }

    public boolean delete(int value) {
        AbstractSkipList.Node node = this.hash.search(value);
        if(node == null){
            return false;
        }
        this.hash.delete(value);
        this.skipList.delete(node);
        size--;
        return true;
    }

    public boolean contains(int value) {
        return !(this.hash.search(value) == null) ;
    }

    public int rank(int value) {
        return this.skipList.rank(value);
    }

    public int select(int index) {
        return this.skipList.select(index);
    }

    public List<Integer> range(int low, int high) {
        AbstractSkipList.Node curr = this.hash.search(low);
        if(curr == null) {
            return null;
        }
        List<Integer> output = new ArrayList<>();
        while(curr.key() <= high){
            output.add(curr.key());
            curr = curr.getNext(0);
        }
        return output;
    }
}
