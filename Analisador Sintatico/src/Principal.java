public class Principal {
    public static void main(String[] args) {
        Analex lex = new Analex(args[0]);

        Parser parser = new Parser(lex);

        parser.program();

    }
}
