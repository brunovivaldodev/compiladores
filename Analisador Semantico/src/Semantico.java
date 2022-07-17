import java.util.ArrayList;
import java.util.List;

public class Semantico {

    private final static int Tamanho_Buffer = 10;
    List<Token> bufferTokens;
    Analex lex;
    boolean isFinal = false;
    String tipo;
    String valor;
    int escopo = 0 ;

    TabelaDeSimbolos tabelaDeSimbolos = new TabelaDeSimbolos();


    ArrayList<String> ErrosSemanticos = new ArrayList<String>();


    public void Gravar_ErrosSemantico(String token) {
        ErrosSemanticos.add(token);
    }

    public void Listar_ErrosSemantico() {
        for (String token : ErrosSemanticos) {
            System.out.println(token);
        }
    }

    public Semantico(Analex lex) {
        this.lex = lex;
        bufferTokens = new ArrayList<>();
        lerToken();
    }

    private void lerToken() {
        if (bufferTokens.size() > 0) {
            bufferTokens.remove(0);
        }

        while (bufferTokens.size() < Tamanho_Buffer && !isFinal) {
            Token proximo = lex.proximoToken();
            if (proximo.nome == TipoToken.Comentario) {
            } else {
                bufferTokens.add((proximo));

                if (proximo.nome == TipoToken.Fim) {
                    isFinal = true;
                }
            }


        }
        // System.out.println("Lido" + lookahead(1));
    }

    Token lookahead(int k) {
        if (bufferTokens.isEmpty()) {
            return null;
        }
        if (k - 1 >= bufferTokens.size()) {
            return bufferTokens.get(bufferTokens.size() - 1);
        }
        return bufferTokens.get(k - 1);
    }

    void match(TipoToken tipo) {
        if (lookahead(1).nome == tipo) {
            lerToken();
        } else {
            erroSintatico(tipo.toString());
        }
    }

    void match2(TipoToken tipo) {
        if (lookahead(1).nome == tipo) {
            lerToken();
        } else {
            erroSintatico(tipo.toString());
        }
    }

    void erroSintatico(String... tokensEsperados) {
        String mensagem = "Erro sintatico: esperando um dos seguintes (";

        for (int i = 0; i < tokensEsperados.length; i++) {
            mensagem += tokensEsperados[i];
            if (i < tokensEsperados.length - 1) {
                mensagem += ",";
            }
        }
        mensagem += "), mas foi encontrado" + lookahead(1);

        throw new RuntimeException(mensagem);
    }


    //1. program → declList

    public void program() {
        declList();
    }

    //2.1 declList →  decl (declList | <<vazio>>)
    private void declList() {
        decl();
        declListSubRegra1();
    }

    private void declListSubRegra1() {
        if (lookahead(3).nome == TipoToken.SinPontEVir) {
            declList();
        } else {

        }
    }

    //3. decl → varDecl | funDecl

    private void decl() {
        if (lookahead(3).nome == TipoToken.AbrePar) {
            funDecl();
        } else if (lookahead(1).nome == TipoToken.PCVoid || lookahead(1).nome == TipoToken.PCChar || lookahead(1).nome == TipoToken.PCInt || lookahead(1).nome == TipoToken.PCLong || lookahead(1).nome == TipoToken.PCFloat) {
            varDecl();
        } else {
            erroSintatico(TipoToken.AbrePar.toString(), TipoToken.PCVoid.toString(), TipoToken.PCChar.toString(), TipoToken.PCInt.toString(), TipoToken.PCLong.toString(), TipoToken.PCFloat.toString());
        }
    }

    // 4. varDecl → typeSpec varDeclList ;
    private void varDecl() {
        typeSpec();
        varDeclList();
        match(TipoToken.SinPontEVir);
    }

    // 5. scopedVarDecl → static typeSpec varDeclList ; | typeSpec varDeclList;

    private void scopedVarDecl() {
        if (lookahead(1).nome == TipoToken.PCStatic) {
            match(TipoToken.PCStatic);
            typeSpec();
            varDeclList();
            match(TipoToken.SinPontEVir);
        }
        else if(lookahead(1).nome == TipoToken.PCVoid || lookahead(1).nome == TipoToken.PCChar || lookahead(1).nome == TipoToken.PCInt || lookahead(1).nome == TipoToken.PCLong || lookahead(1).nome == TipoToken.PCFloat)  {
            typeSpec();
            varDeclList();
            match(TipoToken.SinPontEVir);
        }

        else {

        }
    }

