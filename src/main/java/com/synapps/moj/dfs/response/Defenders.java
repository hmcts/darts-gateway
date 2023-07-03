package com.synapps.moj.dfs.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "defenders", propOrder = {
    "defender"
})
@AllArgsConstructor
@NoArgsConstructor
public class Defenders {

    protected List<String> defender;

    public List<String> getDefender() {
        if (defender == null) {
            defender = new ArrayList<>();
        }
        return this.defender;
    }

}
