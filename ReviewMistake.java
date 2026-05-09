package com.example.learnit;

import java.util.ArrayList;
import java.util.List;

public class QuestionData {
    private String question;
    private String codeTemplate;
    private List<String> choices;
    private int correctAnswerIndex;
    private String explanation;
    private String expectedOutput;

    public QuestionData(String question, String codeTemplate, List<String> choices,
                        int correctAnswerIndex, String explanation, String expectedOutput) {
        this.question = question;
        this.codeTemplate = codeTemplate;
        this.choices = choices;
        this.correctAnswerIndex = correctAnswerIndex;
        this.explanation = explanation;
        this.expectedOutput = expectedOutput;
    }

    public String getQuestion() {
        return question;
    }

    public String getCodeTemplate() {
        return codeTemplate;
    }

    public List<String> getChoices() {
        return choices;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    // Static method to get all questions for C# Data Acolyte level
    public static List<QuestionData> getDataAcolyteQuestions() {
        List<QuestionData> questions = new ArrayList<>();

        // Question 1: Console.WriteLine with string
        List<String> choices1 = new ArrayList<>();
        choices1.add("\"Hello World\"");
        choices1.add("Hello World");
        choices1.add("'Hello World'");
        choices1.add("HelloWorld");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Hello World\"",
            "Console.WriteLine( );",
            choices1, 0,
            "Console.WriteLine requires a string literal in quotes to output text.",
            "Hello World"
        ));

