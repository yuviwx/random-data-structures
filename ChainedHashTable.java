import java.util.*;

public class ChainedHashTable<K, V> implements HashTable<K, V> {
    final static int DEFAULT_INIT_CAPACITY = 4;
    final static double DEFAULT_MAX_LOAD_FACTOR = 2;
    final private HashFactory<K> hashFactory;
    final private double maxLoadFactor;
    private int capacity;
    private HashFunctor<K> hashFunc;
    public LinkedList <Pair<K,V>> [] arr;
    private int k;
    private Integer size;


    public ChainedHashTable(HashFactory<K> hashFactory) {
        this(hashFactory, DEFAULT_INIT_CAPACITY, DEFAULT_MAX_LOAD_FACTOR);
    }

    public ChainedHashTable(HashFactory<K> hashFactory, int k, double maxLoadFactor) {
        this.hashFactory = hashFactory;
        this.maxLoadFactor = maxLoadFactor;
        this.capacity = 1 << k;
        this.hashFunc = hashFactory.pickHash(k);
        this.arr = new LinkedList[capacity];
        this.size = 0;
        this.k = k;
    }

    public V search(K key) {
        int arr_key = hashFunc.hash(key);
        LinkedList<Pair<K, V>> l1 = arr[arr_key];
        if(l1 != null) {
            for (Pair<K,V> pair : l1) {
                if (pair.first().equals(key))
                    return  pair.second();
            }
        }
        return null;
    }

    public void insert(K key, V value) {
        if(((double) (size + 1) /capacity) >= maxLoadFactor){
            reHash();
        }
        int arr_key = hashFunc.hash(key);
        if(this.arr[arr_key] == null) {
            this.arr[arr_key] = new LinkedList<>();
        }
        this.arr[arr_key].addFirst(new Pair<>(key, value));
        size++;
    }

    /**
     * Doubling the size of the table, and then for each cell that contains a linked list
     * we re-Hash all the pairs in the linked list for their new place in the new table.
     */
    public void reHash() {
            this.k = this.k + 1;
            this.capacity = this.capacity * 2;
            LinkedList <Pair<K,V>> [] new_arr = new LinkedList[this.capacity];
            this.hashFunc = hashFactory.pickHash(k);
            for (LinkedList<Pair<K, V>> pairs : arr) {
                if (pairs != null) {
                    for (Pair<K,V> pair : pairs) {
                        int index = hashFunc.hash((K) pair.first());
                        if(new_arr[index] == null) {
                            new_arr[index] = new LinkedList<>();
                        }
                        new_arr[index].addFirst(pair);
                    }
                }
            }
            this.arr = new_arr;
        }


    public boolean delete(K key) {
        int arr_key = hashFunc.hash(key);
        LinkedList<Pair<K, V>> l1 = arr[arr_key];

        for(Pair<K,V> pair : l1){
            if(pair.first().equals(key)) {
                l1.remove(pair);
                size--;
                return true;
            }
        }
        return false;
    }

    public HashFunctor<K> getHashFunc() {
        return hashFunc;
    }

    public int capacity() { return capacity; }
    public Integer size() { return size; }
}
