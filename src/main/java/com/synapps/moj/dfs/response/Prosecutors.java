package com.synapps.moj.dfs.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "prosecutors", propOrder = {
    "prosecutor"
})
public class Prosecutors {

    protected List<String> prosecutor;

    public List<String> getProsecutor() {
        if (prosecutor == null) {
            prosecutor = new ArrayList<>();
        }
        return this.prosecutor;
    }

}
