////////////////// FUNCTIONS //////////////////

// #1 info : {name : fun, args : , enclosing element : null, enclosing element name : null}
function fun() {
    return 0;
};

// #2 info : {name : funWithArg, args : a, enclosing element : null, enclosing element name : null}
function funWithArg(a) {
    return a;
};

// #3 info : {name : funWithArgs, args : a, b, c, enclosing element : null, enclosing element name : null}
function funWithArgs(a, b, c) {
    return a;
};

// #4 info : {name : newFun, args : , enclosing element : null, enclosing element name : null}
new function newFun() {
    return 0;
};

// #5 info : {name : newFunWithArg, args : a, enclosing element : null, enclosing element name : null}
new function newFunWithArg(a = 0) {
    return a;
};

// #6 info : {name : newFunWithArgs, args : a, b, c, enclosing element : null, enclosing element name : null}
new function newFunWithArgs(a, b, c) {
    return 0;
};


////////////////// VAR FUNCTIONS //////////////////

// #7 info : {name : varFun, args : , enclosing element : var, enclosing element name : v1}
var v1 = function varFun() {
    return 0;
};

// #8 info : {name : varFunWithArg, args : a, enclosing element : var, enclosing element name : v2}
const v2 = function varFunWithArg(a) {
    return 0;
};

// #9 info : {name : varFunWithArgs, args : a, b, c, enclosing element : var, enclosing element name : v3}
// #10 info : {name : null, args : a, enclosing element : var, enclosing element name : v3}
var v3 = function varFunWithArgs(a = 0, b = 0, c = (a) => a) {
    return a;
};

var v4 =  {
    // #11 info : {name : varFun, args : , enclosing element : var, enclosing element name : v4}
    'inVarFun' : function varFun() {
        return 0;
    },
    // #12 info : {name : varFunWithArg, args : a, enclosing element : var, enclosing element name : v4}
    'inVarFunWithArg' : function varFunWithArg(a) {
        return 0;
    },
    // #13 info : {name : varFunWithArgs, args : a, b, c, enclosing element : var, enclosing element name : v4}
    'inVarFunWithArgs' : function varFunWithArgs(a = 0, b, c) {
        return a;
    },
    // #14 info : {name : null, args : , enclosing element : var, enclosing element name : v4}
    'anonym' : function() {
        return 0;
    },
    // #15 info : {name : null, args : a, enclosing element : var, enclosing element name : v4}
    'anonymWithArg' : function(a) {
        return 0;
    },
    // #16 info : {name : null, args : a, b, c, enclosing element : var, enclosing element name : v4}
    'anonymWithArgs' : function(a = 0, b = 0, c) {
        return c;
    }
};

// #17 info : {name : null, args : a, enclosing element : var, enclosing element name : v5}
var v5 = function(a) {
    return {
        // #18 info : {name : null, args : a, enclosing element : var, enclosing element name : v5}
        v6: function (a) {
            return a;
        },

        // #19 info : {name : null, args : , enclosing element : var, enclosing element name : v5}
        v7: function () {
            return 0;
        }
    };
};

//////////////// NESTED FUNCTIONS //////////////////

// #20 info : {name : nestedFun1, args : , enclosing element : null, enclosing element name : null}
function nestedFun1() {

    // #21 info : {name : funFun, args : , enclosing element : fun, enclosing element name : nestedFun1}
    function funFun() {

        // #22 info : {name : funFunWithArg, args : a, enclosing element : fun, enclosing element name : funFun}
        function funFunWithArg(a) {

            // #23 info : {name : funFunWithArgs, args : a, b, c, enclosing element : fun, enclosing element name : funFunWithArg}
            function funFunWithArgs(a = 0, b = 0, c = 0) {
                return 0;
            };
        };
    };
};

// #24 info : {name : nestedFun2, args : , enclosing element : null, enclosing element name : null}
function nestedFun2() {

    // #25 info : {name : funFun, args : , enclosing element : fun, enclosing element name : nestedFun2}
    return function funFun() {
        return 0;
    };
};

//////////////// ARROW FUNCTIONS ////////////////

// #26 info : {name : null, args : a, enclosing element : var, enclosing element name : a1}
var a1 = a => 0;

// #27 info : {name : null, args : a, enclosing element : var, enclosing element name : a2}
var a2 = (a) => 0;

// #28 info : {name : null, args : a, b, c, enclosing element : var, enclosing element name : a3}
var a3 = (a = 0, b, c) => a * b * c;

// #29 info : {name : null, args : a, enclosing element : null, enclosing element name : null}
funWithArg(a => a);

// #30 info : {name : null, args : a, enclosing element : null, enclosing element name : null}
funWithArg((a = 0) => a);

// #31 info : {name : null, args : a, b, c, enclosing element : null, enclosing element name : null}
funWithArg((a, b, c) => 0);


//////////////// METHODS ////////////////

class Class {

    // #32 info : {name : classConstructorWithArgs, args : a, b, c, enclosing element : class, enclosing element name : Class}
    classConstructorWithArgs(a, b, c) {

        // #33 info : {name : methodFunctionWithArg, args : a, enclosing element : method, enclosing element name : classConstructorWithArgs}
        this.a = function methodFunctionWithArg(a) {
            return 0;
        };

        // #34 info : {name : null, args : , enclosing element : method, enclosing element name : classConstructorWithArgs}
        this.b = function() {
            return 0;
        };
    };

    // #35 info : {name : classGetter, args : , enclosing element : class, enclosing element name : Class}
    get classGetter() {
        return this.a;
    };

    // #36 info : {name : classSetterWithArg, args : a, enclosing element : class, enclosing element name : Class}
    set classSetterWithArg(a) {
        this.a = a;
    };

    // #37 info : {name : classFun, args : , enclosing element : class, enclosing element name : Class}
    classFun() {
        return 0;
    };

    // #38 info : {name : classFunWithArg, args : a, enclosing element : class, enclosing element name : Class}
    classFunWithArg(a = 0) {
        return a;
    };

    // #39 info : {name : classFunWithArgs, args : a, b, c, enclosing element : class, enclosing element name : Class}
    classFunWithArgs(a, b, c) {
        return 0;
    };

    // #40 info : {name : classStaticFun, args : , enclosing element : class, enclosing element name : Class}
    static classStaticFun() {
        return 0;
    };

    // #41 info : {name : classStaticFunWithArg, args : a, enclosing element : class, enclosing element name : Class}
    static classStaticFunWithArg(a = 0) {
        return a;
    };

    // #42 info : {name : classStaticFunWithArgs, args : a, b, c, enclosing element : class, enclosing element name : Class}
    // #43 info : {name : null, args : a, enclosing element : method, enclosing element name : classStaticFunWithArgs}
    static classStaticFunWithArgs(a, b, c = a => a) {
        return 0;
    };

    // #44 info : {name : classNestedFun, args : , enclosing element : class, enclosing element name : Class}
    classNestedFun() {

        // #45 info : {name : methodFun, args : a, enclosing element : method, enclosing element name : classNestedFun}
        // #46 info : {name : null, args : a, enclosing element : fun, enclosing element name : methodFun}
        function methodFun(a = a => a) {

            // #47 info : {name : null, args : a, enclosing element : fun, enclosing element name : methodFun}
            funWithArg(a => a);

            return 0;
        }
    }
}