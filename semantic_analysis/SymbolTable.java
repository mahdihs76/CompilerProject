package semantic_analysis;

import java.util.ArrayList;


public class SymbolTable {

    public class Symbol {
        public String name;
        public int address;
        public VarType varType;

        public Symbol(String name, int address, VarType varType) {
            this(name, address);
            this.varType = varType;
        }
        public Symbol(String name, int address) {
            this.name = name;
            this.address = address;
        }

        public String getName() {
            return this.name;
        }

        public int getAddress() {
            return this.address;
        }
    }


    private ArrayList<Symbol> symbols;

    public SymbolTable() {
        this.symbols = new ArrayList<Symbol>();
    }


    public int getSize() {
        return this.symbols.size();
    }

    public int getAddressByName(String name) {
        for (Symbol symbol : this.symbols) {
            if (symbol.name.equals(name))
                return symbol.address;
        }

        return -1;
    }

    public String getNameByAddress(int address) {
        return getByAddress(address).getName();
    }


    public Symbol getByAddress(int address) {
        for (Symbol symbol : this.symbols) {
            if (symbol.address == address)
                return symbol;
        }

        return null;
    }

    public boolean doesVarExist(String varName) {
        for (Symbol symbol : this.symbols) {
            if (symbol.name.equals(varName))
                return true;
        }

        return false;
    }

    public void addSymbol(String name, int address, VarType varType) {
        Symbol newSymbol = new Symbol(name, address, varType);
        this.symbols.add(newSymbol);
    }

    public void addSymbol(String name, int address) {
        Symbol newSymbol = new Symbol(name, address);
        this.symbols.add(newSymbol);
    }
    public void removeSymbol(String name) {
        int i = 0;
        for (i = 0; i < this.symbols.size(); i++) {
            if (symbols.get(i).name.equals(name))
                break;
        }
        this.symbols.remove(i);
    }

    public void removeLastSymbol() {
        this.symbols.remove(symbols.size() - 1);
    }

    public Symbol getLastSymbol() {
        return this.symbols.get(symbols.size() - 1);
    }

}