    //9. typeSpec → int  | char | void |float |long
    private void typeSpec() {
        if (lookahead(1).nome == TipoToken.PCVoid) {
            if(tabelaDeSimbolos.existe(lookahead(2).lexema.toString()) && tabelaDeSimbolos.getSimbolo(lookahead(2).lexema.toString()).escopo == escopo){

                Gravar_ErrosSemantico("variavel " + lookahead(2).lexema.toString() +" já declarada");
            }
            tipo = lookahead(1).nome.toString();
            match(TipoToken.PCVoid);
            tabelaDeSimbolos.adicionar(lookahead(1).lexema.toString(), TabelaDeSimbolos.Tipos.PCVoid,escopo);
        } else if (lookahead(1).nome == TipoToken.PCChar) {
            if(tabelaDeSimbolos.existe(lookahead(2).lexema.toString()) && tabelaDeSimbolos.getSimbolo(lookahead(2).lexema.toString()).escopo == escopo){
                Gravar_ErrosSemantico("variavel " + lookahead(2).lexema.toString() +" já declarada");
            }
            tipo = lookahead(1).nome.toString();
            match(TipoToken.PCChar);
            tabelaDeSimbolos.adicionar(lookahead(1).lexema.toString(), TabelaDeSimbolos.Tipos.PCChar,escopo);
        } else if (lookahead(1).nome == TipoToken.PCInt) {
            if(tabelaDeSimbolos.existe(lookahead(2).lexema.toString()) && tabelaDeSimbolos.getSimbolo(lookahead(2).lexema.toString()).escopo == escopo){
                Gravar_ErrosSemantico("variavel " + lookahead(2).lexema.toString() +" já declarada");
            }
            tipo = lookahead(1).nome.toString();
            match(TipoToken.PCInt);
            tabelaDeSimbolos.adicionar(lookahead(1).lexema.toString(), TabelaDeSimbolos.Tipos.PCInt,escopo);
        } else if (lookahead(1).nome == TipoToken.PCLong) {
            if(tabelaDeSimbolos.existe(lookahead(2).lexema.toString()) && tabelaDeSimbolos.getSimbolo(lookahead(2).lexema.toString()).escopo == escopo){
                Gravar_ErrosSemantico("variavel " + lookahead(2).lexema.toString() +" já declarada");
            }
            tipo = lookahead(1).nome.toString();
            match(TipoToken.PCLong);

        } else if (lookahead(1).nome == TipoToken.PCFloat) {
            if(tabelaDeSimbolos.existe(lookahead(2).lexema.toString())){
                Gravar_ErrosSemantico("variavel " + lookahead(2).lexema.toString() +" já declarada");
            }
            tipo = lookahead(1).nome.toString();
            match(TipoToken.PCFloat);
            tabelaDeSimbolos.adicionar(lookahead(1).lexema.toString(), TabelaDeSimbolos.Tipos.PCFloat,escopo);
        } else {
            Gravar_ErrosSemantico("variavel " + lookahead(2).lexema.toString() +" não declarada");
        }
    }


    //6. varDeclList → varDeclList , varDeclInit | varDeclInit
    // 6.1 varDeclList →  varDeclInit (, varDeclList | vazio)

    private void varDeclList() {
        varDeclInit();
        varDeclListSubRegra1();
    }

    private void varDeclListSubRegra1() {
        if (lookahead(3).nome == TipoToken.SinVir) {
            match(TipoToken.SinVir);
            varDeclList();
        } else {

        }
    }

    // 7. varDeclInit → varDeclId
    private void varDeclInit() {
        if (lookahead(2).nome == TipoToken.OPAtribuir) {
            varDeclId();
            match(TipoToken.OPAtribuir);
            simpleExp();
        } else {
            varDeclId();
        }
    }

    // 8. varDeclId → ID
    private void varDeclId() {
        if (lookahead(2).nome == TipoToken.AbreCol) {
            match(TipoToken.Var);
            match(TipoToken.AbreCol);
            match(TipoToken.NumInt);
            match(TipoToken.FechaCol);
        }
        if (lookahead(1).nome == TipoToken.Var) {
            match(TipoToken.Var);
        }
    }

