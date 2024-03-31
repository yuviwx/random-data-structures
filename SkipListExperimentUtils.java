import java.util.Arrays;
import java.util.Random;

public class SkipListExperimentUtils {
    public static double measureLevels(double p, int x) {
        double sum = 0;
        IndexableSkipList n1 = new IndexableSkipList(p);
        for (int i = 0; i < x; i++)
            sum += n1.generateHeight();
        return sum / x + 1;
    }
    /**
     * @return a random array with 10 items that is built from numbers from [0,100)
     */
    public static int[] generate_random_array() {
        int[] random_array = new int[10];
        for (int i = 0; i < random_array.length; i++)
            random_array[i] = (int) (Math.random() * 100);
        return random_array;
    }

    /*
     * The experiment should be performed according to these steps:
     * 1. Create the empty Data-Structure.
     * 2. Generate a randomly ordered list (or array) of items to insert.
     *
     * 3. Save the start time of the experiment (notice that you should not
     *    include the previous steps in the time measurement of this experiment).
     * 4. Perform the insertions according to the list/array from item 2.
     * 5. Save the end time of the experiment.
     *
     * 6. Return the DS and the difference between the times from 3 and 5.
     */
    /**
     * @param size - the requested array size.
     * @return - An array containing the values {0,2,...,2*size} ordered randomly.
     */
    public static int[] randomEvenArray(int size) {
        int size2 = size * 2;
        Random random = new Random();
        int[] randomArr = new int[size+1];

        // Create the array(0,2,...,2*size)
        for (int i = 0; i < size+1; i++)
            randomArr[i] = i * 2;

        // Shuffle the values
        for (int i = randomArr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = randomArr[i];
            randomArr[i] = randomArr[j];
            randomArr[j] = temp;
        }

        return randomArr;
    }
    /**
     * @param size - the requested array size.
     * @return - An array containing the values {0,1,2,...,2*size} ordered randomly.
     */
    public static int[] randomAllArray(int size) {
        Random random = new Random();
        int[] randomArr = new int[size*2+1];

        // Create the array(0,1,2,...,2*size)
        for (int i = 0; i < size*2+1; i++)
            randomArr[i] = i;

        // Shuffle the values
        for (int i = randomArr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = randomArr[i];
            randomArr[i] = randomArr[j];
            randomArr[j] = temp;
        }

        return randomArr;
    }

    /**
     * @param skiplist - AbstractSkipList.
     * @param size     - the half value of the biggest key we will take
     * @return - An array of 'skiplist' Nodes ordered randomly of values <= size*2.
     */
    public static AbstractSkipList.Node[] randomNodeArray (AbstractSkipList skiplist, int size){
        AbstractSkipList.Node[] randomArr = new AbstractSkipList.Node[size+1];
        // Get all the nodes that their keys are in {0,2,...,2*size}
        int m = 0;
        for (AbstractSkipList.Node curr = skiplist.head.getNext(0); curr.key() < Integer.MAX_VALUE; curr = curr.getNext(0), m++)
            randomArr[m] = curr;

        /*Random random = new Random();
        for (int i = randomArr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            AbstractSkipList.Node temp = randomArr[i];
            randomArr[i] = randomArr[j];
            randomArr[j] = temp;
        }*/
        return randomArr;
    }

    /**
     *
     * @param p - probability p
     * is the number of coin tosses until the first
     * success ("head") (including), with probability p for success.
     * @param size - size of the randomArray + 1.
     * @return - <The abstract skip list,( average Time of insertions ) / ( Total items inserted )>
     */
    public static Pair<AbstractSkipList, Double> measureInsertions(double p, int size) {
        IndexableSkipList s1 = new IndexableSkipList(p);
        int[] inputs = randomEvenArray(size);
        long startTime;
        long endTime;
        long averageTime = 0;

        // Insert and measure each insertion
        for (int input : inputs) {
            startTime = System.nanoTime();
            s1.insert(input);
            endTime = System.nanoTime();
            averageTime += (endTime - startTime);
        }
        return new Pair<>(s1, (double) (averageTime / (size + 1)));
    }

    /**
     *
     * @param skipList - the skip list we are working on
     * @param size - Used to create [0,1,...2*size] array
     * @return - (average Time of searching all items)/(size+1)
     */
    public static double measureSearch(AbstractSkipList skipList, int size) {
        long startTime;
        long endTime;
        long averageTime = 0;
        int [] inputs = randomAllArray(size);

        // Search and measure each insertion
        for (int input : inputs) {
            startTime = System.nanoTime();
            skipList.search(input);
            endTime = System.nanoTime();
            averageTime += (endTime - startTime);
        }
        return (double)(averageTime/(size+1));
    }

    public static double measureDeletions(AbstractSkipList skipList, int size) {
        AbstractSkipList.Node[] inputs = randomNodeArray(skipList,size);
        long startTime;
        long endTime;
        long averageTime = 0;
        for (AbstractSkipList.Node input : inputs){
            startTime = System.nanoTime();
            skipList.delete(input);
            endTime = System.nanoTime();
            averageTime += (endTime - startTime);
        }
        return (double) (averageTime/(size + 1));
    }

    public static void main(String [] args){
        // Initializing arrays for the p and x values
        double [] p = {0.33,0.5,0.75,0.9};
        int [] x = {1000,2500,5000,10000,15000,20000,50000};
        // Initialize pair for outputs
        double sumInsert; double sumSearch; double sumDelete;
        Pair<AbstractSkipList,Double> pairInsert;
        // Iterate over the probabilities.
        for(double p1 : p){
            System.out.println("Measure p: " + p1);
            // Iterate over the X's.
            for(int x1: x){
                sumInsert = 0; sumSearch = 0; sumDelete = 0;
                // loop 30 times to get accurate measures.
                for(int i = 0; i<30; i++)
               {
                   pairInsert = measureInsertions(p1,x1);
                   sumInsert += pairInsert.second();
                   sumSearch += measureSearch(pairInsert.first(),x1);
                   sumDelete += measureDeletions(pairInsert.first(),x1);
               }
               System.out.println(x1 + " & " + (sumInsert/30) + " & " + (sumSearch/30) + " & " + +(sumDelete/30) + " \\" + "\\" + "\n" + "\\hline");
            }
        }

    }
}
