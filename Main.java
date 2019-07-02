import parser.Parser;
import semantic_analysis.Instruction;
import semantic_analysis.SemanticAnalyser;
import tokenizer.Token;
import tokenizer.Tokenizer;
import utility.FileHelper;
import utility.HashUtils;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        String inputProgram = FileHelper.readInput("input.txt");


        Tokenizer tokenizer = new Tokenizer(inputProgram);
        Parser parser = new Parser(tokenizer);

        try {
            parser.parse_program();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileHelper.writeOutput(tokenizer.getResultString(), "tokens.txt");
        FileHelper.writeOutput(tokenizer.getLexicalErrorsString(), "lexicalErrors.txt");

        FileHelper.writeOutput(parser.getParsingErrorsString(), "parsingErrors.txt");
        FileHelper.writeOutput(parser.getParseTreeString(), "parseTree.txt");


        SemanticAnalyser analyser = parser.getSemanticAnalyser();

        HashUtils.normalizeHashOperands(analyser);
        FileHelper.writeOutput(analyser.getIntermediateCodeString(), "instructions.txt");
        FileHelper.writeOutput(analyser.getSemanticErrorsString(), "semanticErrors.txt");

    }

}
