import java.util.ArrayList;

public class Analex {

    LerCaractere ldat;
    int count = 0;
    ArrayList<Token> Gravar_Token_Lexema = new ArrayList<Token>();


    public Analex(String arquivo) {
        ldat = new LerCaractere(arquivo);
    }

    public void Gravar_Token_Lexema(Token token) {
        Gravar_Token_Lexema.add(token);
    }

    public void Listar_Token_Lexema() {
        for (Token token : Gravar_Token_Lexema) {
            System.out.println(token);
        }
    }

    public Token proximoToken() {
        Token proximo = null;
        espacos();
        ldat.confirmar();
        proximo = fim();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        proximo = palavrasChave();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        proximo = variavel();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        proximo = numeros();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }

        proximo = abrircomentrarios();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }

        proximo = fecharcomentrarios();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }


        proximo = formEscritaOuOPModulo();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        proximo = opComercialOuOpBoolE();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        proximo = opBoolOu();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }

        proximo = operadorAtribuicaoEIgualdade();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        proximo = operadorDiferente();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        proximo = operadorAritmetico();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        proximo = operadorRelacional();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        proximo = sinais();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        proximo = parenteses();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        proximo = chaves();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        proximo = Colhectes();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        proximo = aspas();
        if (proximo == null) {
            ldat.zerar();
        } else {
            ldat.confirmar();
            return proximo;
        }
        System.err.println("Erro léxico!");
        System.err.println(ldat.toString());
        return null;
    }

    private Token operadorAritmetico() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == '*') {
            c = (char) ldat.lerProximoCaractere();
            if (c == '=') {
                return new Token(TipoToken.OpMultCom, ldat.getLexema());
            }
            return new Token(TipoToken.OpAritMult, ldat.getLexema());
        } else if (c == '/') {
            c = (char) ldat.lerProximoCaractere();
            if (c == '=') {
                return new Token(TipoToken.OpDivCom, ldat.getLexema());
            } else if (c == '/') {
                while (((char) ldat.lerProximoCaractere()) != '\n') {
                    ldat.lerProximoCaractere();
                }
                ldat.retroceder();
                return new Token(TipoToken.Comentario, ldat.getLexema());
            }
            return new Token(TipoToken.OpAritDiv, ldat.getLexema());
        } else if (c == '+') {
            c = (char) ldat.lerProximoCaractere();
            if (c == '+') {
                return new Token(TipoToken.OpInc, ldat.getLexema());
            } else if (c == '=') {
                return new Token(TipoToken.OpSomaCom, ldat.getLexema());
            }

            return new Token(TipoToken.OpAritSoma, ldat.getLexema());
        } else if (c == '-') {
            c = (char) ldat.lerProximoCaractere();
            if (c == '-') {
                return new Token(TipoToken.OpDec, ldat.getLexema());
            } else if (c == '=') {
                return new Token(TipoToken.OpSubCom, ldat.getLexema());
            }
            return new Token(TipoToken.OpAritSub, ldat.getLexema());
        } else if (c == '?') {
            return new Token(TipoToken.OpCond, ldat.getLexema());
        } else {
            return null;
        }
    }

    private Token operadorAtribuicaoEIgualdade() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == '=') {
            c = (char) ldat.lerProximoCaractere();
            if (c == '=') {
                return new Token(TipoToken.OpRelIgual, ldat.getLexema());
            } else {
                ldat.retroceder();
                return new Token(TipoToken.OPAtribuir, ldat.getLexema());
            }
        }
        return null;
    }

    private Token abrircomentrarios() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == '/') {
            c = (char) ldat.lerProximoCaractere();
            if (c == '*') {
                return new Token(TipoToken.AbriCom, ldat.getLexema());
            }
        }
        return null;
    }

    private Token fecharcomentrarios() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == '*') {
            c = (char) ldat.lerProximoCaractere();
            if (c == '/') {
                return new Token(TipoToken.FecharCom, ldat.getLexema());
            }
        }
        return null;
    }


    private Token formEscritaOuOPModulo() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == '%') {
            c = (char) ldat.lerProximoCaractere();
            if (c == 'd') {
                return new Token(TipoToken.FormatInt, ldat.getLexema());
            } else if (c == 'f') {
                return new Token(TipoToken.FormatFloat, ldat.getLexema());
            } else if (c == 'c') {
                return new Token(TipoToken.FormatChar, ldat.getLexema());
            } else if (c == '=') {
                return new Token(TipoToken.OpModCom, ldat.getLexema());
            } else {
                ldat.retroceder();
                return new Token(TipoToken.OPModulo, ldat.getLexema());
            }
        }
        return null;
    }


    private Token operadorDiferente() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == '!') {
            c = (char) ldat.lerProximoCaractere();
            if (c == '=') {
                return new Token(TipoToken.OpRelDif, ldat.getLexema());
            } else {
                return new Token(TipoToken.OpDif, ldat.getLexema());
            }
        }
        return null;
    }

    private Token sinais() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == ';') {
            return new Token(TipoToken.SinPontEVir, ldat.getLexema());
        } else if (c == ',') {
            return new Token(TipoToken.SinVir, ldat.getLexema());
        } else if (c == ':') {
            return new Token(TipoToken.SinDoisPontos, ldat.getLexema());
        } else {
            return null;
        }
    }

    private Token opComercialOuOpBoolE() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == '&') {
            c = (char) ldat.lerProximoCaractere();
            if (c == '&') {
                return new Token(TipoToken.OpBoolE, ldat.getLexema());
            } else {
                ldat.retroceder();
                return new Token(TipoToken.SinComer, ldat.getLexema());
            }
        }
        return null;
    }

    private Token opBoolOu() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == '|') {
            c = (char) ldat.lerProximoCaractere();
            if (c == '|') {
                return new Token(TipoToken.OpBoolOu, ldat.getLexema());
            }
        }
        return null;
    }


    private Token parenteses() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == '(') {
            return new Token(TipoToken.AbrePar, ldat.getLexema());
        } else if (c == ')') {
            return new Token(TipoToken.FechaPar, ldat.getLexema());
        } else {
            return null;
        }
    }

    private Token chaves() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == '{') {
            return new Token(TipoToken.AbreCha, ldat.getLexema());
        } else if (c == '}') {
            return new Token(TipoToken.FechaCha, ldat.getLexema());
        } else {
            return null;
        }
    }

    private Token Colhectes() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == '[') {
            return new Token(TipoToken.AbreCol, ldat.getLexema());
        } else if (c == ']') {
            return new Token(TipoToken.FechaCol, ldat.getLexema());
        } else {
            return null;
        }
    }


    private Token aspas() {
        int estado = 1;
        while (true) {
            char c = (char) ldat.lerProximoCaractere();
            if (estado == 1) {
                if (c == '"') {
                    estado = 2;
                } else {
                    return null;
                }
            } else if (estado == 2) {
                if (c == '\n') {
                    return null;
                }
                if (c == '"') {
                    return new Token(TipoToken.String, ldat.getLexema());
                } else if (c == '\\') {
                    estado = 3;
                }
            } else if (estado == 3) {
                if (c == '\n') {
                    return null;
                } else {
                    estado = 2;
                }
            }
        }
    }


    private Token operadorRelacional() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == '<') {
            c = (char) ldat.lerProximoCaractere();
            if (c == '=') {
                return new Token(TipoToken.OpRelMenorIgual, ldat.getLexema());
            } else {
                ldat.retroceder();
                return new Token(TipoToken.OpRelMenor, ldat.getLexema());
            }
        } else if (c == '>') {
            c = (char) ldat.lerProximoCaractere();
            if (c == '=') {
                return new Token(TipoToken.OpRelMaiorIgual, ldat.getLexema());
            } else {
                ldat.retroceder();
                return new Token(TipoToken.OpRelMaior, ldat.getLexema());
            }
        }
        return null;
    }

    private Token numeros() {
        int estado = 1;
        while (true) {
            char c = (char) ldat.lerProximoCaractere();
            if (estado == 1) {
                if (Character.isDigit(c)) {
                    estado = 2;
                } else {
                    return null;
                }
            } else if (estado == 2) {
                if (c == '.') {
                    c = (char) ldat.lerProximoCaractere();
                    if (Character.isDigit(c)) {
                        estado = 3;
                    } else {
                        return null;
                    }
                } else if (!Character.isDigit(c)) {
                    ldat.retroceder();
                    return new Token(TipoToken.NumInt, ldat.getLexema());
                }
            } else if (estado == 3) {
                if (!Character.isDigit(c)) {
                    ldat.retroceder();
                    return new Token(TipoToken.NumReal, ldat.getLexema());
                }
            }
        }
    }

    private Token variavel() {
        int estado = 1;
        while (true) {
            char c = (char) ldat.lerProximoCaractere();
            if (estado == 1) {
                if (Character.isJavaLetter(c)) {
                    estado = 2;
                } else {
                    return null;
                }
            } else if (estado == 2) {
                if (!Character.isLetterOrDigit(c)) {
                    ldat.retroceder();
                    return new Token(TipoToken.Var, ldat.getLexema());
                }
            }
        }
    }

    private Token Comentario() {
        int caractereLido = ldat.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == '/') {

        }
        return null;
    }


    private void espacos() {

        int estado = 1;
        while (true) {
            char c = (char) ldat.lerProximoCaractere();
            if (estado == 1) {
                if (Character.isWhitespace(c) || c == ' ') {
                    estado = 2;
                } else {
                    ldat.retroceder();
                    return;
                }
            } else if (estado == 2) {
                if (!(Character.isWhitespace(c) || c == ' ')) {
                    ldat.retroceder();
                    return;
                }
            } else if (estado == 3) {
                if (c == '\n') {
                    count++;
                    return;
                }
            }
        }
    }

    private Token palavrasChave() {
        while (true) {
            char c = (char) ldat.lerProximoCaractere();
            if (!Character.isLetter(c)) {
                ldat.retroceder();
                String lexema = ldat.getLexema();
                if (lexema.equals("int")) {
                    return new Token(TipoToken.PCInt, lexema);
                } else if (lexema.equals("float")) {
                    return new Token(TipoToken.PCFloat, lexema);
                } else if (lexema.equals("long")) {
                    return new Token(TipoToken.PCLong, lexema);
                } else if (lexema.equals("char")) {
                    return new Token(TipoToken.PCChar, lexema);
                } else if (lexema.equals("unsigned")) {
                    return new Token(TipoToken.PCUnsigned, lexema);
                } else if (lexema.equals("short")) {
                    return new Token(TipoToken.PCShort, lexema);
                } else if (lexema.equals("const")) {
                    return new Token(TipoToken.PCConst, lexema);
                } else if (lexema.equals("printf")) {
                    return new Token(TipoToken.PCPrinf, lexema);
                } else if (lexema.equals("scanf")) {
                    return new Token(TipoToken.PCScannf, lexema);
                } else if (lexema.equals("if")) {
                    return new Token(TipoToken.PCIf, lexema);
                } else if (lexema.equals("else")) {
                    return new Token(TipoToken.PCElse, lexema);
                } else if (lexema.equals("while")) {
                    return new Token(TipoToken.PCWhile, lexema);
                } else if (lexema.equals("for")) {
                    return new Token(TipoToken.PCFor, lexema);
                } else if (lexema.equals("do")) {
                    return new Token(TipoToken.PCDo, lexema);
                } else if (lexema.equals("to")) {
                    return new Token(TipoToken.PCTO, lexema);
                } else if (lexema.equals("struct")) {
                    return new Token(TipoToken.PCStruct, lexema);
                } else if (lexema.equals("static")) {
                    return new Token(TipoToken.PCStatic, lexema);
                } else if (lexema.equals("then")) {
                    return new Token(TipoToken.PCThen, lexema);
                } else if (lexema.equals("switch")) {
                    return new Token(TipoToken.PCSwitch, lexema);
                } else if (lexema.equals("case")) {
                    return new Token(TipoToken.PCCase, lexema);
                } else if (lexema.equals("return")) {
                    return new Token(TipoToken.PcReturn, lexema);
                } else if (lexema.equals("void")) {
                    return new Token(TipoToken.PCVoid, lexema);
                } else if (lexema.equals("break")) {
                    return new Token(TipoToken.PCbreak, lexema);
                } else if (lexema.equals("default")) {
                    return new Token(TipoToken.PCDefault, lexema);
                } else if (lexema.equals("enum")) {
                    return new Token(TipoToken.PCEnum, lexema);
                } else if (lexema.equals("main")) {
                    return new Token(TipoToken.PCMain, lexema);
                } else if (lexema.equals("sizeof")) {
                    return new Token(TipoToken.PCSizeof, lexema);
                } else {
                    return null;
                }
            }
        }
    }

    private Token fim() {
        int caractereLido = ldat.lerProximoCaractere();
        if (caractereLido == -1) {
            return new Token(TipoToken.Fim, "Fim");
        }
        return null;
    }


}
