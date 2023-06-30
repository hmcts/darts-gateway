package com.service.mojdarts.synapps.com;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCasesResponse", propOrder = {
    "_return"
})
@XmlRootElement(name = "getCasesResponse")
public class GetCasesResponse {

    @XmlElement(name = "return")
    protected com.synapps.moj.dfs.response.GetCasesResponse _return;

    public com.synapps.moj.dfs.response.GetCasesResponse getReturn() {
        return _return;
    }

    public void setReturn(com.synapps.moj.dfs.response.GetCasesResponse value) {
        this._return = value;
    }

}