        // Question 2: Console.WriteLine with WriteLine
        List<String> choices2 = new ArrayList<>();
        choices2.add("WriteLine");
        choices2.add("Print");
        choices2.add("Output");
        choices2.add("Display");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Game Over\"",
            "Console . (\"Game Over\");",
            choices2, 0,
            "WriteLine is the method used to output text with a new line in C#.",
            "Game Over"
        ));

        // Question 3: Console.WriteLine with string literal
        List<String> choices3 = new ArrayList<>();
        choices3.add("\"New message\"");
        choices3.add("New message");
        choices3.add("'New message'");
        choices3.add("NewMessage");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"New message\"",
            "Console.WriteLine( );",
            choices3, 0,
            "Console.WriteLine requires a string literal in quotes to output text.",
            "New message"
        ));

        // Question 4: Console.WriteLine with WriteLine method
        List<String> choices4 = new ArrayList<>();
        choices4.add("WriteLine");
        choices4.add("Print");
        choices4.add("Output");
        choices4.add("Display");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Game Over\"",
            "Console . (\"Game Over\");",
            choices4, 0,
            "WriteLine is the method used to output text with a new line in C#.",
            "Game Over"
        ));

        // Question 5: Console.WriteLine with different string
        List<String> choices5 = new ArrayList<>();
        choices5.add("\"Welcome\"");
        choices5.add("Welcome");
        choices5.add("'Welcome'");
        choices5.add("WelcomeMessage");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Welcome\"",
            "Console.WriteLine( );",
            choices5, 0,
            "Console.WriteLine requires a string literal in quotes to output text.",
            "Welcome"
        ));

        // Question 6: Console.WriteLine with WriteLine method
        List<String> choices6 = new ArrayList<>();
        choices6.add("WriteLine");
        choices6.add("Print");
        choices6.add("Output");
        choices6.add("Display");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Success\"",
            "Console . (\"Success\");",
            choices6, 0,
            "WriteLine is the method used to output text with a new line in C#.",
            "Success"
        ));

        // Question 7: Console.WriteLine with string literal
        List<String> choices7 = new ArrayList<>();
        choices7.add("\"Hello C#\"");
        choices7.add("Hello C#");
        choices7.add("'Hello C#'");
        choices7.add("HelloCSharp");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Hello C#\"",
            "Console.WriteLine( );",
            choices7, 0,
            "Console.WriteLine requires a string literal in quotes to output text.",
            "Hello C#"
        ));

        // Question 8: Console.WriteLine with WriteLine method
        List<String> choices8 = new ArrayList<>();
        choices8.add("WriteLine");
        choices8.add("Print");
        choices8.add("Output");
        choices8.add("Display");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Learning C#\"",
            "Console . (\"Learning C#\");",
            choices8, 0,
            "WriteLine is the method used to output text with a new line in C#.",
            "Learning C#"
        ));

        // Question 9: Console.WriteLine with string literal
        List<String> choices9 = new ArrayList<>();
        choices9.add("\"Programming\"");
        choices9.add("Programming");
        choices9.add("'Programming'");
        choices9.add("ProgrammingText");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Programming\"",
            "Console.WriteLine( );",
            choices9, 0,
            "Console.WriteLine requires a string literal in quotes to output text.",
            "Programming"
        ));

        // Question 10: Console.WriteLine with WriteLine method
        List<String> choices10 = new ArrayList<>();
        choices10.add("WriteLine");
        choices10.add("Print");
        choices10.add("Output");
        choices10.add("Display");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Data Acolyte\"",
            "Console . (\"Data Acolyte\");",
            choices10, 0,
            "WriteLine is the method used to output text with a new line in C#.",
            "Data Acolyte"
        ));

        return questions;
    }

    // Static method to get all questions for C# System Knight level
    public static List<QuestionData> getSystemKnightQuestions() {
        List<QuestionData> questions = new ArrayList<>();

        // Question 1: Variable declaration
        List<String> choices1 = new ArrayList<>();
        choices1.add("int");
        choices1.add("string");
        choices1.add("bool");
        choices1.add("double");
        questions.add(new QuestionData(
            "Drag and drop to declare a variable that stores whole numbers",
            " number = 42;",
            choices1, 0,
            "int is used to declare integer variables in C#.",
            "42"
        ));

        // Question 2: String variable
        List<String> choices2 = new ArrayList<>();
        choices2.add("string");
        choices2.add("int");
        choices2.add("char");
        choices2.add("bool");
        questions.add(new QuestionData(
            "Drag and drop to declare a variable that stores text",
            " message = \"Hello\";",
            choices2, 0,
            "string is used to declare text variables in C#.",
            "Hello"
        ));

        // Question 3: Boolean variable
        List<String> choices3 = new ArrayList<>();
        choices3.add("bool");
        choices3.add("int");
        choices3.add("string");
        choices3.add("double");
        questions.add(new QuestionData(
            "Drag and drop to declare a variable that stores true or false",
            " isActive = true;",
            choices3, 0,
            "bool is used to declare boolean variables in C#.",
            "true"
        ));

        // Question 4: Double variable
        List<String> choices4 = new ArrayList<>();
        choices4.add("double");
        choices4.add("int");
        choices4.add("string");
        choices4.add("bool");
        questions.add(new QuestionData(
            "Drag and drop to declare a variable that stores decimal numbers",
            " price = 19.99;",
            choices4, 0,
            "double is used to declare decimal number variables in C#.",
            "19.99"
        ));

        // Question 5: Variable assignment
        List<String> choices5 = new ArrayList<>();
        choices5.add("=");
        choices5.add("==");
        choices5.add("+=");
        choices5.add("-=");
        questions.add(new QuestionData(
            "Drag and drop to assign a value to a variable",
            "int x  5;",
            choices5, 0,
            "= is used to assign values to variables in C#.",
            "5"
        ));

        // Question 6: String concatenation
        List<String> choices6 = new ArrayList<>();
        choices6.add("+");
        choices6.add("-");
        choices6.add("*");
        choices6.add("/");
        questions.add(new QuestionData(
            "Drag and drop to combine two strings",
            "string result = \"Hello\"  \"World\";",
            choices6, 0,
            "+ is used to concatenate strings in C#.",
            "HelloWorld"
        ));

        // Question 7: Integer addition
        List<String> choices7 = new ArrayList<>();
        choices7.add("+");
        choices7.add("-");
        choices7.add("*");
        choices7.add("/");
        questions.add(new QuestionData(
            "Drag and drop to add two numbers",
            "int sum = 10  5;",
            choices7, 0,
            "+ is used to add numbers in C#.",
            "15"
        ));

        // Question 8: Variable declaration with value
        List<String> choices8 = new ArrayList<>();
        choices8.add("int");
        choices8.add("string");
        choices8.add("bool");
        choices8.add("double");
        questions.add(new QuestionData(
            "Drag and drop to declare a variable that stores age",
            " age = 25;",
            choices8, 0,
            "int is used to declare integer variables in C#.",
            "25"
        ));

        // Question 9: String literal
        List<String> choices9 = new ArrayList<>();
        choices9.add("\"System Knight\"");
        choices9.add("System Knight");
        choices9.add("'System Knight'");
        choices9.add("SystemKnight");
        questions.add(new QuestionData(
            "Drag and drop to create a string literal",
            "string level = ;",
            choices9, 0,
            "String literals must be enclosed in double quotes in C#.",
            "System Knight"
        ));

        // Question 10: Boolean literal
        List<String> choices10 = new ArrayList<>();
        choices10.add("false");
        choices10.add("0");
        choices10.add("\"false\"");
        choices10.add("False");
        questions.add(new QuestionData(
            "Drag and drop to assign a false value",
            "bool flag = ;",
            choices10, 0,
            "false is a boolean literal in C#.",
            "false"
        ));

        return questions;
    }

    // Static method to get all questions for C# Code Warden level
    public static List<QuestionData> getCodeWardenQuestions() {
        List<QuestionData> questions = new ArrayList<>();

        // Question 1: If statement
        List<String> choices1 = new ArrayList<>();
        choices1.add("if");
        choices1.add("for");
        choices1.add("while");
        choices1.add("switch");
        questions.add(new QuestionData(
            "Drag and drop to start a conditional statement",
            " (x > 0) { Console.WriteLine(\"Positive\"); }",
            choices1, 0,
            "if is used to start conditional statements in C#.",
            "Positive"
        ));

        // Question 2: For loop
        List<String> choices2 = new ArrayList<>();
        choices2.add("for");
        choices2.add("if");
        choices2.add("while");
        choices2.add("do");
        questions.add(new QuestionData(
            "Drag and drop to start a counting loop",
            " (int i = 0; i < 5; i++) { Console.WriteLine(i); }",
            choices2, 0,
            "for is used to create counting loops in C#.",
            "0,1,2,3,4"
        ));

        // Question 3: While loop
        List<String> choices3 = new ArrayList<>();
        choices3.add("while");
        choices3.add("for");
        choices3.add("if");
        choices3.add("switch");
        questions.add(new QuestionData(
            "Drag and drop to start a condition-based loop",
            " (count > 0) { count--; }",
            choices3, 0,
            "while is used to create condition-based loops in C#.",
            "count decremented"
        ));

        // Question 4: Method declaration
        List<String> choices4 = new ArrayList<>();
        choices4.add("void");
        choices4.add("int");
        choices4.add("string");
        choices4.add("bool");
        questions.add(new QuestionData(
            "Drag and drop to declare a method that doesn't return a value",
            " PrintMessage() { Console.WriteLine(\"Hello\"); }",
            choices4, 0,
            "void is used for methods that don't return a value in C#.",
            "Hello"
        ));

        // Question 5: Return statement
        List<String> choices5 = new ArrayList<>();
        choices5.add("return");
        choices5.add("break");
        choices5.add("continue");
        choices5.add("exit");
        questions.add(new QuestionData(
            "Drag and drop to return a value from a method",
            " 42;",
            choices5, 0,
            "return is used to return values from methods in C#.",
            "42"
        ));

        // Question 6: Array declaration
        List<String> choices6 = new ArrayList<>();
        choices6.add("[]");
        choices6.add("()");
        choices6.add("{}");
        choices6.add("<>");
        questions.add(new QuestionData(
            "Drag and drop to declare an array",
            "int numbers = new int[5];",
            choices6, 0,
            "[] is used to declare arrays in C#.",
            "array of 5 integers"
        ));

        // Question 7: Class declaration
        List<String> choices7 = new ArrayList<>();
        choices7.add("class");
        choices7.add("struct");
        choices7.add("interface");
        choices7.add("enum");
        questions.add(new QuestionData(
            "Drag and drop to declare a class",
            " Program { }",
            choices7, 0,
            "class is used to declare classes in C#.",
            "class definition"
        ));

        // Question 8: Constructor
        List<String> choices8 = new ArrayList<>();
        choices8.add("public");
        choices8.add("private");
        choices8.add("protected");
        choices8.add("internal");
        questions.add(new QuestionData(
            "Drag and drop to make a constructor accessible",
            " Program() { }",
            choices8, 0,
            "public makes constructors accessible from outside the class in C#.",
            "public constructor"
        ));

        // Question 9: Try-catch block
        List<String> choices9 = new ArrayList<>();
        choices9.add("try");
        choices9.add("if");
        choices9.add("for");
        choices9.add("while");
        questions.add(new QuestionData(
            "Drag and drop to start exception handling",
            " { // risky code } catch { // handle error }",
            choices9, 0,
            "try is used to start exception handling blocks in C#.",
            "exception handling"
        ));

        // Question 10: Using statement
        List<String> choices10 = new ArrayList<>();
        choices10.add("using");
        choices10.add("import");
        choices10.add("include");
        choices10.add("require");
        questions.add(new QuestionData(
            "Drag and drop to import a namespace",
            " System;",
            choices10, 0,
            "using is used to import namespaces in C#.",
            "System namespace"
        ));

        return questions;
    }

    // Static method to get all questions for C# Tech Emperor level
    public static List<QuestionData> getTechEmperorQuestions() {
        List<QuestionData> questions = new ArrayList<>();

        // Question 1: Interface declaration
        List<String> choices1 = new ArrayList<>();
        choices1.add("interface");
        choices1.add("class");
        choices1.add("struct");
        choices1.add("enum");
        questions.add(new QuestionData(
            "Drag and drop to declare an interface",
            " ILogger { void Log(string message); }",
            choices1, 0,
            "interface is used to declare interfaces in C#.",
            "interface definition"
        ));

        // Question 2: Generic type parameter
        List<String> choices2 = new ArrayList<>();
        choices2.add("<T>");
        choices2.add("(T)");
        choices2.add("[T]");
        choices2.add("{T}");
        questions.add(new QuestionData(
            "Drag and drop to declare a generic type parameter",
            "class List { }",
            choices2, 0,
            "<T> is used to declare generic type parameters in C#.",
            "generic list"
        ));

        // Question 3: Lambda expression
        List<String> choices3 = new ArrayList<>();
        choices3.add("=>");
        choices3.add("->");
        choices3.add("=");
        choices3.add("==");
        questions.add(new QuestionData(
            "Drag and drop to create a lambda expression",
            "Func<int, int> square = x  x * x;",
            choices3, 0,
            "=> is used to create lambda expressions in C#.",
            "lambda function"
        ));

        // Question 4: Async method
        List<String> choices4 = new ArrayList<>();
        choices4.add("async");
        choices4.add("await");
        choices4.add("task");
        choices4.add("thread");
        questions.add(new QuestionData(
            "Drag and drop to declare an asynchronous method",
            " Task<string> GetDataAsync() { }",
            choices4, 0,
            "async is used to declare asynchronous methods in C#.",
            "async method"
        ));

        // Question 5: Await keyword
        List<String> choices5 = new ArrayList<>();
        choices5.add("await");
        choices5.add("async");
        choices5.add("wait");
        choices5.add("yield");
        questions.add(new QuestionData(
            "Drag and drop to wait for an asynchronous operation",
            "string result =  GetDataAsync();",
            choices5, 0,
            "await is used to wait for asynchronous operations in C#.",
            "await result"
        ));

        // Question 6: LINQ Where clause
        List<String> choices6 = new ArrayList<>();
        choices6.add("Where");
        choices6.add("Select");
        choices6.add("OrderBy");
        choices6.add("GroupBy");
        questions.add(new QuestionData(
            "Drag and drop to filter a collection",
            "var filtered = numbers. (x => x > 0);",
            choices6, 0,
            "Where is used to filter collections in LINQ.",
            "filtered collection"
        ));

        // Question 7: LINQ Select clause
        List<String> choices7 = new ArrayList<>();
        choices7.add("Select");
        choices7.add("Where");
        choices7.add("OrderBy");
        choices7.add("GroupBy");
        questions.add(new QuestionData(
            "Drag and drop to transform a collection",
            "var doubled = numbers. (x => x * 2);",
            choices7, 0,
            "Select is used to transform collections in LINQ.",
            "transformed collection"
        ));

        // Question 8: Delegate declaration
        List<String> choices8 = new ArrayList<>();
        choices8.add("delegate");
        choices8.add("event");
        choices8.add("action");
        choices8.add("func");
        questions.add(new QuestionData(
            "Drag and drop to declare a delegate",
            " void Logger(string message);",
            choices8, 0,
            "delegate is used to declare delegates in C#.",
            "delegate type"
        ));

        // Question 9: Event declaration
        List<String> choices9 = new ArrayList<>();
        choices9.add("event");
        choices9.add("delegate");
        choices9.add("action");
        choices9.add("func");
        questions.add(new QuestionData(
            "Drag and drop to declare an event",
            " EventHandler<string> MessageReceived;",
            choices9, 0,
            "event is used to declare events in C#.",
            "event declaration"
        ));

        // Question 10: Attribute declaration
        List<String> choices10 = new ArrayList<>();
        choices10.add("[");
        choices10.add("(");
        choices10.add("{");
        choices10.add("<");
        questions.add(new QuestionData(
            "Drag and drop to start an attribute",
            " Obsolete] public void OldMethod() { }",
            choices10, 0,
            "[ is used to start attributes in C#.",
            "attribute declaration"
        ));

        return questions;
    }

    // Static method to get all questions for Java Data Acolyte level
    public static List<QuestionData> getJavaDataAcolyteQuestions() {
        List<QuestionData> questions = new ArrayList<>();

        // Question 1: System.out.println with string
        List<String> choices1 = new ArrayList<>();
        choices1.add("\"Hello World\"");
        choices1.add("Hello World");
        choices1.add("'Hello World'");
        choices1.add("HelloWorld");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Hello World\"",
            "System.out.println( );",
            choices1, 0,
            "System.out.println requires a string literal in quotes to output text.",
            "Hello World"
        ));

        // Question 2: System.out.println with println
        List<String> choices2 = new ArrayList<>();
        choices2.add("println");
        choices2.add("print");
        choices2.add("output");
        choices2.add("display");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Game Over\"",
            "System.out. (\"Game Over\");",
            choices2, 0,
            "println is the method used to output text with a new line in Java.",
            "Game Over"
        ));

        // Question 3: System.out.println with string literal
        List<String> choices3 = new ArrayList<>();
        choices3.add("\"New message\"");
        choices3.add("New message");
        choices3.add("'New message'");
        choices3.add("NewMessage");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"New message\"",
            "System.out.println( );",
            choices3, 0,
            "System.out.println requires a string literal in quotes to output text.",
            "New message"
        ));

        // Question 4: System.out.println with println method
        List<String> choices4 = new ArrayList<>();
        choices4.add("println");
        choices4.add("print");
        choices4.add("output");
        choices4.add("display");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Game Over\"",
            "System.out. (\"Game Over\");",
            choices4, 0,
            "println is the method used to output text with a new line in Java.",
            "Game Over"
        ));

        // Question 5: System.out.println with different string
        List<String> choices5 = new ArrayList<>();
        choices5.add("\"Welcome\"");
        choices5.add("Welcome");
        choices5.add("'Welcome'");
        choices5.add("WelcomeMessage");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Welcome\"",
            "System.out.println( );",
            choices5, 0,
            "System.out.println requires a string literal in quotes to output text.",
            "Welcome"
        ));

        // Question 6: System.out.println with println method
        List<String> choices6 = new ArrayList<>();
        choices6.add("println");
        choices6.add("print");
        choices6.add("output");
        choices6.add("display");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Success\"",
            "System.out. (\"Success\");",
            choices6, 0,
            "println is the method used to output text with a new line in Java.",
            "Success"
        ));

        // Question 7: System.out.println with string literal
        List<String> choices7 = new ArrayList<>();
        choices7.add("\"Hello Java\"");
        choices7.add("Hello Java");
        choices7.add("'Hello Java'");
        choices7.add("HelloJava");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Hello Java\"",
            "System.out.println( );",
            choices7, 0,
            "System.out.println requires a string literal in quotes to output text.",
            "Hello Java"
        ));

        // Question 8: System.out.println with println method
        List<String> choices8 = new ArrayList<>();
        choices8.add("println");
        choices8.add("print");
        choices8.add("output");
        choices8.add("display");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Learning Java\"",
            "System.out. (\"Learning Java\");",
            choices8, 0,
            "println is the method used to output text with a new line in Java.",
            "Learning Java"
        ));

        // Question 9: System.out.println with string literal
        List<String> choices9 = new ArrayList<>();
        choices9.add("\"Programming\"");
        choices9.add("Programming");
        choices9.add("'Programming'");
        choices9.add("ProgrammingText");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Programming\"",
            "System.out.println( );",
            choices9, 0,
            "System.out.println requires a string literal in quotes to output text.",
            "Programming"
        ));

        // Question 10: System.out.println with println method
        List<String> choices10 = new ArrayList<>();
        choices10.add("println");
        choices10.add("print");
        choices10.add("output");
        choices10.add("display");
        questions.add(new QuestionData(
            "Drag and drop to complete a line of code that outputs \"Data Acolyte\"",
            "System.out. (\"Data Acolyte\");",
            choices10, 0,
            "println is the method used to output text with a new line in Java.",
            "Data Acolyte"
        ));

        return questions;
    }

    // Static method to get all questions for Java System Knight level
    public static List<QuestionData> getJavaSystemKnightQuestions() {
        List<QuestionData> questions = new ArrayList<>();

        // Question 1: Variable declaration
        List<String> choices1 = new ArrayList<>();
        choices1.add("int");
        choices1.add("String");
        choices1.add("boolean");
        choices1.add("double");
        questions.add(new QuestionData(
            "Drag and drop to declare a variable that stores whole numbers",
            " number = 42;",
            choices1, 0,
            "int is used to declare integer variables in Java.",
            "42"
        ));

        // Question 2: String variable
        List<String> choices2 = new ArrayList<>();
        choices2.add("String");
        choices2.add("int");
        choices2.add("char");
        choices2.add("boolean");
        questions.add(new QuestionData(
            "Drag and drop to declare a variable that stores text",
            " message = \"Hello\";",
            choices2, 0,
            "String is used to declare text variables in Java.",
            "Hello"
        ));

        // Question 3: Boolean variable
        List<String> choices3 = new ArrayList<>();
        choices3.add("boolean");
        choices3.add("int");
        choices3.add("String");
        choices3.add("double");
        questions.add(new QuestionData(
            "Drag and drop to declare a variable that stores true or false",
            " isActive = true;",
            choices3, 0,
            "boolean is used to declare boolean variables in Java.",
            "true"
        ));

        // Question 4: Double variable
        List<String> choices4 = new ArrayList<>();
        choices4.add("double");
        choices4.add("int");
        choices4.add("String");
        choices4.add("boolean");
        questions.add(new QuestionData(
            "Drag and drop to declare a variable that stores decimal numbers",
            " price = 19.99;",
            choices4, 0,
            "double is used to declare decimal number variables in Java.",
            "19.99"
        ));

        // Question 5: Variable assignment
        List<String> choices5 = new ArrayList<>();
        choices5.add("=");
        choices5.add("==");
        choices5.add("+=");
        choices5.add("-=");
        questions.add(new QuestionData(
            "Drag and drop to assign a value to a variable",
            "int x  5;",
            choices5, 0,
            "= is used to assign values to variables in Java.",
            "5"
        ));

        // Question 6: String concatenation
        List<String> choices6 = new ArrayList<>();
        choices6.add("+");
        choices6.add("-");
        choices6.add("*");
        choices6.add("/");
        questions.add(new QuestionData(
            "Drag and drop to combine two strings",
            "String result = \"Hello\"  \"World\";",
            choices6, 0,
            "+ is used to concatenate strings in Java.",
            "HelloWorld"
        ));

        // Question 7: Integer addition
        List<String> choices7 = new ArrayList<>();
        choices7.add("+");
        choices7.add("-");
        choices7.add("*");
        choices7.add("/");
        questions.add(new QuestionData(
            "Drag and drop to add two numbers",
            "int sum = 10  5;",
            choices7, 0,
            "+ is used to add numbers in Java.",
            "15"
        ));

        // Question 8: Variable declaration with value
        List<String> choices8 = new ArrayList<>();
        choices8.add("int");
        choices8.add("String");
        choices8.add("boolean");
        choices8.add("double");
        questions.add(new QuestionData(
            "Drag and drop to declare a variable that stores age",
            " age = 25;",
            choices8, 0,
            "int is used to declare integer variables in Java.",
            "25"
        ));

        // Question 9: String literal
        List<String> choices9 = new ArrayList<>();
        choices9.add("\"System Knight\"");
        choices9.add("System Knight");
        choices9.add("'System Knight'");
        choices9.add("SystemKnight");
        questions.add(new QuestionData(
            "Drag and drop to create a string literal",
            "String level = ;",
            choices9, 0,
            "String literals must be enclosed in double quotes in Java.",
            "System Knight"
        ));

        // Question 10: Boolean literal
        List<String> choices10 = new ArrayList<>();
        choices10.add("false");
        choices10.add("0");
        choices10.add("\"false\"");
        choices10.add("False");
        questions.add(new QuestionData(
            "Drag and drop to assign a false value",
            "boolean flag = ;",
            choices10, 0,
            "false is a boolean literal in Java.",
            "false"
        ));

        return questions;
    }

    // Static method to get all questions for Java Code Warden level
    public static List<QuestionData> getJavaCodeWardenQuestions() {
        List<QuestionData> questions = new ArrayList<>();

        // Question 1: If statement
        List<String> choices1 = new ArrayList<>();
        choices1.add("if");
        choices1.add("for");
        choices1.add("while");
        choices1.add("switch");
        questions.add(new QuestionData(
            "Drag and drop to start a conditional statement",
            " (x > 0) { System.out.println(\"Positive\"); }",
            choices1, 0,
            "if is used to start conditional statements in Java.",
            "Positive"
        ));

        // Question 2: For loop
        List<String> choices2 = new ArrayList<>();
        choices2.add("for");
        choices2.add("if");
        choices2.add("while");
        choices2.add("do");
        questions.add(new QuestionData(
            "Drag and drop to start a counting loop",
            " (int i = 0; i < 5; i++) { System.out.println(i); }",
            choices2, 0,
            "for is used to create counting loops in Java.",
            "0,1,2,3,4"
        ));

        // Question 3: While loop
        List<String> choices3 = new ArrayList<>();
        choices3.add("while");
        choices3.add("for");
        choices3.add("if");
        choices3.add("switch");
        questions.add(new QuestionData(
            "Drag and drop to start a condition-based loop",
            " (count > 0) { count--; }",
            choices3, 0,
            "while is used to create condition-based loops in Java.",
            "count decremented"
        ));

        // Question 4: Method declaration
        List<String> choices4 = new ArrayList<>();
        choices4.add("void");
        choices4.add("int");
        choices4.add("String");
        choices4.add("boolean");
        questions.add(new QuestionData(
            "Drag and drop to declare a method that doesn't return a value",
            " printMessage() { System.out.println(\"Hello\"); }",
            choices4, 0,
            "void is used for methods that don't return a value in Java.",
            "Hello"
        ));

        // Question 5: Return statement
        List<String> choices5 = new ArrayList<>();
        choices5.add("return");
        choices5.add("break");
        choices5.add("continue");
        choices5.add("exit");
        questions.add(new QuestionData(
            "Drag and drop to return a value from a method",
            " 42;",
            choices5, 0,
            "return is used to return values from methods in Java.",
            "42"
        ));

        // Question 6: Array declaration
        List<String> choices6 = new ArrayList<>();
        choices6.add("[]");
        choices6.add("()");
        choices6.add("{}");
        choices6.add("<>");
        questions.add(new QuestionData(
            "Drag and drop to declare an array",
            "int[] numbers = new int[5];",
            choices6, 0,
            "[] is used to declare arrays in Java.",
            "array of 5 integers"
        ));

        // Question 7: Class declaration
        List<String> choices7 = new ArrayList<>();
        choices7.add("class");
        choices7.add("interface");
        choices7.add("enum");
        choices7.add("abstract");
        questions.add(new QuestionData(
            "Drag and drop to declare a class",
            " Program { }",
            choices7, 0,
            "class is used to declare classes in Java.",
            "class definition"
        ));

        // Question 8: Constructor
        List<String> choices8 = new ArrayList<>();
        choices8.add("public");
        choices8.add("private");
        choices8.add("protected");
        choices8.add("default");
        questions.add(new QuestionData(
            "Drag and drop to make a constructor accessible",
            " Program() { }",
            choices8, 0,
            "public makes constructors accessible from outside the class in Java.",
            "public constructor"
        ));

        // Question 9: Try-catch block
        List<String> choices9 = new ArrayList<>();
        choices9.add("try");
        choices9.add("if");
        choices9.add("for");
        choices9.add("while");
        questions.add(new QuestionData(
            "Drag and drop to start exception handling",
            " { // risky code } catch { // handle error }",
            choices9, 0,
            "try is used to start exception handling blocks in Java.",
            "exception handling"
        ));

        // Question 10: Import statement
        List<String> choices10 = new ArrayList<>();
        choices10.add("import");
        choices10.add("using");
        choices10.add("include");
        choices10.add("require");
        questions.add(new QuestionData(
            "Drag and drop to import a package",
            " java.util.Scanner;",
            choices10, 0,
            "import is used to import packages in Java.",
            "Scanner class"
        ));

        return questions;
    }

    // Static method to get all questions for Java Tech Emperor level
    public static List<QuestionData> getJavaTechEmperorQuestions() {
        List<QuestionData> questions = new ArrayList<>();

        // Question 1: Interface declaration
        List<String> choices1 = new ArrayList<>();
        choices1.add("interface");
        choices1.add("class");
        choices1.add("abstract");
        choices1.add("enum");
        questions.add(new QuestionData(
            "Drag and drop to declare an interface",
            " Logger { void log(String message); }",
            choices1, 0,
            "interface is used to declare interfaces in Java.",
            "interface definition"
        ));

        // Question 2: Generic type parameter
        List<String> choices2 = new ArrayList<>();
        choices2.add("<T>");
        choices2.add("(T)");
        choices2.add("[T]");
        choices2.add("{T}");
        questions.add(new QuestionData(
            "Drag and drop to declare a generic type parameter",
            "class List { }",
            choices2, 0,
            "<T> is used to declare generic type parameters in Java.",
            "generic list"
        ));

        // Question 3: Lambda expression
        List<String> choices3 = new ArrayList<>();
        choices3.add("->");
        choices3.add("=>");
        choices3.add("=");
        choices3.add("==");
        questions.add(new QuestionData(
            "Drag and drop to create a lambda expression",
            "Function<Integer, Integer> square = x  x * x;",
            choices3, 0,
            "-> is used to create lambda expressions in Java.",
            "lambda function"
        ));

        // Question 4: Stream filter
        List<String> choices4 = new ArrayList<>();
        choices4.add("filter");
        choices4.add("map");
        choices4.add("collect");
        choices4.add("forEach");
        questions.add(new QuestionData(
            "Drag and drop to filter a stream",
            "numbers.stream(). (x -> x > 0).collect(Collectors.toList());",
            choices4, 0,
            "filter is used to filter streams in Java.",
            "filtered stream"
        ));

        // Question 5: Stream map
        List<String> choices5 = new ArrayList<>();
        choices5.add("map");
        choices5.add("filter");
        choices5.add("collect");
        choices5.add("forEach");
        questions.add(new QuestionData(
            "Drag and drop to transform a stream",
            "numbers.stream(). (x -> x * 2).collect(Collectors.toList());",
            choices5, 0,
            "map is used to transform streams in Java.",
            "transformed stream"
        ));

        // Question 6: Optional
        List<String> choices6 = new ArrayList<>();
        choices6.add("Optional");
        choices6.add("Maybe");
        choices6.add("Nullable");
        choices6.add("Option");
        questions.add(new QuestionData(
            "Drag and drop to create an optional value",
            "<String> name = Optional.of(\"John\");",
            choices6, 0,
            "Optional is used to handle nullable values in Java.",
            "optional value"
        ));

        // Question 7: Annotation
        List<String> choices7 = new ArrayList<>();
        choices7.add("@");
        choices7.add("#");
        choices7.add("$");
        choices7.add("%");
        questions.add(new QuestionData(
            "Drag and drop to start an annotation",
            " Override public String toString() { }",
            choices7, 0,
            "@ is used to start annotations in Java.",
            "annotation"
        ));

        // Question 8: Enum declaration
        List<String> choices8 = new ArrayList<>();
        choices8.add("enum");
        choices8.add("class");
        choices8.add("interface");
        choices8.add("abstract");
        questions.add(new QuestionData(
            "Drag and drop to declare an enumeration",
            " Color { RED, GREEN, BLUE }",
            choices8, 0,
            "enum is used to declare enumerations in Java.",
            "enumeration"
        ));

        // Question 9: Thread creation
        List<String> choices9 = new ArrayList<>();
        choices9.add("Thread");
        choices9.add("Runnable");
        choices9.add("Executor");
        choices9.add("Future");
        questions.add(new QuestionData(
            "Drag and drop to create a thread",
            " thread = new (() -> System.out.println(\"Hello\"));",
            choices9, 0,
            "Thread is used to create threads in Java.",
            "thread object"
        ));

        // Question 10: Synchronized keyword
        List<String> choices10 = new ArrayList<>();
        choices10.add("synchronized");
        choices10.add("volatile");
        choices10.add("final");
        choices10.add("static");
        questions.add(new QuestionData(
            "Drag and drop to make a method thread-safe",
            " void increment() { count++; }",
            choices10, 0,
            "synchronized is used to make methods thread-safe in Java.",
            "thread-safe method"
        ));
        return questions;
    }
} 