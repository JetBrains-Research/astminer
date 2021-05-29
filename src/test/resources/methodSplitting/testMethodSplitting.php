<?php

////////////////// FUNCTIONS //////////////////

// #1 info : {name: fun, args: , enclosing element: null, return type: null}
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

// #6 info : {name: $varFunc, args: $x, enclosing element: variable, return type: null}
$varFunc = function ($x) {
    return $x;
};

$outerVar = 10;

// #7 info : {name: $varFuncWithOuterVar, args: $x, enclosing element: variable, return type: null}
$varFuncWithOuterVar = function ($x) use ($outerVar) {
    return $x * $outerVar;
};

////////////////// ARROW FUNCTIONS //////////////////

// #8 info : {name: $arrow1, args: $x, $y, enclosing element: variable, return type: null}
$arrow1 = fn($x, $y) => $x + $y;

// #9 info : {name: $arrow2, args: $x, enclosing element: variable, return type: null}
// #10 info : {name: null, args: $y, enclosing element: function, return type: null}
$arrow2 = fn($x) => fn($y) => $x * $y;

// #12 info : {name: null, args: $x, enclosing element: null, return type: null}
fn($x = 42) => $x;

// #13 info : {name: null, args: &$x, enclosing element: null, return type: null}
fn(&$x) => $x;

// #14 info : {name: null, args: $x, enclosing element: null, return type: null}
fn&($x) => $x;

// #15 info : {name: null, args: $x, ...$rest, enclosing element: null, return type: null}
fn($x, ...$rest) => $rest;

////////////////// METHOD FUNCTIONS //////////////////

class someClass {
    // #16 info : {name: someFunc, args: , enclosing element: class, return type: null}
    public function someFunc() {
        return 42;
    }

    // #17 info : {name: funcWithParams, args: , enclosing element: class, return type: null}
    public function funcWithParams($a, $b) {

        // #18 info : {name: innerFunction, args: , enclosing element: method, return type: null}
        function innerFunction() {

            // #19 info : {name: superInnerFunction, args: , enclosing element: function, return type: null}
            function superInnerFunction() {
                return 42;
            }
            return 42;
        }
        return 42;
    }
}