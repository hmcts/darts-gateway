package com.emc.documentum.fs.rt;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StackTraceHolder", propOrder = {
    "className",
    "fileName",
    "lineNumber",
    "methodName"
})
public class StackTraceHolder {

    protected String className;
    protected String fileName;
    protected int lineNumber;
    protected String methodName;

    public String getClassName() {
        return className;
    }

    public void setClassName(String value) {
        this.className = value;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String value) {
        this.fileName = value;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int value) {
        this.lineNumber = value;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String value) {
        this.methodName = value;
    }

}
