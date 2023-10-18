// grammar name
grammar Hello;

// define the expression and rule
expression: 'hello' WD ;

WD : [a-zA-Z0-9_]+ ;
BLANK : [\t\r\n]+ -> skip ;