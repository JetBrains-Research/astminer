grammar C;

@parser::members
{
            public boolean skipToEndOfObject()
            {
                Stack<Object> CurlyStack = new Stack<Object>();
                Object o = new Object();
                int t = _input.LA(1);

                while(t != EOF && !(CurlyStack.empty() && t == CLOSING_CURLY)){

                    if(t == PRE_ELSE){
                        Stack<Object> ifdefStack = new Stack<Object>();
                        consume();
                        t = _input.LA(1);

                        while(t != EOF && !(ifdefStack.empty() && (t == PRE_ENDIF))){
                            if(t == PRE_IF)
                                ifdefStack.push(o);
                            else if(t == PRE_ENDIF)
                                ifdefStack.pop();
                            consume();
                            t = _input.LA(1);
                        }
                    }

                    if(t == OPENING_CURLY)
                        CurlyStack.push(o);
                    else if(t == CLOSING_CURLY)
                        CurlyStack.pop();

                    consume();
                    t = _input.LA(1);
                }
                if(t != EOF)
                    consume();
                return true;
            }

   // this should go into FunctionGrammar but ANTLR fails
   // to join the parser::members-section on inclusion

   public boolean preProcSkipToEnd()
   {
                Stack<Object> CurlyStack = new Stack<Object>();
                Object o = new Object();
                int t = _input.LA(1);

                while(t != EOF && !(CurlyStack.empty() && t == PRE_ENDIF)){

                    if(t == PRE_IF)
                        CurlyStack.push(o);
                    else if(t == PRE_ENDIF)
                        CurlyStack.pop();

                    consume();
                    t = _input.LA(1);
                }
                if(t != EOF)
                    consume();
                return true;
   }

}

code : (function_def | simple_decl | using_directive | water)*;

using_directive: USING NAMESPACE identifier ';';

init_declarator: declarator '(' expr? ')' #initDeclWithCall
               | declarator '=' initializer #initDeclWithAssign
               | declarator #initDeclSimple
               ;

declarator: ptrs? identifier type_suffix?;

type_suffix : ('[' conditional_expression? ']') | param_type_list;

expr: assign_expr (',' expr)?;

assign_expr: conditional_expression (assignment_operator assign_expr)?;
conditional_expression: or_expression #normOr
		      | or_expression ('?' expr ':' conditional_expression) #cndExpr;


or_expression : and_expression ('||' or_expression)?;
and_expression : inclusive_or_expression ('&&' and_expression)?;
inclusive_or_expression: exclusive_or_expression ('|' inclusive_or_expression)?;
exclusive_or_expression: bit_and_expression ('^' exclusive_or_expression)?;
bit_and_expression: equality_expression ('&' bit_and_expression)?;
equality_expression: relational_expression (equality_operator equality_expression)?;
relational_expression: shift_expression (relational_operator relational_expression)?;
shift_expression: additive_expression ( ('<<'|'>>') shift_expression)?;
additive_expression: multiplicative_expression (('+'| '-') additive_expression)?;
multiplicative_expression: cast_expression ( ('*'| '/'| '%') multiplicative_expression)?;

cast_expression: ('(' cast_target ')' cast_expression)
               | unary_expression
;

cast_target: type_name ptr_operator*;

// currently does not implement delete

unary_expression: inc_dec cast_expression
                | unary_op_and_cast_expr
                | sizeof_expression
                | new_expression
                | postfix_expression
                ;

new_expression: '::'? NEW type_name '[' conditional_expression? ']'
              | '::'? NEW type_name '(' expr? ')'
              ;

unary_op_and_cast_expr: unary_operator cast_expression;

sizeof_expression: sizeof '(' sizeof_operand ')'
                 | sizeof sizeof_operand2;

sizeof: 'sizeof';

sizeof_operand: type_name ptr_operator *;
sizeof_operand2: unary_expression;

inc_dec: ('--' | '++');

// this is a bit misleading. We're just allowing access_specifiers
// here because C programs can use 'public', 'protected' or 'private'
// as variable names.

postfix_expression: postfix_expression '[' expr ']' #arrayIndexing
                  | postfix_expression '(' function_argument_list ')' #funcCall
                  | postfix_expression '.' TEMPLATE? (identifier) #memberAccess
                  | postfix_expression '->' TEMPLATE? (identifier) #ptrMemberAccess
                  | postfix_expression inc_dec #incDecOp
                  | primary_expression # primaryOnly
                  ;

function_argument_list: ( function_argument (',' function_argument)* )?;
function_argument: assign_expr;


primary_expression: identifier | constant | '(' expr ')';



