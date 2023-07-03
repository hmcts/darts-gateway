package com.synapps.moj.dfs.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "judges", propOrder = {
    "judge"
})
@AllArgsConstructor
@NoArgsConstructor
public class Judges {

    protected List<String> judge;

    public List<String> getJudge() {
        if (judge == null) {
            judge = new ArrayList<>();
        }
        return this.judge;
    }

}
