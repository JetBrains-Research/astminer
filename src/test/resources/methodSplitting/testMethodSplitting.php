<?php

////////////////// FUNCTIONS //////////////////

// #1 info : {name: fun, args : , enclosing element: null, return type: null}
function fun() {
    return 5;
}

// #2 info : {name: fun2, args: $a, $b, enclosing element: null, return type: null}
function fun2($a, $b) {
    return $a + $b;
}

// #3 info : {name: funWithTypedParameter, args: int $a, enclosing element: null, return type: null}
function funWithTypedParameter(int $a) {
    return $a;
}

// #4 info : {name: funWithReturnType, args: $a, $b, enclosing element: null, return type: string}
function funWithReturnType($a, $b) : string {
    return 'hello';
}

// #5 info : {name: funWithDottedArg, args: $a, ...$rest, enclosing element: null, return type: null}
function funWithDottedArg($a, ...$rest) {
    return 'hello';
}

////////////////// VAR FUNCTIONS //////////////////

// #6 info : {name: varFunc, args: $x, enclosing element: variable, return type: null}
$varFunc = function ($x) {
    return $x;
};

$outerVar = 10;

// #7 info : {name: varFuncWithOuterVar, args: $x, enclosing element: variable, return type: null}
$varFuncWithOuterVar = function ($x) use ($outerVar) {
    return $x * $outerVar;
};

/>