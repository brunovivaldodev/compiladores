public class Principal {
    public static void main(String[] args) {
        Analex lex = new Analex(args[0]);
        Analex lex2 = new Analex(args[0]);

        Parser parser = new Parser(lex);
        Semantico seman = new Semantico(lex2);

        parser.program();
        seman.program();
        parser.Listar_ErrosSintaticos();
        seman.Listar_ErrosSemantico();


    }
}
