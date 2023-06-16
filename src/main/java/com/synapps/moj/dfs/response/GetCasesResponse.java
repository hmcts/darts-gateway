package com.synapps.moj.dfs.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCasesResponse", propOrder = {
    "cases"
})
public class GetCasesResponse
    extends DARTSResponse {

    protected Cases cases;

    public Cases getCases() {
        return cases;
    }

    public void setCases(Cases value) {
        this.cases = value;
    }

}
