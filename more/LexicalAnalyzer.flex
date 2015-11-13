import java.util.regex.PatternSyntaxException;
import java.util.HashMap;

%%// Options of the scanner

%class LexicalAnalyzer	//Name
%unicode						//Use unicode
%line							//Use line counter (yyline variable)
%column						//Use character counter by line (yycolumn variable)
%function nextToken
%type Symbol
%yylexthrow PatternSyntaxException
%yylexthrow SyntaxErrorException


%{
    private static HashMap<Object, Integer> varNameMap = new HashMap<Object, Integer>();
    public static HashMap<Object, Integer> getVarNameMap() {
        return varNameMap;
    }
%}

%eofval{
	return new Symbol(LexicalUnit.END_OF_STREAM,yyline, yycolumn);
%eofval}

//Extended Regular Expressions

EndOfLine   = \r|\n|\r\n
Tab = \t
Space = " "

Comment             = co.*co

LeftParenthesis     = \(
RightParenthesis    = \)

Beg                 = begin
End                 = end

Semicolon           = ;
Assign              = :=

Minus               = -
Plus                = \+
Times               = \*
Divide              = \/

If                  = if
Then                = then
Fi                  = fi
Else                = else
Not                 = not

Or                  = or
And                 = and

Equal               = =
GreaterEqual        = >=
Greater             = >
SmallerEqual        = <=
Smaller             = <
Different           = \/=

While               = while
Do                  = do
Od                  = od
For                 = for
From                = from
By                  = by
To                  = to


Print               = print
Read                = read

AlphaUpperCase      = [A-Z]
AlphaLowerCase      = [a-z]
Alpha               = {AlphaUpperCase}|{AlphaLowerCase}
Numeric             = [0-9]
AlphaNumeric        = {Alpha}|{Numeric}
VarName             = {Alpha}{AlphaNumeric}*
Number              = {Numeric}*
BadVarName          = {Numeric}{AlphaNumeric}*

//Declare exclusive states

%%//Identification of tokens

{Comment} {}

{LeftParenthesis} { return new Symbol(LexicalUnit.LEFT_PARENTHESIS, yyline, yycolumn, yytext()); }
{RightParenthesis} { return new Symbol(LexicalUnit.RIGHT_PARENTHESIS, yyline, yycolumn, yytext()); }

{Beg} { return new Symbol(LexicalUnit.BEG, yyline, yycolumn, yytext()); }
{End} { return new Symbol(LexicalUnit.END, yyline, yycolumn, yytext()); }

{Semicolon} { return new Symbol(LexicalUnit.SEMICOLON, yyline, yycolumn, yytext()); }
{Assign} { return new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn, yytext()); }

{Minus} { return new Symbol(LexicalUnit.MINUS, yyline, yycolumn, yytext()); }
{Plus} { return new Symbol(LexicalUnit.PLUS, yyline, yycolumn, yytext()); }
{Times} { return new Symbol(LexicalUnit.TIMES, yyline, yycolumn, yytext()); }
{Divide} { return new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn, yytext()); }

{If} {return new Symbol(LexicalUnit.IF, yyline, yycolumn, yytext());}
{Then} {return new Symbol(LexicalUnit.THEN, yyline, yycolumn, yytext());}
{Fi} {return new Symbol(LexicalUnit.FI, yyline, yycolumn, yytext());}
{Else} {return new Symbol(LexicalUnit.ELSE, yyline, yycolumn, yytext());}
{Not} {return new Symbol(LexicalUnit.NOT, yyline, yycolumn, yytext());}

{Or} { return new Symbol(LexicalUnit.OR, yyline, yycolumn, yytext()); }
{And} { return new Symbol(LexicalUnit.AND, yyline, yycolumn, yytext()); }

{Equal} { return new Symbol(LexicalUnit.EQUAL, yyline, yycolumn, yytext()); }
{GreaterEqual} { return new Symbol(LexicalUnit.GREATER_EQUAL, yyline, yycolumn, yytext()); }
{Greater} { return new Symbol(LexicalUnit.GREATER, yyline, yycolumn, yytext()); }
{SmallerEqual} { return new Symbol(LexicalUnit.SMALLER_EQUAL, yyline, yycolumn, yytext()); }
{Smaller} { return new Symbol(LexicalUnit.SMALLER, yyline, yycolumn, yytext()); }
{Different} { return new Symbol(LexicalUnit.DIFFERENT, yyline, yycolumn, yytext()); }

{While} {return new Symbol(LexicalUnit.WHILE, yyline, yycolumn, yytext());}
{Do} {return new Symbol(LexicalUnit.DO, yyline, yycolumn, yytext());}
{Od} {return new Symbol(LexicalUnit.OD, yyline, yycolumn, yytext());}
{For} {return new Symbol(LexicalUnit.FOR, yyline, yycolumn, yytext());}
{From} {return new Symbol(LexicalUnit.FROM, yyline, yycolumn, yytext());}
{By} {return new Symbol(LexicalUnit.BY, yyline, yycolumn, yytext());}
{To} {return new Symbol(LexicalUnit.TO, yyline, yycolumn, yytext());}

{Print} { return new Symbol(LexicalUnit.PRINT, yyline, yycolumn, yytext()); }
{Read} { return new Symbol(LexicalUnit.READ, yyline, yycolumn, yytext()); }

{Number} { return new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, yytext()); }
{BadVarName} { throw new SyntaxErrorException(yytext()+"\n Error: Variable names must start with a letter."); }
{VarName} { Symbol varName = new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext()); 
            if(!varNameMap.containsKey(varName.getValue())) {
                varNameMap.put(varName.getValue(), varName.getLine());
            }
            return varName;
}


{Space} {}
{EndOfLine} {}
{Tab} {}

[^] {throw new SyntaxErrorException(yytext());}