statements: (pre_opener
            | pre_closer
            | pre_else {preProcSkipToEnd(); }
            | statement)*;

statement: opening_curly
         | closing_curly
         | block_starter
         | jump_statement
         | label
         | simple_decl
         | expr_statement
         | water
        ;

pre_opener: PRE_IF;
pre_else: PRE_ELSE;
pre_closer: PRE_ENDIF;
opening_curly: '{';
closing_curly: '}';

block_starter: selection_or_iteration;

selection_or_iteration: TRY                      #Try_statement
                      | CATCH '(' param_type ')' #Catch_statement
                      | IF '(' condition ')'     #If_statement
                      | ELSE                     #Else_statement
                      | SWITCH '(' condition ')' #Switch_statement
                      | FOR '(' (for_init_statement | ';') condition? ';'  expr? ')' #For_statement
                      | DO                          #Do_statement
                      | WHILE '(' condition ')'     #While_statement
;

// Don't know why, but: introducing this unused rule results
// in a performance boost.

k/do_statement1: DO statement WHILE '(' expr ')';

for_init_statement : simple_decl
                   | expr ';'
                   ;

jump_statement: BREAK ';'		#breakStatement
              | CONTINUE ';' 		#continueStatement
              | GOTO identifier ';'	#gotoStatement
              | RETURN expr? ';'	#returnStatement
              ;

label: CASE? (identifier | number | CHAR ) ':' ;

expr_statement: expr? ';';

condition: expr
	 | type_name declarator '=' assign_expr;

function_def : template_decl_start? return_type? function_name
            function_param_list ctor_list? compound_statement;

return_type : (function_decl_specifiers* type_name) ptr_operator*;

function_param_list : '(' parameter_decl_clause? ')' CV_QUALIFIER* exception_specification?;

parameter_decl_clause: (parameter_decl (',' parameter_decl)*) (',' '...')?
                     | VOID;
parameter_decl : param_decl_specifiers parameter_id;
parameter_id: ptrs? ('(' parameter_id ')' | parameter_name) type_suffix?;

compound_statement: OPENING_CURLY { skipToEndOfObject(); };

ctor_list: ':'  ctor_initializer (',' ctor_initializer)*;
ctor_initializer:  initializer_id ctor_expr;
initializer_id : '::'? identifier;
ctor_expr:  '(' expr? ')';

function_name: '(' function_name ')' | identifier | OPERATOR operator;

exception_specification : THROW '(' type_id_list ')';
type_id_list: no_brackets* ('(' type_id_list ')' no_brackets*)*;

simple_decl : (TYPEDEF? template_decl_start?) var_decl;

var_decl : class_def init_declarator_list? #declByClass
         | type_name init_declarator_list #declByType
         ;

init_declarator_list: init_declarator (',' init_declarator)* ';';

initializer: assign_expr
           |'{' initializer_list '}'
;

initializer_list: initializer (',' initializer)*;


class_def: CLASS_KEY class_name? base_classes? OPENING_CURLY {skipToEndOfObject(); } ;
class_name: identifier;
base_classes: ':' base_class (',' base_class)*;
base_class: VIRTUAL? access_specifier? identifier;

type_name : (CV_QUALIFIER* (CLASS_KEY | UNSIGNED | SIGNED)?
            base_type ('<' template_param_list '>')? ('::' base_type ('<' template_param_list '>')? )*) CV_QUALIFIER?
          | UNSIGNED
          | SIGNED
          ;


base_type: (ALPHA_NUMERIC | VOID | LONG | LONG)+;

// Parameters

param_decl_specifiers : (AUTO | REGISTER)? type_name;

// this is a bit misleading. We're just allowing access_specifiers
// here because C programs can use 'public', 'protected' or 'private'
// as variable names.

parameter_name: identifier;

param_type_list: '(' VOID ')'
               | '(' (param_type (',' param_type)*)? ')';

param_type: param_decl_specifiers param_type_id;
param_type_id: ptrs? ('(' param_type_id ')' | parameter_name?) type_suffix?;

// operator-identifiers not implemented
identifier : (ALPHA_NUMERIC ('::' ALPHA_NUMERIC)*) | access_specifier;
number: HEX_LITERAL | DECIMAL_LITERAL | OCTAL_LITERAL;

ptrs: (ptr_operator 'restrict'?)+;


unary_operator : '&' | '*' | '+'| '-' | '~' | '!';
relational_operator: ('<'|'>'|'<='|'>=');

constant
    :   HEX_LITERAL
    |   OCTAL_LITERAL
    |   DECIMAL_LITERAL
	|	STRING
    |   CHAR
    |   FLOATING_POINT_LITERAL
    ;

