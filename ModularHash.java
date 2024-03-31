import java.util.Random;

public class ModularHash implements HashFactory<Integer> {
    private final Random rand;
    public ModularHash() {
        rand = new Random();
    }

    @Override
    public HashFunctor<Integer> pickHash(int k) {
        int a = rand.nextInt(Integer.MAX_VALUE - 1) + 1;
        int b = rand.nextInt(Integer.MAX_VALUE);
        long p = generatePrime();
        int m = 1 << k;
        return new Functor(a, b, p, m);
    }
    public long generatePrime(){
        long p = rand.nextLong((long)Integer.MAX_VALUE + 1L,Long.MAX_VALUE);
        HashingUtils h1 = new HashingUtils();
        while(!h1.runMillerRabinTest(p,50)){
             p = rand.nextLong((long)Integer.MAX_VALUE + 1L,Long.MAX_VALUE);
        }
        return p;
    }

    public class Functor implements HashFunctor<Integer> {
        final private int a;
        final private int b;
        final private long p;
        final private int m;

        public Functor(int a, int b, long p, int m) {
            this.a = a;
            this.b = b;
            this.p = p;
            this.m = m;
        }
        @Override
        public int hash(Integer key) {
            //Implement
            int output = (int) HashingUtils.mod(HashingUtils.mod((long) a * key + b, p),m);
            if(output < 0){
                output += p;
            }
            return output;
        }

        public int a() {
            return a;
        }

        public int b() {
            return b;
        }

        public long p() {
            return p;
        }

        public int m() {
            return m;
        }
    }
}
