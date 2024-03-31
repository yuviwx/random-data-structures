import java.util.Random;

public class StringHash implements HashFactory<String> {
    private final Random rand;
    public StringHash() {
        rand = new Random();
    }

    @Override
    public HashFunctor<String> pickHash(int k) {
        return new Functor(k);
    }

    public class Functor implements HashFunctor<String> {
        final private HashFunctor<Integer> carterWegmanHash;
        final private int c;
        final private int q;

        public Functor(int k){
            this.c = 7;
            this.q = generatePrime();
            this.carterWegmanHash = new ModularHash().pickHash(k);
        }
        public int generatePrime(){
            int q = rand.nextInt(Integer.MAX_VALUE/2+1,Integer.MAX_VALUE);
            HashingUtils h1 = new HashingUtils();
            while(!h1.runMillerRabinTest(q,50)){
                q = rand.nextInt(Integer.MAX_VALUE/2+1,Integer.MAX_VALUE);
            }
            return q;
        }
        @Override
        public int hash(String key) {
           int hashValue;
           int strToInt = stringToInt(key);
           hashValue = carterWegmanHash.hash(strToInt);
           return hashValue;
        }

        public int stringToInt(String s){
            int k = s.length();
            long output = 0;
            for(int i = 0; i < s.length(); i++){
                output += HashingUtils.multiplyMod(s.charAt(i),HashingUtils.modPow(this.c, k-i, q), q);
            }
            output = HashingUtils.mod(output, q);
            return (int)(output);
        }

        public int c() {
            return c;
        }

        public int q() {
            return q;
        }

        public HashFunctor<Integer> carterWegmanHash() {
            return carterWegmanHash;
        }
    }
}
