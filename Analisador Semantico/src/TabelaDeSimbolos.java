
import java.util.ArrayList;

public class TabelaDeSimbolos {

    ArrayList<EntradaTabelaDeSimbolos> tabela = new ArrayList<EntradaTabelaDeSimbolos>();

    public enum Tipos {
        PCVoid,
        PCChar,
        PCInt,
        PCFloat,
        INVALIDO
    }

    class EntradaTabelaDeSimbolos {
        String nome;
        Tipos tipo;
        int escopo ;

        private EntradaTabelaDeSimbolos(String nome, Tipos tipo, int escopo) {
            this.nome = nome;
            this.tipo = tipo;
            this.escopo = escopo;
        }
    }



    public void adicionar(String nome, Tipos tipo, int escopo) {
        tabela.add(new EntradaTabelaDeSimbolos(nome, tipo, escopo));
    }


    public boolean existe(String nome) {
        for (EntradaTabelaDeSimbolos tabelasimbolos : tabela) {
           if(tabelasimbolos.nome.equals(nome) ) {
               return true;
           }
        }
        return false;
    }

    public EntradaTabelaDeSimbolos getSimbolo(String nome) {
        for (EntradaTabelaDeSimbolos tabelasimbolos : tabela) {
            if(tabelasimbolos.nome.equals(nome) ) {
                return tabelasimbolos;
            }
        }
        return null;
    }

    public void listar() {
        for (EntradaTabelaDeSimbolos tabelasimbolos : tabela) {
            System.out.printf(tabelasimbolos.nome + tabelasimbolos.tipo);
        }
    }


}