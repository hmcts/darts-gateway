package com.service.mojdarts.synapps.com.addcase;

import com.synapps.moj.dfs.response.DARTSResponse;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addCaseResponse", propOrder = {
    "_return"
})
@XmlRootElement(name = "addCaseResponse")
public class AddCaseResponse {

    @XmlElement(name = "return")
    protected DARTSResponse  _return;

    public DARTSResponse getReturn() {
        return _return;
    }

    public void setReturn(DARTSResponse value) {
        this._return = value;
    }

}