    // 10. funDecl → typeSpec ID ( parms ) stmt
    private void funDecl() {
        typeSpec();
        match(TipoToken.Var);
        match(TipoToken.AbrePar);
        parms();
        match(TipoToken.FechaPar);
        stmt();
    }
    //11. parms → parmList | <<vazio>>

    private void parms() {
        parmList();
    }


    private void parmList() {
        parmTypeList();
        parmListSubRegra1();
    }
    private void parmListSubRegra1() {
        if (lookahead(3).nome == TipoToken.SinPontEVir) {
            match(TipoToken.SinPontEVir);
            parmTypeList();
        } else {

        }
    }

    //13. parmTypeList → typeSpec parmIdList
    private void parmTypeList() {
        typeSpec();
        parmIdList();
    }

    //14. parmIdList → parmIdList , parmId | parmId
    private void parmIdList() {
        parmId();
        parmIdListSubRegra1();
    }

    // 15. parmId → ID | ID [ ]
    private void parmId() {
        if(lookahead(1).nome == TipoToken.Var){
            match(TipoToken.Var);
        }
    }

    private void parmIdListSubRegra1() {
        if (lookahead(1).nome == TipoToken.SinVir) {
            match(TipoToken.SinVir);
            parmTypeList();
        } else {

        }
    }

    // 16. stmt → expStmt | compoundStmt | selectStmt | iterStmt | returnStmt | breakStmt
    private void stmt() {
        if (lookahead(1).nome == TipoToken.Var) {
            expStmt();
        } else if (lookahead(1).nome == TipoToken.AbreCha) {
            compoundStmt();
        } else if (lookahead(1).nome == TipoToken.PCIf) {
            selectStmt();
        } else if (lookahead(1).nome == TipoToken.PCWhile || lookahead(1).nome == TipoToken.PCFor) {
            iterStmt();
        } else if (lookahead(1).nome == TipoToken.PcReturn) {
            returnStmt();
        } else if (lookahead(1).nome == TipoToken.PCbreak) {
            breakStmt();
        }
    }

    private void returnStmt() {
        if (lookahead(2).nome == TipoToken.OpAritSub || lookahead(2).nome == TipoToken.OpAritMult || lookahead(2).nome == TipoToken.OpCond || lookahead(2).nome == TipoToken.AbrePar || lookahead(2).nome == TipoToken.NumInt || lookahead(2).nome == TipoToken.String || lookahead(2).nome == TipoToken.NumReal || lookahead(2).nome == TipoToken.AbrePar || lookahead(2).nome == TipoToken.SinVir || lookahead(2).nome == TipoToken.Var) {
            match(TipoToken.PcReturn);
            exp();
            match(TipoToken.SinPontEVir);
        } else if (lookahead(1).nome == TipoToken.PcReturn) {
            match(TipoToken.PcReturn);
            match(TipoToken.SinPontEVir);
        }

    }

    private void breakStmt() {
        match(TipoToken.PCbreak);
        match(TipoToken.SinPontEVir);
    }


    // 17. expStmt → exp ; | ;
    private void expStmt() {
        if (lookahead(1).nome == TipoToken.Var) {
            exp();
            match(TipoToken.SinPontEVir);
        } else if (lookahead(1).nome == TipoToken.SinPontEVir) {
            match(TipoToken.SinPontEVir);
        } else {
            erroSintatico(TipoToken.Var.toString(), TipoToken.SinVir.toString());
        }
    }

    // 26.1 exp → mutable ( = exp |  += exp | −= exp | ∗= exp | /= exp | ++ |  −− ) | simpleExp
    private void exp() {
        if (lookahead(1).nome == TipoToken.OpAritSub || lookahead(1).nome == TipoToken.OpDif|| lookahead(1).nome == TipoToken.OpAritMult || lookahead(1).nome == TipoToken.OpCond || lookahead(1).nome == TipoToken.AbrePar || lookahead(1).nome == TipoToken.NumInt || lookahead(1).nome == TipoToken.String || lookahead(1).nome == TipoToken.NumReal || lookahead(2).nome == TipoToken.AbrePar || lookahead(1).nome == TipoToken.SinVir) {
            simpleExp();
        } else if (lookahead(1).nome == TipoToken.Var) {
            mutable();
            expSubRegra1();
        } else {

        }
    }


