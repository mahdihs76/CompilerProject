package semantic_analysis;

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
        private int codeStartAddress;
        private int codeEndAdress;
        private int returnLineMemAddress;
        private int beforeCodeStartAddress;
        private int beforeCodeEndAddress;

        private VarType returnType;
        private int callerAddress;
        private ArrayList<Integer> callingAddresses;
        private ArrayList<Param> params;


        public Procedure(String name) {
            this.name = name;
            this.returnLineMemAddress = -1;
            this.returnType = VarType.VOID;
            this.callingAddresses = new ArrayList<Integer>();
            this.params = new ArrayList<Param>();
        }

        public String getParamNameByIndex(int index) {
            return params.get(index).getName();
        }
        public VarType getParamTypeByIndex(int index) {
            return params.get(index).getVarType();
        }

        public void setReturnType(VarType returnType) {
            this.returnType = returnType;
        }
        public void addParam(String parName, VarType parType) {
            params.add(new Param(parName, parType));
        }

        public void setCodeStartAddress(int codeStartAddress) {
             this.codeStartAddress = codeStartAddress;
        }
        public void setCodeEndAdress(int codeEndAdress) {
            this.codeEndAdress = codeEndAdress;
        }

        public int getCodeEndAddress() {
            return codeEndAdress;
        }

        public int getReturnLineMemAddress() {
            return returnLineMemAddress;
        }
        public void setReturnLineMemAddress(int returnLineMemAddress) {
            this.returnLineMemAddress = returnLineMemAddress;
        }

        public int getBeforeCodeEndAddress() {
            return beforeCodeEndAddress;
        }
        public void setBeforeCodeEndAddress(int beforeCodeEndAddress) {
            this.beforeCodeEndAddress = beforeCodeEndAddress;
        }

        public int getBeforeCodeStartAddress() {
            return beforeCodeStartAddress;
        }
        public void setBeforeCodeStartAddress(int beforeCodeStartAddress) {
            this.beforeCodeStartAddress = beforeCodeStartAddress;
        }

        public String getName() { return this.name; }
        public int getCodeStartAddress() { return this.codeStartAddress; }

        public int getParamsCount() {
            if (params == null || params.isEmpty()) return 0;
            return params.size();
        }

    }


    private ArrayList<Procedure> procs;

    public ProcTable() {
        this.procs = new ArrayList<Procedure>();
    }





    public void setCodeStartAdress(String procName, int codeStartAdress) {
        for(Procedure p: procs) {
            if (p.getName().equals(procName)) {
                p.setCodeStartAddress(codeStartAdress);
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

    public int getProcIndexByName(String procName) {
        for(Procedure p: procs) {
            if (p.getName().equals(procName)) {
                return procs.indexOf(p);
            }
        }
        return -1;
    }

    public Procedure getProcByIndex(int procIndex) {
        return procs.get(procIndex);
    }



}
