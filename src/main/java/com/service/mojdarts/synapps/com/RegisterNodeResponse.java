package com.service.mojdarts.synapps.com;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registerNodeResponse", propOrder = {
    "_return"
})
public class RegisterNodeResponse {

    @XmlElement(name = "return")
    protected com.synapps.moj.dfs.response.RegisterNodeResponse _return;

    public com.synapps.moj.dfs.response.RegisterNodeResponse getReturn() {
        return _return;
    }

    public void setReturn(com.synapps.moj.dfs.response.RegisterNodeResponse value) {
        this._return = value;
    }

}
