import parser.Parser;
import tokenizer.Token;
import tokenizer.Tokenizer;
import utility.FileHelper;

public class Main {

    public static void main(String[] args) {

        String inputProgram = FileHelper.readInput("input.txt");


        Tokenizer tokenizer = new Tokenizer(inputProgram);
        while(true) {
            Token newToken = tokenizer.get_next_token();
            if(newToken == null)
                break;
        }
        FileHelper.writeOutput(tokenizer.getResultString(), "tokens.txt");
        FileHelper.writeOutput(tokenizer.getLexicalErrorsString(), "lexicalErrors.txt");


        Parser parser = new Parser(tokenizer.getResult());
        try {
            parser.parse_program();
        }catch (Exception ignored){ /* do nothing */ }
        FileHelper.writeOutput(parser.getParsingErrorsString(), "parsingErrors.txt");
        FileHelper.writeOutput(parser.getParseTreeString(), "parseTree.txt");



    }
}
