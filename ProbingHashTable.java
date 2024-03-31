import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class ProbingHashTable<K, V> implements HashTable<K, V> {
    final static int DEFAULT_INIT_CAPACITY = 4;
    final static double DEFAULT_MAX_LOAD_FACTOR = 0.75;
    final private HashFactory<K> hashFactory;
    final private double maxLoadFactor;
    private int capacity;
    private HashFunctor<K> hashFunc;
    private Pair<K,V> [] arr;
    private int k;
    private Integer size;

    public ProbingHashTable(HashFactory<K> hashFactory) {
        this(hashFactory, DEFAULT_INIT_CAPACITY, DEFAULT_MAX_LOAD_FACTOR);
    }

    public ProbingHashTable(HashFactory<K> hashFactory, int k, double maxLoadFactor) {
        this.hashFactory = hashFactory;
        this.maxLoadFactor = maxLoadFactor;
        this.capacity = 1 << k;
        this.hashFunc = hashFactory.pickHash(k);
        this.arr = new Pair[capacity];
        this.k = k;
        this.size = 0;
    }

    public V search(K key) {
        int index = hashFunc.hash(key);
        HashingUtils h1 = new HashingUtils();
        Pair<K, V> pair;
        for(int i = 0; i < capacity ; i++){
            pair = arr[index];
            if(pair == null)
                return null;
            if(pair.first() == key)
                return pair.second();
            index = (index+1) % capacity;
        }
        return null;

    }

    public void insert(K key, V value) {
        boolean flag = true;
        if(((double) (size+1) /capacity) >= maxLoadFactor) {
            reHash();
        }

       int index = hashFunc.hash(key);
       for(int i = 0; i < capacity && flag; i++){
           if(arr[index] == null || arr[index].first()==null) {
               arr[index] = new Pair<K,V>(key, value);
               flag = false;
           }
           index = (index + 1) % capacity;
       }
       size++;
    }
    public void reHash(){
        this.k = this.k + 1;
        this.capacity = this.capacity << 1;
        Pair<K,V>[] old_arr = this.arr;
        this.arr = new Pair[capacity];
        this.hashFunc = hashFactory.pickHash(k);
        for(Pair<K,V> pair: old_arr){
            if(pair != null && pair.first() != null) {
                this.insert( pair.first(),  pair.second());
            }
        }
    }

    public boolean delete(K key) {
        int index = hashFunc.hash(key);

        for(int i = 0; i < capacity ; i++){
            if(arr[index] == null || arr[index].first() == null)
                return false;

            if(arr[index].first() == key) {
                arr[index] = new Pair<>(null, null); // Deleted sign
                size--;
                return true;
            }
            index = (index + 1) % capacity;
        }
        return false;
    }

    public HashFunctor<K> getHashFunc() {
        return hashFunc;
    }

    public int capacity() { return capacity; }
    public Integer size() { return size; }

    public static void main(String[] args){
        ProbingHashTable<Long,Long> p1 = new ProbingHashTable<>(new MultiplicativeShiftingHash(), 16, 0.5);
        for(long i= 0; i<10; i++)
        {
            p1.insert(i,i);
            System.out.println(p1.search(i));
        }

    }
}
