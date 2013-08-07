InfixPP
=======

Abstract
---------------
An abstract syntax tree printer for binary expressions.

Run
---------------
`java -jar infixpp.jar <file>`


Examples
---------------
### Arithmetic Expressions

Arithmetic expressions with five binary operators.
The only operator of power (exponent) has right associativity.
The operators of addition and subtraction has same precedence.
Also multiplication and division has same precedence.
Addition and subtraction are weaker than multiplication and division.
Power has the strongest precedence.

Script:

    Define '+' := "add".
    Define '-' := "sub".
    Define '*' := "mul".
    Define '/' := "div".
    Define '^' := "pow" right.
    
    Order ('+', '-') < ('*', '/') < '^'.
    
    Translate "1" + "2" * "3" - "4" End
    Translate "1" + "2" * ("3" - "4") End
    Translate "x" + "y" + "z" End
    Translate "a" ^ "2" ^ "3" End

Output:

    sub(add(1, mul(2, 3)), 4)
    add(1, mul(2, sub(3, 4)))
    add(add(x, y), z)
    pow(a, pow(2, 3))

### Propositional Formulae

Propositional formulae with four binary operators.
The only operator of implication has right associativity.

Script:

    Define '<=>' := "eqv".
    Define '=>'  := "imp" right.
    Define '\/'  := "or".
    Define '/\'  := "and".
    
    Order '<=>' < '=>' < '\/' < '/\'.
    
    Translate
            ("p" => "q" => "p")
        <=> ("a" \/ ("a" => "false"))
        <=> (("a" /\ ("a" => "false")) => "false")
    End

Output:

    eqv(eqv(imp(p, imp(q, p)), or(a, imp(a, false))), imp(and(a, imp(a, false)), false))
