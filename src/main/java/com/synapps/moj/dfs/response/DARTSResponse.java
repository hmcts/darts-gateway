package com.synapps.moj.dfs.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DARTSResponse", propOrder = {
    "code",
    "message"
})
@XmlSeeAlso({
    GetCourtLogResponse.class,
    RegisterNodeResponse.class,
    GetCasesResponse.class
})
public class DARTSResponse {

    protected String code;
    protected String message;

    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        this.code = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

}
