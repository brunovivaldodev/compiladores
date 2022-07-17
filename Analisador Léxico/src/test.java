import java.util.ArrayList;

public class test {
        public static void main(String[] args) {
            Analex lex = new Analex(args[0]);
            Token t = null;

            while((t = lex.proximoToken()).nome != TipoToken.Fim) {

            lex.Gravar_Token_Lexema(t) ;

            }

            lex.Listar_Token_Lexema();
            System.out.println(lex.count);


        }

}
