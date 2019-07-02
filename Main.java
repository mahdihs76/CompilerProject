import parser.Parser;
import tokenizer.Token;
import tokenizer.Tokenizer;
import utility.FileHelper;

public class Main {

    public static void main(String[] args) {

        String inputProgram = FileHelper.readInput("input.txt");


        Tokenizer tokenizer = new Tokenizer(inputProgram);
        Parser parser = new Parser(tokenizer);

        try {
            parser.parse_program();
        }catch (Exception ignored){ /* do nothing */ }

        FileHelper.writeOutput(tokenizer.getResultString(), "tokens.txt");
        FileHelper.writeOutput(tokenizer.getLexicalErrorsString(), "lexicalErrors.txt");

        FileHelper.writeOutput(parser.getParsingErrorsString(), "parsingErrors.txt");
        FileHelper.writeOutput(parser.getParseTreeString(), "parseTree.txt");


        FileHelper.writeOutput(parser.getSemanticAnalyser().getIntermediateCodeString(), "instructions.txt");
        FileHelper.writeOutput(parser.getSemanticAnalyser().getSemanticErrorsString(), "semanticErrors.txt");

    }
}
