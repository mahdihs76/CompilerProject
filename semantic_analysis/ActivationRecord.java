package semantic_analysis;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivationRecord {

    private String name;

    private ArrayList<LocalVariable> localVars;

    private ActivationRecord controlLink;
    private Integer returnValue;
    private ArrayList<VarType> parameters;

    private ArrayList<String> localProcedures;

    public ActivationRecord(String name) {
        this.name = name;

        this.localVars = new ArrayList<LocalVariable>();
        this.localProcedures = new ArrayList<String>();

        this.controlLink = null;
        this.returnValue = null;
        this.parameters = new ArrayList<VarType>();
    }





    public void addLocalVar(VarType varType, int address, int size) {
        LocalVariable localVariable = new LocalVariable(varType, address, size);
    }
    public void addLocalVar(VarType varType, int address) {
        LocalVariable localVariable = new LocalVariable(varType, address, 1);
    }

    public void addParameter(String name, VarType varType) {
        LocalVariable localVariable = new LocalVariable(name, varType);
    }

    public void addLocalProcedure(String procName) {
        this.localProcedures.add(procName);
    }


    public LocalVariable searchLocalVariable(int address) {
        for(LocalVariable v: this.localVars){
            if(v.address == address)
                return v;
        }
        return null;
    }

    public boolean searchLocalVariable(String procName) {
        for(String localProcName: this.localProcedures){
            if(localProcName.equals(procName))
                return true;
        }
        return false;
    }




    public class LocalVariable {
        private VarType varType;
        private int address;
        private int size;
        private String name;

        public LocalVariable(VarType varType, int address, int size) {
            this.varType = varType;
            this.address = address;
            this.size = size;
        }

        public LocalVariable(String name, VarType varType) {
            this.name = name;
            this.varType = varType;
        }


        public int getSize() { return this.size; }
        public VarType getVarType() { return this.varType; }
        public String getName() { return this.name; }

    }








}
