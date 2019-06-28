package semantic_analysis;

import java.util.ArrayList;


public class SymbolTable {

    private class Symbol {
        public String name;
        public int address;

        public Symbol(String name, int address) {
            this.name = name;
            this.address = address;
        }
    }


    private ArrayList<Symbol> symbols;
    private int size;

    public SymbolTable() {
        this.size = 0;
        this.symbols = new ArrayList<Symbol>();
    }



    public Integer getAddressByName(String name) {
        for(Symbol symbol: this.symbols) {
            if(symbol.name.equals(name))
                return symbol.address;
        }

        return null;
    }

    public String getNameByAddress(int address) {
        for(Symbol symbol: this.symbols) {
            if(symbol.address == address)
                return symbol.name;
        }

        return null;
    }

    public boolean doesVarExist(String varName) {
        for(Symbol symbol: this.symbols) {
            if(symbol.name.equals(varName))
                return true;
        }

        return false;
    }


}







