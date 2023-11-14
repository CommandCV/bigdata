grammar Sql;

sql: statement EOF ;

statement
    : createDatabaseStatement
    | dropDatabaseStatement
    | createTableStatement
    | alterTableStatement
    | dropTableStatement
    | queryStatement
    | insertStatement
    | deleteStatement
    ;

createDatabaseStatement
    : CREATE DATABASE databaseName
    ;

dropDatabaseStatement
    : DROP DATABASE databaseName
    ;

createTableStatement
    : CREATE TABLE databaseName POINT tableName LEFT_PAREN columnDefinitions RIGHT_PAREN
    ;
databaseName: WD;
tableName: WD;
columnDefinitions: columnDefinition (COMMA columnDefinition)*;
columnDefinition: columnName dataType;
columnName: WD;
dataType
    : INT                                       # intType
    | BIGINT                                    # bigintType
    | CHAR LEFT_PAREN DIGIT RIGHT_PAREN         # charType
    | VARCHAR LEFT_PAREN DIGIT RIGHT_PAREN      # varcharType
    ;

alterTableStatement
    : ALTER TABLE databaseName POINT tableName alterOperator
    ;
alterOperator
    : ADD COLUMN columnDefinition       # addColumn
    | DROP COLUMN columnName            # dropColumn
    ;

dropTableStatement
    : DROP TABLE databaseName POINT tableName
    ;

queryStatement
    : SELECT selectColumn FROM databaseName POINT tableName filter? order?
    ;
selectColumn
    : ASTERISK                          # selectAll
    | columnName (COMMA columnName)*    # selectCol
    ;
filter: WHERE filterCondition (AND filterCondition)*;
filterCondition: columnName op = (EQUAL_THAN | LESS_THAN | GREATER_THAN) value;
order: ORDER BY columnName sorting;
sorting: ASC | DESC;
value
    : DIGIT                                # numberValue
    | SINGLE_QUOTE WD SINGLE_QUOTE         # stringValue
    ;

insertStatement
    : INSERT INTO databaseName POINT tableName VALUES rows
    ;
rows: row (COMMA row)*;
row: LEFT_PAREN value (COMMA value)* RIGHT_PAREN;

deleteStatement
    : DELETE FROM databaseName POINT tableName match?
    ;
match: WHERE filterCondition (AND filterCondition)*;

CREATE: ('C' | 'c') ('R' | 'r') ('E' | 'e') ('A' | 'a') ('T' | 't') ('E' | 'e');
ALTER: ('A' | 'a') ('L' | 'l') ('T' | 't') ('E' | 'e') ('R' | 'r');
DROP: ('D' | 'd') ('R' | 'r') ('O'| 'o') ('P' | 'p');
SELECT: ('S' | 's') ('E' | 'e') ('L' | 'l') ('E' | 'e') ('C' | 'c') ('T' | 't');
INSERT: ('I' | 'i') ('N' | 'n') ('S' | 's') ('E' | 'e') ('R' | 'r') ('T' | 't');
DELETE: ('D' | 'd') ('E' | 'e') ('L' | 'l') ('E' | 'e') ('T' | 't') ('E' | 'e');

INTO: ('I' | 'i') ('N' | 'n') ('T' | 't') ('O'| 'o');
FROM: ('F' | 'f') ('R' | 'r') ('O' | 'o') ('M' | 'm');
DATABASE: ('D' | 'd') ('A' | 'a') ('T' | 't') ('A' | 'a') ('B' | 'b') ('A' | 'a') ('S' | 's') ('E' | 'e');
TABLE: ('T' | 't') ('A' | 'a') ('B' | 'b') ('L' | 'l') ('E' | 'e');
COLUMN: ('C' | 'c') ('O'| 'o') ('L' | 'l') ('U' | 'u') ('M' | 'm') ('N' | 'n');
ADD: ('A' | 'a') ('D' | 'd') ('D' | 'd');
VALUES: ('V' | 'v') ('A' | 'a') ('L' | 'l') ('U' | 'u') ('E' | 'e') ('S' | 's');
WHERE: ('W' | 'w') ('H' | 'h') ('E' | 'e') ('R' | 'r') ('E' | 'e');
AND: ('A' | 'a') ('N' | 'n') ('D' | 'd');
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
POINT: '.';
COMMA: ',';
ASTERISK: '*';
SINGLE_QUOTE: '\'';

EQUAL_THAN: '=';
LESS_THAN: '<';
GREATER_THAN: '>';

DIGIT: [0-9]+;
WD: ([a-zA-Z_] | DIGIT)+;
BLANK: [ \t\r\n]+ -> skip;