// keywords & operators

function_decl_specifiers: ('inline' | 'virtual' | 'explicit' | 'friend' | 'static');
ptr_operator: ('*' | '&');

access_specifier: ('public' | 'private' | 'protected');

operator: (('new' | 'delete' ) ('[' ']')?)
  | '+' | '-' | '*' | '/' | '%' |'^' | '&' | '|' | '~'
  | '!' | '=' | '<' | '>' | '+=' | '-=' | '*='
  | '/=' | '%=' | '^=' | '&=' | '|=' | '>>'
  |'<<'| '>>=' | '<<=' | '==' | '!='
  | '<=' | '>=' | '&&' | '||' | '++' | '--'
  | ',' | '->*' | '->' | '(' ')' | '[' ']'
  ;

assignment_operator: '=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|=';
equality_operator: ('=='| '!=');

template_decl_start : TEMPLATE '<' template_param_list '>';


// template water
template_param_list : (('<' template_param_list '>') |
                       ('(' template_param_list ')') |
                       no_angle_brackets_or_brackets)+
;

// water

no_brackets: ~('(' | ')');
no_brackets_curlies_or_squares: ~('(' | ')' | '{' | '}' | '[' | ']');
no_brackets_or_semicolon: ~('(' | ')' | ';');
no_angle_brackets_or_brackets : ~('<' | '>' | '(' | ')');
no_curlies: ~('{' | '}');
no_squares: ~('[' | ']');
no_squares_or_semicolon: ~('[' | ']' | ';');
no_comma_or_semicolon: ~(',' | ';');

assign_water: ~('(' | ')' | '{' | '}' | '[' | ']' | ';' | ',');
assign_water_l2: ~('(' | ')' | '{' | '}' | '[' | ']');

water: .;

// Keywords shared among C/C++/Java

IF: 'if'; ELSE: 'else'; FOR: 'for'; WHILE: 'while';

BREAK: 'break'; CASE: 'case'; CONTINUE: 'continue';
SWITCH: 'switch'; DO: 'do';

GOTO: 'goto'; RETURN: 'return';

TYPEDEF: 'typedef';
VOID: 'void'; UNSIGNED: 'unsigned'; SIGNED: 'signed';
LONG: 'long'; CV_QUALIFIER :  'const' | 'volatile';

// Keywords shared among C++/Java

VIRTUAL: 'virtual';
TRY: 'try'; CATCH: 'catch'; THROW: 'throw';
USING: 'using'; NAMESPACE: 'namespace';

// Keywords shared among C/C++

AUTO: 'auto'; REGISTER: 'register';

// C++ keywords

OPERATOR: 'operator';
TEMPLATE: 'template';
NEW: 'new';

CLASS_KEY: ('struct' | 'class' | 'union' | 'enum');

ALPHA_NUMERIC: [a-zA-Z_~][a-zA-Z0-9_]*;

OPENING_CURLY: '{';
CLOSING_CURLY: '}';

// pre-processor directives: C/C++

PRE_IF: ('#if' | '#ifdef' | '#ifndef') ~[\r\n]* '\r'? '\n';
PRE_ELSE: ('#else' | '#elif') ~[\r\n]* '\r'? '\n';
PRE_ENDIF: '#endif' ~[\r\n]* '\r'? '\n';
// PREPROC : '#' ~[\r\n]* '\r'? '\n' -> skip;


HEX_LITERAL : '0' ('x'|'X') HexDigit+ IntegerTypeSuffix? ;
DECIMAL_LITERAL : ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix? ;
OCTAL_LITERAL : '0' ('0'..'7')+ IntegerTypeSuffix? ;

FLOATING_POINT_LITERAL
    :   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
    |   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
    |   ('0'..'9')+ Exponent FloatTypeSuffix?
    |   ('0'..'9')+ Exponent? FloatTypeSuffix
	;

CHAR
    :   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    ;

STRING
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;


fragment
IntegerTypeSuffix
	:	('u'|'U')? ('l'|'L')
	|	('u'|'U')  ('l'|'L')?
	;

fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+;

fragment
FloatTypeSuffix : ('f'|'F'|'d'|'D');


fragment
EscapeSequence
    :   '\\' .
    |   UnicodeEscape
    |   OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

COMMENT
    :   '/*' .*? '*/'    -> skip
    ;
WHITESPACE  :   [ \r\t\u000C\n]+ -> skip
    ;

CPPCOMMENT
    : '//' ~[\r\n]* '\r'? '\n' -> skip
    ;

OTHER : . -> skip ;
