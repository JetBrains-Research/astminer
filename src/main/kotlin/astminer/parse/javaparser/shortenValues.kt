package astminer.parse.javaparser

/**
 * Shorten values used to decrease memory usage. This workaround was taken from code2seq repository.
 * @see [link][https://github.com/tech-srl/code2seq/blob/428b1896a87e0c34af5a407c709c56047ec99a07/
 * JavaExtractor/JPredict/src/main/java/JavaExtractor/FeaturesEntities/Property.java]
 * */
val SHORTEN_VALUES = hashMapOf(
    "ArrayAccessExpr" to "ArAc",
    "ArrayBracketPair" to "ArBr",
    "ArrayCreationExpr" to "ArCr",
    "ArrayCreationLevel" to "ArCrLvl",
    "ArrayInitializerExpr" to "ArIn",
    "ArrayType" to "ArTy",
    "AssertStmt" to "Asrt",
    "AssignExpr:and" to "AsAn",
    "AssignExpr:assign" to "As",
    "AssignExpr:lShift" to "AsLS",
    "AssignExpr:minus" to "AsMi",
    "AssignExpr:or" to "AsOr",
    "AssignExpr:plus" to "AsP",
    "AssignExpr:rem" to "AsRe",
    "AssignExpr:rSignedShift" to "AsRSS",
    "AssignExpr:rUnsignedShift" to "AsRUS",
    "AssignExpr:slash" to "AsSl",
    "AssignExpr:star" to "AsSt",
    "AssignExpr:xor" to "AsX",
    "BinaryExpr:and" to "And",
    "BinaryExpr:binAnd" to "BinAnd",
    "BinaryExpr:binOr" to "BinOr",
    "BinaryExpr:divide" to "Div",
    "BinaryExpr:equals" to "Eq",
    "BinaryExpr:greater" to "Gt",
    "BinaryExpr:greaterEquals" to "Geq",
    "BinaryExpr:less" to "Ls",
    "BinaryExpr:lessEquals" to "Leq",
    "BinaryExpr:lShift" to "LS",
    "BinaryExpr:minus" to "Minus",
    "BinaryExpr:notEquals" to "Neq",
    "BinaryExpr:or" to "Or",
    "BinaryExpr:plus" to "Plus",
    "BinaryExpr:remainder" to "Mod",
    "BinaryExpr:rSignedShift" to "RSS",
    "BinaryExpr:rUnsignedShift" to "RUS",
    "BinaryExpr:times" to "Mul",
    "BinaryExpr:xor" to "Xor",
    "BlockStmt" to "Bk",
    "BooleanLiteralExpr" to "BoolEx",
    "CastExpr" to "Cast",
    "CatchClause" to "Catch",
    "CharLiteralExpr" to "CharEx",
    "ClassExpr" to "ClsEx",
    "ClassOrInterfaceDeclaration" to "ClsD",
    "ClassOrInterfaceType" to "Cls",
    "ConditionalExpr" to "Cond",
    "ConstructorDeclaration" to "Ctor",
    "DoStmt" to "Do",
    "DoubleLiteralExpr" to "Dbl",
    "EmptyMemberDeclaration" to "Emp",
    "EnclosedExpr" to "Enc",
    "EnumDeclaration" to "EnD",
    "ExplicitConstructorInvocationStmt" to "ExpCtor",
    "ExpressionStmt" to "Ex",
    "FieldAccessExpr" to "Fld",
    "FieldDeclaration" to "FldDec",
    "ForeachStmt" to "Foreach",
    "ForStmt" to "For",
    "IfStmt" to "If",
    "InitializerDeclaration" to "Init",
    "InstanceOfExpr" to "InstanceOf",
    "IntegerLiteralExpr" to "IntEx",
    "IntegerLiteralMinValueExpr" to "IntMinEx",
    "LabeledStmt" to "Labeled",
    "LambdaExpr" to "Lambda",
    "LongLiteralExpr" to "LongEx",
    "MarkerAnnotationExpr" to "MarkerExpr",
    "MemberValuePair" to "Mvp",
    "MethodCallExpr" to "Cal",
    "MethodDeclaration" to "Mth",
    "MethodReferenceExpr" to "MethRef",
    "NameExpr" to "Nm",
    "NormalAnnotationExpr" to "NormEx",
    "NullLiteralExpr" to "Null",
    "ObjectCreationExpr" to "ObjEx",
    "Parameter" to "Prm",
    "PrimitiveType" to "Prim",
    "QualifiedNameExpr" to "Qua",
    "ReturnStmt" to "Ret",
    "SingleMemberAnnotationExpr" to "SMEx",
    "StringLiteralExpr" to "StrEx",
    "SuperExpr" to "SupEx",
    "SwitchEntryStmt" to "SwiEnt",
    "SwitchStmt" to "Switch",
    "SynchronizedStmt" to "Sync",
    "ThisExpr" to "This",
    "ThrowStmt" to "Thro",
    "TryStmt" to "Try",
    "TypeDeclarationStmt" to "TypeDec",
    "TypeExpr" to "Type",
    "TypeParameter" to "TypePar",
    "UnaryExpr:inverse" to "Inverse",
    "UnaryExpr:negative" to "Neg",
    "UnaryExpr:not" to "Not",
    "UnaryExpr:posDecrement" to "PosDec",
    "UnaryExpr:posIncrement" to "PosInc",
    "UnaryExpr:positive" to "Pos",
    "UnaryExpr:preDecrement" to "PreDec",
    "UnaryExpr:preIncrement" to "PreInc",
    "UnionType" to "Unio",
    "VariableDeclarationExpr" to "VDE",
    "VariableDeclarator" to "VD",
    "VariableDeclaratorId" to "VDID",
    "VoidType" to "Void",
    "WhileStmt" to "While",
    "WildcardType" to "Wild",
)
