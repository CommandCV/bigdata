grammar Calculate;

calculate : expr EOF ;

// Support the '*' '/' '+' '-' operators.
// The operators '*' and '/' is prior to '+' and '-'.
expr
    : expr op = (TIMES | DIVIDE) expr # TimesDiv
    | expr op = (PLUS | MINUS) expr # PlusMinus
    | NUMBER # Number
    ;

// define number
NUMBER : [0-9]+ ;

// define operators
TIMES : '*' ;
DIVIDE : '/' ;
PLUS : '+' ;
MINUS : '-' ;

// match one or more ' ' '\t' '\r' '\n' characters
SPACE : [ \t\r\n]+ -> skip ;