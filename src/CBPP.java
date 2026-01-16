public class CBPP {
    public static void main(String[] args) {
        System.out.println(new Preprocessor(new Scanner("define A \"A\"; ifdef A {e{}}").scan()).execute());
    }
}
