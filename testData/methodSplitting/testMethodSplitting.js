////////////////// FUNCTIONS //////////////////

// #1
function fun() {
    return 0;
};

// #2
function funWithArg(a) {
    return a;
};

// #3
function funWithArgs(a, b, c) {
    return a;
};

// #4
new function exprFun() {
    return 0;
};

// #5
new function exprFunWithArg(a = 0) {
    return a;
};

// #6
new function exprFunWithArgs(a, b, c) {
    return 0;
};


////////////////// VAR FUNCTIONS //////////////////

// #7
var v1 = function varFun() {
    return 0;
};

// #8
const v2 = function varFunWithArg(a) {
    return 0;
};

// #9
var v3 = function varFunWithArgs(a = 0, b = 0, c = 0) {
    return a;
};

var v4 =  {
    // #10
    'inVarFun' : function varFun() {
        return 0;
    },
    // #11
    'inVarFunWithArg' : function varFunWithArg(a) {
        return 0;
    },
    // #12
    'inVarFunWithArgs' : function varFunWithArgs(a = 0, b, c) {
        return a;
    },
    // #13
    'anonym' : function() {
        return 0;
    },
    // #14
    'anonymWithArg' : function(a) {
        return 0;
    },
    // #15
    'anonymWithArgs' : function(a = 0, b = 0, c) {
        return c;
    }
};

// #16
var v5 = function(a) {
    return {
        // #17
        v6: function (a) {
            return a;
        },

        // #18
        v7: function () {
            return 0;
        }
    };
};

//////////////// NESTED FUNCTIONS //////////////////

// #19
function nestedFun1() {
    // #20
    function funFun() {
        // #21
        function funFunWithArg(a) {
            // #22
            function funFunWithArgs(a = 0, b = 0, c = 0) {
                return 0;
            };
        };
    };
};

// #23
function nestedFun2() {
    // #24
    return function returnFun() {
        return 0;
    };
};

//////////////// ARROW FUNCTIONS ////////////////

// #25
var a1 = a => 0;

// #26
var a2 = (a) => 0;

// #27
var a3 = (a = 0, b, c) => a * b * c;

// #28
funWithArg(a => a);

// #29
funWithArg((a = 0) => a);

// #30
funWithArg((a, b, c) => 0);

//////////////// METHODS ////////////////

class Class {

    // #31
    classConstructorWithArgs(a, b, c) {

        // #32
        this.a = function exprFunctionWithArg(a) {
            return 0;
        };

        // #33
        this.b = function() {
            return 0;
        };
    };

    // #34
    get classGetter() {
        return this.a;
    };

    // #35
    set classSetterWithArg(a) {
        this.a = a;
    };

    // #36
    classFun() {
        return 0;
    };

    // #37
    classFunWithArg(a = 0) {
        return a;
    };

    // #38
    classFunWithArgs(a, b, c) {
        return 0;
    };

    // #39
    static classStaticFun() {
        return 0;
    };

    // #40
    static classStaticFunWithArg(a = 0) {
        return a;
    };

    // #41
    static classStaticFunWithArgs(a, b, c) {
        return 0;
    };

    // #42
    classNestedFun() {
        // #43
        function methodFun() {
            return 0;
        }
    }
}