    private void expSubRegra1() {
        if (lookahead(1).nome == TipoToken.OPAtribuir) {
            match(TipoToken.OPAtribuir);
            exp();
        } else if (lookahead(1).nome == TipoToken.OpSomaCom) {
            match(TipoToken.OpSomaCom);
            exp();
        } else if (lookahead(1).nome == TipoToken.OpSubCom) {
            match(TipoToken.OpSubCom);
            exp();
        } else if (lookahead(1).nome == TipoToken.OpMultCom) {
            match(TipoToken.OpMultCom);
            exp();
        } else if (lookahead(1).nome == TipoToken.OpDivCom) {
            match(TipoToken.OpDivCom);
            exp();
        } else if (lookahead(1).nome == TipoToken.OpInc) {
            match(TipoToken.OpInc);
        } else if (lookahead(1).nome == TipoToken.OpDec) {
            match(TipoToken.OpDec);
        } else {
            simpleExp();
        }
    }

    //41. mutable → ID | ID [ exp ]
    private void mutable() {
        if (lookahead(2).nome == TipoToken.AbreCol) {
            match(TipoToken.Var);
            match(TipoToken.AbreCol);
            exp();
            match(TipoToken.FechaCol);
        } else {
            if(!tabelaDeSimbolos.existe(lookahead(1).lexema.toString())){
                Gravar_ErrosSemantico("variavel " + lookahead(1).lexema.toString() +" não declarada");
            }
            match(TipoToken.Var);
        }
    }

    // 27.  simpleExp → simpleExp or andExp | andExp
    // 27.1 simpleExp → andExp simpleExpSubRegra1
    private void simpleExp() {
        andExp();
        simpleExpSubRegra1();
    }

    // 27.2 simpleExpSubRegra1 -> or andExp simpleExpSubRegra1 | <<vazio>>

    private void simpleExpSubRegra1() {
        if (lookahead(1).nome == TipoToken.OpBoolOu) {
            match(TipoToken.OpBoolOu);
            andExp();
            simpleExpSubRegra1();
        } else {

        }
    }


    // 28.1 andExp → unaryRelExp andExpSubRegra1
    private void andExp() {
        unaryRelExp();
        andExpSubRegra1();
    }

    // 28.2 andExpSubRegra1 -> and unaryRelExp  andExpSubRegra1 | <<vazio>>
    private void andExpSubRegra1() {
        if (lookahead(1).nome == TipoToken.OpBoolE) {
            match(TipoToken.OpBoolE);
            unaryRelExp();
            andExpSubRegra1();
        } else {

        }

    }


    // 29. unaryRelExp → not unaryRelExp | relExp
    private void unaryRelExp() {
        if (lookahead(1).nome == TipoToken.OpDif) {
            match(TipoToken.OpDif);
            unaryRelExp();
        } else if (lookahead(1).nome == TipoToken.OpAritSub || lookahead(1).nome == TipoToken.OpAritSoma || lookahead(1).nome == TipoToken.AbrePar || lookahead(1).nome == TipoToken.NumInt || lookahead(1).nome == TipoToken.OpAritMult || lookahead(1).nome == TipoToken.OpCond || lookahead(1).nome == TipoToken.Var || lookahead(1).nome == TipoToken.String || lookahead(1).nome == TipoToken.NumReal || lookahead(1).nome == TipoToken.OpRelMaior) {
            relExp();
        }
    }

    //30. relExp → minmaxExp relop minmaxExp | minmaxExp
    private void relExp() {
        if (lookahead(2).nome == TipoToken.OpRelMenorIgual || lookahead(2).nome == TipoToken.OpRelMenor || lookahead(2).nome == TipoToken.OpRelMaior || lookahead(2).nome == TipoToken.OpRelMaiorIgual || lookahead(2).nome == TipoToken.OpRelIgual ||lookahead(2).nome == TipoToken.OpRelDif) {
            minmaxExp();
            relop();
            minmaxExp();
        } else {
            minmaxExp();
        }

    }

    // 31. relop → <= | < | > | >= | == | !=
    private void relop() {
        if (lookahead(1).nome == TipoToken.OpRelMenorIgual) {
            match(TipoToken.OpRelMenorIgual);
        } else if (lookahead(1).nome == TipoToken.OpRelMenor) {
            match(TipoToken.OpRelMenor);
        } else if (lookahead(1).nome == TipoToken.OpRelMaior) {
            match(TipoToken.OpRelMaior);
        } else if (lookahead(1).nome == TipoToken.OpRelMaiorIgual) {
            match(TipoToken.OpRelMaiorIgual);
        } else if (lookahead(1).nome == TipoToken.OpRelIgual) {
            match(TipoToken.OpRelIgual);
        } else if (lookahead(1).nome == TipoToken.OpRelDif) {
            match(TipoToken.OpRelDif);
        } else {

        }
    }

