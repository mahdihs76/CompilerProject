package semantic_analysis;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivationRecord {

    private String name;

    private ArrayList<LocalVariable> localVars;

    private ActivationRecord controlLink;
    private ArrayList<ActivationRecord> childs;

    private Integer returnValue;
    private int returnAddress;

    private ArrayList<VarType> parameters;

    private ArrayList<String> localProcedures;

    public ActivationRecord(String name) {
        this.name = name;

        this.localVars = new ArrayList<>();
        this.localProcedures = new ArrayList<>();
        this.childs = new ArrayList<>();
        this.controlLink = null;
        this.returnValue = null;
        this.returnAddress = -1;
        this.parameters = new ArrayList<>();
    }

    public void addLocalVar(VarType varType, int address) {
        localVars.add(new LocalVariable(varType, address));
    }

    public void addParameter(String name, VarType varType) {
        LocalVariable localVariable = new LocalVariable(name, varType);
    }

    public void addLocalProcedure(String procName) {
        this.localProcedures.add(procName);
    }


    public LocalVariable searchLocalVariable(int address, String idName) {
        for(LocalVariable v: this.localVars){
            if(v.address == address)
                return v;
        }
        return null;
    }

    public boolean searchLocalProc(String name) {
        for(String procName: this.localProcedures){
            if(procName.equals(name))
                return true;
        }
        return false;
    }

    public boolean searchLocalVariable(String procName) {
        for(String localProcName: this.localProcedures){
            if(localProcName.equals(procName))
                return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setControlLink(ActivationRecord controlLink) {
        controlLink.childs.add(this);
        this.controlLink = controlLink;
    }

    public ActivationRecord findAccessibleChild(String name) {
        for (ActivationRecord activationRecord : childs) {
            if (activationRecord.getName().equals(name)) return activationRecord;
        }
        if (controlLink == null) return null;
        return controlLink.findAccessibleChild(name);
    }

    public ActivationRecord getControlLink() {
        return controlLink;
    }

    public int getReturnAddress() {
        return this.returnAddress;
    }

    public void setReturnAddress(int returnAddress) {
        this.returnAddress = returnAddress;
    }

    public void setReturnValue(Integer returnValue) {
        this.returnValue = returnValue;
    }

    public Integer getReturnValue() {
        return returnValue;
    }



    public class LocalVariable {
        private VarType varType;
        private int address;
        private String name;

        public LocalVariable(VarType varType, int address) {
            this.varType = varType;
            this.address = address;
        }

        public LocalVariable(String name, VarType varType) {
            this.name = name;
            this.varType = varType;
        }

        public int getSize() { return this.varType.getSize(); }
        public VarType getVarType() { return this.varType; }
        public String getName() { return this.name; }

    }








}
