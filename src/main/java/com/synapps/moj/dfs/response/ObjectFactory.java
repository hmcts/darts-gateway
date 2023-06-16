package com.synapps.moj.dfs.response;

import jakarta.xml.bind.annotation.XmlRegistry;


@XmlRegistry
public class ObjectFactory {


    public ObjectFactory() {
    }

    public DARTSResponse createDARTSResponse() {
        return new DARTSResponse();
    }

    public GetCourtLogResponse createGetCourtLogResponse() {
        return new GetCourtLogResponse();
    }

    public CourtLog createCourtLog() {
        return new CourtLog();
    }

    public CourtLogEntry createCourtLogEntry() {
        return new CourtLogEntry();
    }

    public RegisterNodeResponse createRegisterNodeResponse() {
        return new RegisterNodeResponse();
    }

    public GetCasesResponse createGetCasesResponse() {
        return new GetCasesResponse();
    }

    public Cases createCases() {
        return new Cases();
    }

    public Case createCase() {
        return new Case();
    }

    public Defendants createDefendants() {
        return new Defendants();
    }

    public Judges createJudges() {
        return new Judges();
    }

    public Prosecutors createProsecutors() {
        return new Prosecutors();
    }

    public Defenders createDefenders() {
        return new Defenders();
    }

}