    //32.1 minmaxExp → sumExp (minmaxExp | <<vazio>>)
    private void minmaxExp() {
        sumExp();
        minmaxExpSubRegra1();
    }

    private void minmaxExpSubRegra1() {
        if (lookahead(1).nome == TipoToken.OpAritSub || lookahead(1).nome == TipoToken.AbrePar || lookahead(1).nome == TipoToken.NumInt || lookahead(1).nome == TipoToken.OpAritMult || lookahead(1).nome == TipoToken.OpCond || lookahead(1).nome == TipoToken.Var || lookahead(1).nome == TipoToken.String || lookahead(1).nome == TipoToken.NumReal) {
            minmaxExp();
        } else {

        }
    }


    //34.1 sumExp → mulExp sumExpSubRegra1
    //34.2 sumExpSubRegra1 -> sumop mulExp sumExpSubRegra1 | <<vazio>>
    private void sumExp() {
        mulExp();
        sumExpSubRegra1();
    }

    private void sumExpSubRegra1() {
        if (lookahead(1).nome == TipoToken.OpAritSoma || lookahead(1).nome == TipoToken.OpAritSub) {
            sumop();
            mulExp();
            sumExpSubRegra1();
        } else {

        }
    }

    //35. sumop → + | −

    private void sumop() {
        if (lookahead(1).nome == TipoToken.OpAritSoma) {
            match2(TipoToken.OpAritSoma);
        } else if (lookahead(1).nome == TipoToken.OpAritSub) {
            match2(TipoToken.OpAritSub);
        } else {
            erroSintatico(TipoToken.OpAritSoma.toString(), TipoToken.OpAritSub.toString());
        }
    }

    //36.1 mulExp → unaryExp mulExpSubRegra1
    //36.2 mulExpSubRegra1 -> mulop unaryExp mulExpSubRegra1 | <<vazio>>
    private void mulExp() {
        unaryExp();
        mulExpSubRegra1();
    }

    //36.2 mulExpSubRegra1 -> mulop unaryExp mulExpSubRegra1 | <<vazio>>
    private void mulExpSubRegra1() {
        if (lookahead(1).nome == TipoToken.OpAritMult || lookahead(1).nome == TipoToken.OpAritDiv) {
            mulop();
            unaryExp();
            mulExpSubRegra1();
        } else {

        }
    }

    //37. mulop → ∗ | / | %
    private void mulop() {
        if (lookahead(1).nome == TipoToken.OpAritMult) {
            match(TipoToken.OpAritMult);
        } else if (lookahead(1).nome == TipoToken.OpAritDiv) {
            match(TipoToken.OpAritDiv);
        } else {
            erroSintatico(TipoToken.OpAritDiv.toString(), TipoToken.OpAritMult.toString());
        }

    }

    //38. unaryExp → unaryop unaryExp | factor

    private void unaryExp() {
        if (lookahead(1).nome == TipoToken.OpAritSub || lookahead(1).nome == TipoToken.OpAritMult || lookahead(1).nome == TipoToken.OpCond) {
            unaryop();
            unaryExp();
        } else if (lookahead(1).nome == TipoToken.Var || lookahead(1).nome == TipoToken.AbrePar || lookahead(1).nome == TipoToken.NumInt || lookahead(1).nome == TipoToken.Var || lookahead(1).nome == TipoToken.String || lookahead(1).nome == TipoToken.NumReal) {
            factor();
        } else {

        }
    }

    // 39. unaryop → − | ∗ | ?
    private void unaryop() {
        if (lookahead(1).nome == TipoToken.OpAritSub) {
            match(TipoToken.OpAritSub);
        } else if (lookahead(1).nome == TipoToken.OpAritMult) {
            match(TipoToken.OpAritMult);
        } else if (lookahead(1).nome == TipoToken.OpCond) {
            match(TipoToken.OpCond);
        } else {
            erroSintatico(TipoToken.OpAritSub.toString(), TipoToken.OpAritMult.toString(), TipoToken.OpCond.toString());
        }
    }

