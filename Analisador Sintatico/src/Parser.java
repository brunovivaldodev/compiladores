import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final static int Tamanho_Buffer = 10;
    List<Token> bufferTokens;
    Analex lex;
    boolean isFinal = false;

    public Parser(Analex lex) {
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
            bufferTokens.add((proximo));

            if (proximo.nome == TipoToken.Fim) {
                isFinal = true;
            }
        }
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
            System.out.println("Match" + lookahead(1));
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
            erroSintatico(TipoToken.PCVoid.toString(), TipoToken.PCChar.toString(), TipoToken.PCInt.toString(), TipoToken.PCLong.toString(), TipoToken.PCFloat.toString());
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
        if(lookahead(1).nome == TipoToken.PCStatic){
            match(TipoToken.PCStatic);
            typeSpec();
            varDeclList();
            match(TipoToken.SinPontEVir);
        }
        else {
            typeSpec();
            varDeclList();
            match(TipoToken.SinPontEVir);
        }
    }

    //9. typeSpec → int  | char | void |float |long
    private void typeSpec() {
        if (lookahead(1).nome == TipoToken.PCVoid) {
            match(TipoToken.PCVoid);
        } else if (lookahead(1).nome == TipoToken.PCChar) {
            match(TipoToken.PCChar);
        } else if (lookahead(1).nome == TipoToken.PCInt) {
            match(TipoToken.PCInt);
        } else if (lookahead(1).nome == TipoToken.PCLong) {
            match(TipoToken.PCLong);
        } else if (lookahead(1).nome == TipoToken.PCFloat) {
            match(TipoToken.PCFloat);
        } else {
            erroSintatico(TipoToken.PCVoid.toString(), TipoToken.PCChar.toString(), TipoToken.PCInt.toString(), TipoToken.PCLong.toString(), TipoToken.PCFloat.toString());
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
        varDeclId();
    }

    // 8. varDeclId → ID
    private void varDeclId() {
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

    }


    // 16. stmt → expStmt | compoundStmt | selectStmt | iterStmt | returnStmt | breakStmt
    private void stmt() {
        if(lookahead(1).nome == TipoToken.Var || lookahead(2).nome == TipoToken.Var){
            expStmt();
        }
        else if (lookahead(1).nome == TipoToken.AbreCha){
            compoundStmt();
        }

        else if (lookahead(1).nome == TipoToken.PCIf){
            selectStmt();
        }
    }



    // 17. expStmt → exp ; | ;
    private void expStmt() {
        if(lookahead(1).nome == TipoToken.Var || lookahead(2).nome == TipoToken.Var){
            exp();
            match(TipoToken.SinPontEVir);
        }
        else if(lookahead(1).nome == TipoToken.SinPontEVir){
            match(TipoToken.SinPontEVir);
        }
        else{
            erroSintatico(TipoToken.Var.toString(), TipoToken.SinVir.toString());
        }
    }

    // 26.1 exp → mutable ( = exp |  += exp | −= exp | ∗= exp | /= exp | ++ |  −− ) | simpleExp
    private void exp() {
        if(lookahead(1).nome == TipoToken.Var || lookahead(2).nome == TipoToken.Var){
            mutable();
            expSubRegra1();
        }

        // implement simpleExp

        else {
            erroSintatico();
        }

    }


    private void expSubRegra1() {
        if(lookahead(1).nome == TipoToken.OPAtribuir){
            match(TipoToken.OPAtribuir);
            exp();
        }
        else if(lookahead(1).nome == TipoToken.OpSomaCom){
            match(TipoToken.OpSomaCom);
            exp();
        }
        if(lookahead(1).nome == TipoToken.OpSubCom){
            match(TipoToken.OpSubCom);
            exp();
        }
        if(lookahead(1).nome == TipoToken.OpMultCom){
            match(TipoToken.OpMultCom);
            exp();
        }
        if(lookahead(1).nome == TipoToken.OpDivCom){
            match(TipoToken.OpDivCom);
            exp();
        }
        if(lookahead(1).nome == TipoToken.OpInc){
            match(TipoToken.OpInc);
            exp();
        }
        if(lookahead(1).nome == TipoToken.OpDec){
            match(TipoToken.OpDec);
            exp();
        }
    }

    //41. mutable → ID | ID [ exp ]
    private void mutable() {
        if(lookahead(1).nome == TipoToken.Var || lookahead(2).nome == TipoToken.Var){
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
        if(lookahead(1).nome == TipoToken.OpBoolOu){
            match(TipoToken.OpBoolOu);
            andExp();
            simpleExpSubRegra1();
        }
        else {

        }
    }


    // 28.1 andExp → unaryRelExp andExpSubRegra1
    private void andExp() {
        unaryRelExp();
        andExpSubRegra1();
    }

    // 28.2 andExpSubRegra1 -> and unaryRelExp  andExpSubRegra1 | <<vazio>>
    private void andExpSubRegra1() {
        if (lookahead(1).nome == TipoToken.OpBoolE){
            match(TipoToken.OpBoolE);
            unaryRelExp();
            andExpSubRegra1();
        }
        else {

        }

    }


    // 29. unaryRelExp → not unaryRelExp | relExp
    private void unaryRelExp() {
        if (lookahead(1).nome == TipoToken.OpDif){
            match(TipoToken.OpDif);
            unaryRelExp();
        }
        else if(lookahead(1).nome == TipoToken.OpAritSoma || lookahead(1).nome == TipoToken.OpAritSub){
            relExp();
        }
    }

    //30. relExp → minmaxExp relop minmaxExp | minmaxExp
    private void relExp() {
        if(lookahead(1).nome == TipoToken.OpAritSoma || lookahead(1).nome == TipoToken.OpAritSub){
            minmaxExp();
            relop();
            minmaxExp();
        }
        else {
            minmaxExp();
        }

    }

    // 31. relop → <= | < | > | >= | == | ! =
    private void  relop() {
        if(lookahead(1).nome == TipoToken.OpRelMenorIgual){
            match(TipoToken.OpRelMenorIgual);
        }
        else if (lookahead(1).nome == TipoToken.OpRelMenor){
            match(TipoToken.OpRelMenor);
        }
        else if (lookahead(1).nome == TipoToken.OpRelMaior){
            match(TipoToken.OpRelMaior);
        }
        else if (lookahead(1).nome == TipoToken.OpRelMaiorIgual){
            match(TipoToken.OpRelMaiorIgual);
        }
        else if (lookahead(1).nome == TipoToken.OpRelIgual){
            match(TipoToken.OpRelIgual);
        }
        else if (lookahead(1).nome == TipoToken.OpRelDif){
            match(TipoToken.OpRelDif);
        }
        else {
            erroSintatico(TipoToken.OpRelMenorIgual.toString(),TipoToken.OpRelMenor.toString(),TipoToken.OpRelMaior.toString(),
                    TipoToken.OpRelMaiorIgual.toString(),TipoToken.OpRelIgual.toString(), TipoToken.OpRelDif.toString());
        }
    }

    //32.1 minmaxExp → sumExp (minmaxExp | <<vazio>>)
    private void minmaxExp() {
        sumExp();
        minmaxExpSubRegra1();
    }

    private void minmaxExpSubRegra1(){
        if(lookahead(1).nome == TipoToken.OpAritMult || lookahead(1).nome == TipoToken.OpAritSub || lookahead(1).nome == TipoToken.OpCond ){
            minmaxExp();
        }
        else {

        }
    }


    //34.1 sumExp → mulExp sumExpSubRegra1
    //34.2 sumExpSubRegra1 -> sumop mulExp sumExpSubRegra1 | <<vazio>>
    private void sumExp() {
        mulExp();
        sumExpSubRegra1();
    }

    private void sumExpSubRegra1() {
        if(lookahead(1).nome == TipoToken.OpAritSoma || lookahead(1).nome == TipoToken.OpAritSub){
            sumop();
            mulExp();
            sumExpSubRegra1();
        }
        else {

        }
    }

    //35. sumop → + | −

    private void sumop() {
        if (lookahead(1).nome == TipoToken.OpAritSoma){
            match(TipoToken.OpAritSoma);
        }
        else if (lookahead(1).nome == TipoToken.OpAritSub){
            match(TipoToken.OpAritSub);
        }
        else {
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
        if(lookahead(1).nome == TipoToken.OpAritMult || lookahead(1).nome == TipoToken.OpAritDiv ){
            mulop();
            unaryExp();
            mulExpSubRegra1();
        }
        else {

        }
    }

    //37. mulop → ∗ | / | %
    private void mulop() {
        if(lookahead(1).nome == TipoToken.OpAritMult){
            match(TipoToken.OpAritMult);
        }
        else if(lookahead(1).nome == TipoToken.OpAritDiv){
            match(TipoToken.OpAritDiv);
        }
        else {
            erroSintatico(TipoToken.OpAritDiv.toString(), TipoToken.OpAritMult.toString());
        }

    }

    //38. unaryExp → unaryop unaryExp | factor

    private void unaryExp() {
        if(lookahead(1).nome == TipoToken.OpAritSub || lookahead(1).nome ==TipoToken.OpAritMult || lookahead(1).nome == TipoToken.OpCond){
            unaryop();
            unaryExp();
        }
        else if (lookahead(1).nome == TipoToken.Var || lookahead(1).nome == TipoToken.AbreAsp){
            factor();
        }
        else {
            erroSintatico(TipoToken.OpAritSub.toString(),TipoToken.OpAritMult.toString(),TipoToken.OpCond.toString(), TipoToken.Var.toString(),TipoToken.AbreAsp.toString());
        }
    }

    // 39. unaryop → − | ∗ | ?
    private void unaryop() {
        if(lookahead(1).nome == TipoToken.OpAritSub){
            match(TipoToken.OpAritSub);
        }
        else if(lookahead(1).nome == TipoToken.OpAritMult){
            match(TipoToken.OpAritMult);
        }
        else if (lookahead(1).nome == TipoToken.OpCond){
            match(TipoToken.OpCond);
        }
        else {
            erroSintatico(TipoToken.OpAritSub.toString() , TipoToken.OpAritMult.toString(), TipoToken.OpCond.toString());
        }
    }

    //40. factor → immutable | mutable
    private void factor() {
        if(lookahead(1).nome == TipoToken.AbrePar){
            immutable();
        }
        else if (lookahead(1).nome == TipoToken.Var){
            mutable();
        }
        else {
            erroSintatico(TipoToken.AbrePar.toString(),TipoToken.Var.toString());
        }
    }

    // 42. immutable → ( exp ) | call | constant
    private void immutable() {
        if(lookahead(1).nome == TipoToken.AbrePar){
            match(TipoToken.AbreAsp);
            exp();
            match(TipoToken.FechaPar);
        }
        else if(lookahead(1).nome == TipoToken.Var){
            call();
        }
        else if (lookahead(1).nome == TipoToken.NumInt){
            constant();
        }

        else {
            erroSintatico(TipoToken.AbrePar.toString(), TipoToken.Var.toString());
        }
        
    }

    // 46. constant → NUMINT | NUMREAL | CHARCONST | STRINGCONST
    private void constant() {
        if(lookahead(1).nome == TipoToken.NumInt){
            match(TipoToken.NumInt);
        }
        else if(lookahead(1).nome == TipoToken.NumReal){
            match(TipoToken.NumInt);
        }
        else {
            erroSintatico(TipoToken.NumInt.toString(), TipoToken.NumReal.toString());
        }
    }

    //43. call → ID ( args )
    private void call() {
        match(TipoToken.Var);
        match(TipoToken.AbreAsp);
        args();
        match(TipoToken.FechaPar);

    }

    //44. args → argList | <<vazio>>
    private void args() {
        argList();
    }

    //45.1 argList → exp(argList || <<vazio>>)
    private void argList() {
        exp();
        argListSubRegra1();
    }

    private void argListSubRegra1() {
        if(lookahead(1).nome == TipoToken.Var){
            argList();
        }
        else {

        }
    }


    // 18. compoundStmt → { localDecls stmtList }
    private void compoundStmt() {
        match(TipoToken.AbreCha);
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
        if(lookahead(3).nome == TipoToken.SinPontEVir){
            localDecls();
        }
        else {

        }
    }

    // 20.2 stmtList → stmt ( stmtList | <<vazio>> )

    private void stmtList() {
        stmt();
        stmtListSubRegra1();
    }

    private void stmtListSubRegra1() {
        if(lookahead(1).nome == TipoToken.Var || lookahead(1).nome == TipoToken.AbreCha ){
            stmtList();
        }
        else {

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
        if(lookahead(1).nome == TipoToken.PCElse){
            match(TipoToken.PCElse);
            stmt();
        }
        else {
        }
    }

}

