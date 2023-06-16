package com.service.mojdarts.synapps.com;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCourtLogResponse", propOrder = {
    "_return"
})
public class GetCourtLogResponse {

    @XmlElement(name = "return")
    protected com.synapps.moj.dfs.response.GetCourtLogResponse _return;

    public com.synapps.moj.dfs.response.GetCourtLogResponse getReturn() {
        return _return;
    }

    public void setReturn(com.synapps.moj.dfs.response.GetCourtLogResponse value) {
        this._return = value;
    }

}