    //40. factor → immutable | mutable
    private void factor() {
        if (lookahead(1).nome == TipoToken.AbrePar || lookahead(1).nome == TipoToken.NumInt || lookahead(1).nome == TipoToken.NumReal || lookahead(1).nome == TipoToken.String) {
            immutable();
        } else if (lookahead(1).nome == TipoToken.Var) {
            mutable();
        } else {
            erroSintatico(TipoToken.AbrePar.toString(), TipoToken.Var.toString());
        }
    }

    // 42. immutable → ( exp ) | call | constant
    private void immutable() {
        if (lookahead(1).nome == TipoToken.AbrePar) {
            match(TipoToken.AbrePar);
            exp();
            match(TipoToken.FechaPar);
        } else if (lookahead(1).nome == TipoToken.Var) {
            call();
        } else if (lookahead(1).nome == TipoToken.NumInt || lookahead(1).nome == TipoToken.NumReal || lookahead(1).nome == TipoToken.String) {
            constant();
        } else {
            erroSintatico(TipoToken.AbrePar.toString(), TipoToken.Var.toString());
        }

    }

    // 46. constant → NUMINT | NUMREAL | CHARCONST | STRINGCONST
    private void constant() {
        if (lookahead(1).nome == TipoToken.NumInt) {
            match(TipoToken.NumInt);
        } else if (lookahead(1).nome == TipoToken.NumReal) {
            match(TipoToken.NumInt);
        } else if (lookahead(1).nome == TipoToken.String) {
            match(TipoToken.String);
        } else {
            erroSintatico(TipoToken.NumInt.toString(), TipoToken.NumReal.toString(), TipoToken.String.toString());
        }
    }

    //43. call → ID ( args )
    private void call() {
        match(TipoToken.Var);
        match(TipoToken.AbrePar);
        args();
        match(TipoToken.FechaPar);

    }

    //44. args → argList | <<vazio>>
    private void args() {
        argList();
    }

    //45.1 argList → exp(, argList || <<vazio>>)
    private void argList() {
        exp();
        argListSubRegra1();
    }

    private void argListSubRegra1() {
        if (lookahead(1).nome == TipoToken.SinVir) {
            match(TipoToken.SinVir);
            argList();
        } else {

        }
    }


    // 18. compoundStmt → { localDecls stmtList }
    private void compoundStmt() {
        match(TipoToken.AbreCha);
        escopo++;
        localDecls();
        stmtList();
        match(TipoToken.FechaCha);
    }

    // 19.1 localDecls →  scopedVarDecl ( localDecls | <<vazio>>)
    private void localDecls() {
        scopedVarDecl();
        localDeclsSubRegra1();
    }

    private void localDeclsSubRegra1() {
        if (lookahead(1).nome == TipoToken.PCInt ||lookahead(1).nome ==  TipoToken.PCVoid || lookahead(1).nome == TipoToken.PCChar || lookahead(1).nome == TipoToken.PCLong || lookahead(1).nome == TipoToken.PCFloat) {
            localDecls();

        } else {

        }
    }

    // 20.2 stmtList → stmt ( stmtList | <<vazio>> )

    private void stmtList() {
        stmt();
        stmtListSubRegra1();
    }

    private void stmtListSubRegra1() {
        if (lookahead(1).nome == TipoToken.Var || lookahead(1).nome == TipoToken.AbreCha || lookahead(1).nome == TipoToken.NumInt || lookahead(1).nome == TipoToken.PCIf || lookahead(1).nome == TipoToken.PCWhile || lookahead(1).nome == TipoToken.PCFor || lookahead(1).nome == TipoToken.PcReturn) {
            stmtList();
        } else {

        }
    }

    //21. selectStmt → if simpleExp then stmt | if simpleExp then stmt else stmt
    // 21.1 selectStmt → if simpleExp then stmt (else stmt | <<vazio>>)
    private void selectStmt() {
        match(TipoToken.PCIf);
        simpleExp();
        match(TipoToken.PCThen);
        stmt();
        selectStmtSubRegra1();
    }

    private void selectStmtSubRegra1() {
        if (lookahead(1).nome == TipoToken.PCElse) {
            match(TipoToken.PCElse);
            stmt();
        } else {
        }
    }

