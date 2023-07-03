package com.synapps.moj.dfs.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "defendants", propOrder = {
    "defendant"
})
@AllArgsConstructor
@NoArgsConstructor
public class Defendants {

    protected List<String> defendant;

    public List<String> getDefendant() {
        if (defendant == null) {
            defendant = new ArrayList<>();
        }
        return this.defendant;
    }

}
