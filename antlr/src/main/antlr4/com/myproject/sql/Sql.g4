grammar Sql;

sql: statement EOF ;

statement
    : createTableStatement
    | alterTableStatement
    | dropTableStatement
    | queryStatement
    ;

createTableStatement
    : CREATE TABLE tableName LEFT_PAREN columnDefinition (COMMA columnDefinition)* RIGHT_PAREN
    ;
tableName: WD;
columnName: WD;
columnDefinition: columnName dataType;
dataType
    : INT
    | BIGINT
    | CHAR LEFT_PAREN DIGIT RIGHT_PAREN
    | VARCHAR LEFT_PAREN DIGIT RIGHT_PAREN
    ;

alterTableStatement
    : ALTER TABLE tableName alterOperator
    ;
alterOperator
    : ADD COLUMN columnDefinition       # addColumn
    | DROP COLUMN columnName            # dropColumn
    ;

dropTableStatement
    : DROP TABLE tableName
    ;

queryStatement
    : SELECT selectColumn FROM tableName filter? order?
    ;
selectColumn
    : ASTERISK                          # selectAll
    | columnName (COMMA columnName)*    # selectCol
    ;
filter: WHERE columnName op = (EQUAL_THAN | LESS_THAN | GREATE_THAN) value;
order: ORDER BY columnName sorting;
sorting: ASC | DESC;
value
    : DIGIT                                # numberValue
    | SINGLE_QUOTE WD SINGLE_QUOTE         # stringValue
    ;


CREATE: ('C' | 'c') ('R' | 'r') ('E' | 'e') ('A' | 'a') ('T' | 't') ('E' | 'e');
ALTER: ('A' | 'a') ('L' | 'l') ('T' | 't') ('E' | 'e') ('R' | 'r');
DROP: ('D' | 'd') ('R' | 'r') ('O'| 'o') ('P' | 'p');
SELECT: ('S' | 's') ('E' | 'e') ('L' | 'l') ('E' | 'e') ('C' | 'c') ('T' | 't');

FROM: ('F' | 'f') ('R' | 'r') ('O' | 'o') ('M' | 'm');
TABLE: ('T' | 't') ('A' | 'a') ('B' | 'b') ('L' | 'l') ('E' | 'e');
COLUMN: ('C' | 'c') ('O'| 'o') ('L' | 'l') ('U' | 'u') ('M' | 'm') ('N' | 'n');
ADD: ('A' | 'a') ('D' | 'd') ('D' | 'd');
WHERE: ('W' | 'w') ('H' | 'h') ('E' | 'e') ('R' | 'r') ('E' | 'e');
ORDER: ('O' | 'o') ('R' | 'r') ('D' | 'd') ('E' | 'e') ('R' | 'r');
BY: ('B' | 'b') ('Y' | 'y');
ASC: ('A' | 'a') ('S' | 's') ('C' | 'c');
DESC: ('D' | 'd') ('E' | 'e') ('S' | 's') ('C' | 'c');

INT: ('I' | 'i') ('N' | 'n') ('T' | 't');
BIGINT: ('B' | 'b') ('I' | 'i') ('G' | 'g') ('I' | 'i') ('N' | 'n') ('T' | 't');
CHAR: ('C' | 'c') ('H' | 'h') ('A' | 'a') ('R' | 'r');
VARCHAR: ('V' | 'v') ('A' | 'a') ('R' | 'r') ('C' | 'c') ('H' | 'h') ('A' | 'a') ('R' | 'r');

LEFT_PAREN: '(';
RIGHT_PAREN: ')';
COMMA: ',';
ASTERISK: '*';
SINGLE_QUOTE: '\'';

EQUAL_THAN: '=';
LESS_THAN: '<';
GREATE_THAN: '>';

DIGIT: [0-9]+;
WD: ([a-zA-Z_] | DIGIT)+;
BLANK: [ \t\r\n]+ -> skip;