    //22. iterStmt → while simpleExp do stmt | for ID = iterRange do stmt
    private void iterStmt() {
        if (lookahead(1).nome == TipoToken.PCWhile) {
            match(TipoToken.PCWhile);
            simpleExp();
            match(TipoToken.PCDo);
            stmt();
        } else if (lookahead(1).nome == TipoToken.PCFor) {
            match(TipoToken.PCFor);
            match(TipoToken.Var);
            match(TipoToken.OPAtribuir);
            iterRange();
            match(TipoToken.PCDo);
            stmt();

        }
    }

    private void iterRange() {
        simpleExp();
        match(TipoToken.PCTO);
        simpleExp();
    }

}


/*
1. program → declList

2. declList → declList decl | decl
2.1 declList →  decl (declList | <<vazio>>)

3. decl → varDecl | funDecl
4. varDecl → typeSpec varDeclList ;

5. scopedVarDecl → static typeSpec varDeclList ; | typeSpec varDeclList;

6. varDeclList → varDeclList , varDeclInit | varDeclInit
6.1 varDeclList →  varDeclInit (, varDeclList | <<vazio>>)

7. varDeclInit → varDeclId

8. varDeclId → ID

9. typeSpec ->  int | bool | char | void |float |long

10. funDecl → typeSpec ID ( parms ) stmt
11. parms → parmList | <<vazio>>
12. parmList → parmList ; parmTypeList | parmTypeList
12.1 parmList → parmTypeList (; parmTypeList | <<vazio>>)

13. parmTypeList → typeSpec parmIdList

14. parmIdList → parmIdList , parmId | parmId
14.1 parmIdList → parmId (parmIdList , | <<vazio>>)

15. parmId → ID | ID [ ]

16. stmt → expStmt | compoundStmt | selectStmt | iterStmt | returnStmt | breakStmt

17. expStmt → exp ; | ;

18. compoundStmt → { localDecls stmtList }

19. localDecls → localDecls scopedVarDecl | <<vazio>>
19.1 localDecls → localDecls scopedVarDecl | scopedVarDecl
19.2 localDecls →  scopedVarDecl ( localDecls | <<vazio>>)

20. stmtList → stmtList stmt | <<vazio>>
20.1 stmtList → stmtList stmt | stmt
20.2 stmtList → stmt ( stmtList | <<vazio>> )

21. selectStmt → if simpleExp then stmt | if simpleExp then stmt else stmt
21.1 selectStmt → if simpleExp then stmt (else stmt | <<vazio>>)

26. exp → mutable = exp | mutable += exp | mutable −= exp | mutable ∗= exp | mutable /= exp | mutable ++ | mutable −− | simpleExp
26.1 exp → mutable ( = exp |  += exp | −= exp | ∗= exp | /= exp | ++ |  −− ) | simpleExp

27.  simpleExp → simpleExp or andExp | andExp
27.1 simpleExp → andExp simpleExpSubRegra1
27.2 simpleExpSubRegra1 -> or andExp simpleExpSubRegra1 | <<vazio>>

28. andExp → andExp and unaryRelExp | unaryRelExp
28.1 andExp → unaryRelExp andExpSubRegra1
28.2 andExpSubRegra1 -> and unaryRelExp  andExpSubRegra1 | <<vazio>>

29. unaryRelExp → not unaryRelExp | relExp

30. relExp → minmaxExp relop minmaxExp | minmaxExp

31. relop → <= | < | > | >= | == | !=

32. minmaxExp → minmaxExp sumExp | sumExp
32.1 minmaxExp → sumExp (minmaxExp | <<vazio>>)

34. sumExp → sumExp sumop mulExp | mulExp
34.1 sumExp → mulExp sumExpSubRegra1
34.2 sumExpSubRegra1 -> sumop mulExp sumExpSubRegra1 | <<vazio>>

35. sumop → + | −

36. mulExp → mulExp mulop unaryExp | unaryExp
36.1 mulExp → unaryExp mulExpSubRegra1
36.2 mulExpSubRegra1 -> mulop unaryExp mulExpSubRegra1 | <<vazio>>

37. mulop → ∗ | / | %

38. unaryExp → unaryop unaryExp | factor

39. unaryop → − | ∗ | ?

40. factor → immutable | mutable

41. mutable → ID | ID [ exp ]

42. immutable → ( exp ) | call | constant

43. call → ID ( args )

44. args → argList | <<vazio>>

45. argList → argList , exp | exp
45.1 argList → exp(argList || <<vazio>>)

46. constant → NUMINT | NUMREAL | CHARCONST | STRINGCONST



*/
