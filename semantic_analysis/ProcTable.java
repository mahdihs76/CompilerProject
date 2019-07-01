package semantic_analysis;

import java.lang.reflect.Parameter;
import java.util.ArrayList;

public class ProcTable {

    public class Procedure {

        public class Param {
            private String name;
            private VarType varType;

            public Param(String name, VarType varType) {
                this.name = name;
                this.varType = varType;
            }

            public String getName() {
                return name;
            }
            public VarType getVarType() {
                return varType;
            }
        }

        private String name;
        private int codeStartAdress;
        private VarType returnType;
        private int callerAddress;
        private int codeEndAdress;
        private ArrayList<Integer> callingAddresses;
        private ArrayList<Param> params;


        public Procedure(String name) {
            this.name = name;
            this.returnType = VarType.VOID;
            this.callingAddresses = new ArrayList<Integer>();
            this.params = new ArrayList<Param>();
        }

        public void setReturnType(VarType returnType) {
            this.returnType = returnType;
        }
        public void addParam(String parName, VarType parType) {
            params.add(new Param(parName, parType));
        }

        public void setCodeStartAdress(int codeStartAdress) {
             this.codeStartAdress = codeStartAdress;
        }
        public void setCodeEndAdress(int codeEndAdress) {
            this.codeEndAdress = codeEndAdress;
        }

        public String getName() { return this.name; }
        public int getCodeStartAdress() { return this.codeStartAdress; }

    }


    private ArrayList<Procedure> procs;

    public ProcTable() {
        this.procs = new ArrayList<Procedure>();
    }





    public void setCodeStartAdress(String procName, int codeStartAdress) {
        for(Procedure p: procs) {
            if (p.getName().equals(procName)) {
                p.setCodeStartAdress(codeStartAdress);
                break;
            }
        }
    }

    public int getSize() { return this.procs.size(); }

    public boolean doesProcExist(String procName) {
        for(Procedure proc: this.procs) {
            if(proc.name.equals(procName))
                return true;
        }

        return false;
    }

    public void addProc(String name) {
        Procedure newProc = new Procedure(name);
        this.procs.add(newProc);
    }

    public Procedure getProcByName(String procName) {
        for(Procedure p: procs) {
            if (p.getName().equals(procName)) {
                return p;
            }
        }
        return null;
    }

    public Procedure getProcByIndex(int procIndex) {
        return procs.get(procIndex);
    }



}
