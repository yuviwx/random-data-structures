import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class HashingExperimentUtils {
    final private static int k = 16;

    public static Pair<Double, Double> measureOperationsChained(double maxLoadFactor) {
        // Creating a hash table using chaining.
        HashFactory<Long> factoryH = new MultiplicativeShiftingHash();
        ChainedHashTable<Long, Long> chan = new ChainedHashTable<>(factoryH, k, maxLoadFactor);
        return helper(chan, maxLoadFactor);
    }

    public static Pair<Double, Double> measureOperationsProbing(double maxLoadFactor) {
        // Creating a hash table using probing.
        HashFactory<Long> factoryH = new MultiplicativeShiftingHash();
        ProbingHashTable<Long, Long> prob = new ProbingHashTable<>(factoryH, k, maxLoadFactor);

        return helper(prob, maxLoadFactor);
    }

    /**
     * This function contains all identical lines of code from both measureOperationsProbing and measureOperationsChaining.
     *
     * @param hash          a hash table
     * @param maxLoadFactor the amount of items in the hash divide by the capacity of the hash-array
     * @return a pair of measurements - insert and search average times
     */
    private static Pair<Double, Double> helper(HashTable<Long, Long> hash, double maxLoadFactor) {
        long endTime;
        long startTime;
        long averageTimeSearch = 0;
        long averageTimeInsert = 0;
        int size = (int) (((maxLoadFactor * hash.capacity())));
// Creating an array with random items
        HashingUtils h1 = new HashingUtils();
        Integer[] arr = h1.genUniqueIntegers((int) (((maxLoadFactor * hash.capacity())) * (1.5)));
        // Insert all the items until 'maxLoadFactor'
        for (long i = 0; i < size; i++) {
            startTime = System.nanoTime();
            hash.insert(i, (long) arr[(int) i]);
            endTime = System.nanoTime();
            averageTimeInsert += (endTime - startTime);
        }

        // Search 2^16 * maxLoadFactor when 50% success and 50% fail.
        for (int i = 0; i < (size / 2); i++) {
            startTime = System.nanoTime();
            hash.search((long) (i));
            endTime = System.nanoTime();
            averageTimeSearch += (endTime - startTime);
        }
        for (int i = size; i < arr.length; i++) {
            startTime = System.nanoTime();
            hash.search((long) (i));
            endTime = System.nanoTime();
            averageTimeSearch += (endTime - startTime);
        }

        Double outputInsert = (double) (averageTimeInsert / size);
        Double outputSearch = (double) (averageTimeSearch / size);
        return new Pair<>(outputInsert, outputSearch);
    }


    // & 69.2 & 130839.4
    public static Pair<Double, Double> measureLongOperations() {
        // Creating a hash table using chaining.
        HashFactory<Long> factoryH = new MultiplicativeShiftingHash();
        ChainedHashTable<Long, Long> chan = new ChainedHashTable<>(factoryH, k, 1);
        int maxLoadFactor = 1;
        return helper2(chan, maxLoadFactor);
    }

    private static Pair<Double, Double> helper2(HashTable<Long, Long> hash, double maxLoadFactor) {
        long endTime;
        long startTime;
        long averageTimeSearch = 0;
        long averageTimeInsert = 0;
        int size = (int) (((maxLoadFactor * hash.capacity())));
// Creating an array with random items
        HashingUtils h1 = new HashingUtils();
        Long[] arr = h1.genUniqueLong((int) (((maxLoadFactor * hash.capacity())) * (1.5)));
        // Insert all the items until 'maxLoadFactor'
        for (long i = 0; i < size; i++) {
            startTime = System.nanoTime();
            hash.insert(i, arr[(int) i]);
            endTime = System.nanoTime();
            averageTimeInsert += (endTime - startTime);
        }

        // Search 2^16 * maxLoadFactor when 50% success and 50% fail.
        for (int i = 0; i < (size >> 1); i++) {
            startTime = System.nanoTime();
            hash.search((long) (i));
            endTime = System.nanoTime();
            averageTimeSearch += (endTime - startTime);
        }
        for (int i = size; i < arr.length; i++) {
            startTime = System.nanoTime();
            hash.search((long) (i));
            endTime = System.nanoTime();
            averageTimeSearch += (endTime - startTime);
        }

        Double outputInsert = (double) (averageTimeInsert / size);
        Double outputSearch = (double) (averageTimeSearch / size);
        return new Pair<>(outputInsert, outputSearch);
    }

    public static Pair<Double, Double> measureStringOperations() {
        HashFactory<String> factoryH = new StringHash();
        ChainedHashTable<String, Long> chan = new ChainedHashTable<>(factoryH, k, 1);
        int maxLoadFactor = 1;
        return helper3(chan, maxLoadFactor);
    }

    // & 12813.8 & 11677.0
    private static Pair<Double, Double> helper3(HashTable<String, Long> hash, double maxLoadFactor) {
        long endTime;
        long startTime;
        long averageTimeSearch = 0;
        long averageTimeInsert = 0;
        int size = (int) (((maxLoadFactor * hash.capacity())));
        // Creating an array with random items
        HashingUtils h1 = new HashingUtils();
        List<String> arr = h1.genUniqueStrings((int) (((maxLoadFactor * hash.capacity())) * (1.5)), 10, 20);
        // Insert all the items until 'maxLoadFactor'
        for (long i = 0; i < size; i++) {
            startTime = System.nanoTime();
            hash.insert(arr.get((int) i), i);
            endTime = System.nanoTime();
            averageTimeInsert += (endTime - startTime);
        }

        // Search 2^16 * maxLoadFactor when 50% success and 50% fail.
        for (int i = 0; i < (size >> 1); i++) {
            startTime = System.nanoTime();
            hash.search(arr.get((int) i));
            endTime = System.nanoTime();
            averageTimeSearch += (endTime - startTime);
        }
        for (int i = size; i < arr.size(); i++) {
            startTime = System.nanoTime();
            hash.search(arr.get((int) i));
            ;
            endTime = System.nanoTime();
            averageTimeSearch += (endTime - startTime);
        }

        Double outputInsert = (double) (averageTimeInsert / size);
        Double outputSearch = (double) (averageTimeSearch / size);
        return new Pair<>(outputInsert, outputSearch);
    }

    public static void main(String[] args) {






            System.out.println("this checker was written by the Legendary Tomer Cohen.");
            System.out.println("what do you want to check?");
            System.out.println("1. probing");
            System.out.println("2. chaining");
            System.out.println("3. long and string");
            Scanner scanner = new Scanner(System.in);
            int input = scanner.nextInt();
            if (input == 1) {
                double[] a = {0.5, 0.75, 7.0 / 8.0, 15.0 / 16.0};
                double[] insertionsTimes = new double[4];
                double[] searchesTimes = new double[4];
                for (int i = 0; i < 4; i++) {
                    System.out.println("checking a=" + a[i]);
                    double averageInsertion = 0;
                    double averageSearch = 0;
                    for (int j = 0; j < 30; j++) {
                        System.out.println("check #" + (j + 1));
                        Pair<Double, Double> pa = measureOperationsProbing(a[i]);
                        averageInsertion += pa.first();
                        averageSearch += pa.second();
                    }
                    insertionsTimes[i] = averageInsertion / 30;
                    searchesTimes[i] = averageSearch / 30;
                }
                Printer(insertionsTimes, searchesTimes, a, "Linear Probing");

            }
            if (input == 2) {
                double[] a = {0.5, 0.75, 1, 3.0 / 2.0, 2};
                double[] insertionsTimes = new double[5];
                double[] searchesTimes = new double[5];
                for (int i = 0; i < 5; i++) {
                    System.out.println("checking a=" + a[i]);
                    double averageInsertion = 0;
                    double averageSearch = 0;
                    for (int j = 0; j < 30; j++) {
                        System.out.println("check #" + (j + 1));
                        Pair<Double, Double> pa = measureOperationsChained(a[i]);
                        averageInsertion += pa.first();
                        averageSearch += pa.second();
                    }
                    insertionsTimes[i] = averageInsertion / 30;
                    searchesTimes[i] = averageSearch / 30;
                }
                Printer(insertionsTimes, searchesTimes, a, "Chaining");
            }
            if (input == 3) {
                System.out.println("checking long");
                double[] insertionsTimes = new double[2];
                double[] searchesTimes = new double[2];
                double averageInsertion = 0;
                double averageSearch = 0;
                for (int j = 0; j < 10; j++) {
                    System.out.println("check #" + (j + 1));
                    Pair<Double, Double> pa = measureLongOperations();
                    averageInsertion += pa.first();
                    averageSearch += pa.second();
                }
                insertionsTimes[0] = averageInsertion / 10;
                searchesTimes[0] = averageSearch / 10;

                System.out.println("checking String");
                averageInsertion = 0;
                averageSearch = 0;
                for (int j = 0; j < 10; j++) {
                    System.out.println("check #" + (j + 1));
                    Pair<Double, Double> pa = measureStringOperations();
                    averageInsertion += pa.first();
                    averageSearch += pa.second();
                }
                insertionsTimes[1] = averageInsertion / 10;
                searchesTimes[1] = averageSearch / 10;


                Printer2(insertionsTimes, searchesTimes);
            }

        }



        private static void Printer(double[] insert,double[] search,double[] a,String name){
            System.out.println("                                                                        "+name);
            for(int i=0;i<insert.length;i++) {
                System.out.println("|-------------------------------------------------------------------------------------------------------------------------|");
                System.out.println("|   a="+a[i]+"       |    Average Insertion="+insert[i]+"     |   Average Search="+search[i]);
            }
            System.out.println("|-------------------------------------------------------------------------------------------------------------------------|");
        }
        private static void Printer2(double[] insert,double[] search){
            System.out.println("|-------------------------------------------------------------------------------------------------------------------------|");
            System.out.println("|   Long       |    Average Insertion="+insert[0]+"     |   Average Search="+search[0]);
            System.out.println("|-------------------------------------------------------------------------------------------------------------------------|");
            System.out.println("|   String       |    Average Insertion="+insert[1]+"     |   Average Search="+search[1]);

            System.out.println("|-------------------------------------------------------------------------------------------------------------------------|");
        }

    }
