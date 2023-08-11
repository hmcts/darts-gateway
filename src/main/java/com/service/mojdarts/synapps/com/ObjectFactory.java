package com.service.mojdarts.synapps.com;

import com.service.mojdarts.synapps.com.addcase.NewDataSet;
import com.synapps.moj.dfs.response.DARTSResponse;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private static final QName _Exception_QNAME = new QName("http://com.synapps.mojdarts.service.com", "Exception");
    private static final QName _AddAudio_QNAME = new QName("http://com.synapps.mojdarts.service.com", "addAudio");
    private static final QName _AddAudioResponse_QNAME = new QName(
        "http://com.synapps.mojdarts.service.com",
        "addAudioResponse"
    );
    private static final QName _AddCase_QNAME = new QName("http://com.synapps.mojdarts.service.com", "addCase");
    private static final QName _AddCaseResponse_QNAME = new QName(
        "http://com.synapps.mojdarts.service.com",
        "addCaseResponse"
    );
    private static final QName _AddDocument_QNAME = new QName("http://com.synapps.mojdarts.service.com", "addDocument");
    private static final QName _AddDocumentResponse_QNAME = new QName(
        "http://com.synapps.mojdarts.service.com",
        "addDocumentResponse"
    );
    private static final QName _AddLogEntry_QNAME = new QName("http://com.synapps.mojdarts.service.com", "addLogEntry");
    private static final QName _AddLogEntryResponse_QNAME = new QName(
        "http://com.synapps.mojdarts.service.com",
        "addLogEntryResponse"
    );
    private static final QName _GetCases_QNAME = new QName("http://com.synapps.mojdarts.service.com", "getCases");
    private static final QName _GetCasesResponse_QNAME = new QName(
        "http://com.synapps.mojdarts.service.com",
        "getCasesResponse"
    );
    private static final QName _GetCourtLog_QNAME = new QName("http://com.synapps.mojdarts.service.com", "getCourtLog");
    private static final QName _GetCourtLogResponse_QNAME = new QName(
        "http://com.synapps.mojdarts.service.com",
        "getCourtLogResponse"
    );
    private static final QName _RegisterNode_QNAME = new QName(
        "http://com.synapps.mojdarts.service.com",
        "registerNode"
    );
    private static final QName _RegisterNodeResponse_QNAME = new QName(
        "http://com.synapps.mojdarts.service.com",
        "registerNodeResponse"
    );
    private static final QName _RequestTranscription_QNAME = new QName(
        "http://com.synapps.mojdarts.service.com",
        "requestTranscription"
    );
    private static final QName _RequestTranscriptionResponse_QNAME = new QName(
        "http://com.synapps.mojdarts.service.com",
        "requestTranscriptionResponse"
    );
    private static final QName _Response_QNAME = new QName("http://com.synapps.mojdarts.service.com", "response");

    public ObjectFactory() {
    }

    public Exception createException() {
        return new Exception();
    }

    public AddAudio createAddAudio() {
        return new AddAudio();
    }

    public AddAudioResponse createAddAudioResponse() {
        return new AddAudioResponse();
    }

    public NewDataSet createAddCase() {
        return new NewDataSet();
    }

    public com.service.mojdarts.synapps.com.AddCaseResponse createAddCaseResponse() {
        return new com.service.mojdarts.synapps.com.AddCaseResponse();
    }

    public AddDocument createAddDocument() {
        return new AddDocument();
    }

    public AddDocumentResponse createAddDocumentResponse() {
        return new AddDocumentResponse();
    }

    public AddLogEntry createAddLogEntry() {
        return new AddLogEntry();
    }

    public AddLogEntryResponse createAddLogEntryResponse() {
        return new AddLogEntryResponse();
    }

    public GetCases createGetCases() {
        return new GetCases();
    }

    public GetCasesResponse createGetCasesResponse() {
        return new GetCasesResponse();
    }

    public GetCourtLog createGetCourtLog() {
        return new GetCourtLog();
    }

    public GetCourtLogResponse createGetCourtLogResponse() {
        return new GetCourtLogResponse();
    }

    public RegisterNode createRegisterNode() {
        return new RegisterNode();
    }

    public RegisterNodeResponse createRegisterNodeResponse() {
        return new RegisterNodeResponse();
    }

    public RequestTranscription createRequestTranscription() {
        return new RequestTranscription();
    }

    public RequestTranscriptionResponse createRequestTranscriptionResponse() {
        return new RequestTranscriptionResponse();
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<>(_Exception_QNAME, Exception.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "addAudio")
    public JAXBElement<AddAudio> createAddAudio(AddAudio value) {
        return new JAXBElement<>(_AddAudio_QNAME, AddAudio.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "addAudioResponse")
    public JAXBElement<AddAudioResponse> createAddAudioResponse(AddAudioResponse value) {
        return new JAXBElement<>(_AddAudioResponse_QNAME, AddAudioResponse.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "addCase")
    public JAXBElement<NewDataSet> createAddCase(NewDataSet value) {
        return new JAXBElement<>(_AddCase_QNAME, NewDataSet.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "addCaseResponse")
    public JAXBElement<com.service.mojdarts.synapps.com.AddCaseResponse> createAddCaseResponse(
        com.service.mojdarts.synapps.com.AddCaseResponse value) {
        return new JAXBElement<>(_AddCaseResponse_QNAME, com.service.mojdarts.synapps.com.AddCaseResponse.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "addDocument")
    public JAXBElement<AddDocument> createAddDocument(AddDocument value) {
        return new JAXBElement<>(_AddDocument_QNAME, AddDocument.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "addDocumentResponse")
    public JAXBElement<AddDocumentResponse> createAddDocumentResponse(AddDocumentResponse value) {
        return new JAXBElement<>(_AddDocumentResponse_QNAME, AddDocumentResponse.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "addLogEntry")
    public JAXBElement<AddLogEntry> createAddLogEntry(AddLogEntry value) {
        return new JAXBElement<>(_AddLogEntry_QNAME, AddLogEntry.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "addLogEntryResponse")
    public JAXBElement<AddLogEntryResponse> createAddLogEntryResponse(AddLogEntryResponse value) {
        return new JAXBElement<>(_AddLogEntryResponse_QNAME, AddLogEntryResponse.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "getCases")
    public JAXBElement<GetCases> createGetCases(GetCases value) {
        return new JAXBElement<>(_GetCases_QNAME, GetCases.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "getCasesResponse")
    public JAXBElement<GetCasesResponse> createGetCasesResponse(GetCasesResponse value) {
        return new JAXBElement<>(_GetCasesResponse_QNAME, GetCasesResponse.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "getCourtLog")
    public JAXBElement<GetCourtLog> createGetCourtLog(GetCourtLog value) {
        return new JAXBElement<>(_GetCourtLog_QNAME, GetCourtLog.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "getCourtLogResponse")
    public JAXBElement<GetCourtLogResponse> createGetCourtLogResponse(GetCourtLogResponse value) {
        return new JAXBElement<>(_GetCourtLogResponse_QNAME, GetCourtLogResponse.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "registerNode")
    public JAXBElement<RegisterNode> createRegisterNode(RegisterNode value) {
        return new JAXBElement<>(_RegisterNode_QNAME, RegisterNode.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "registerNodeResponse")
    public JAXBElement<RegisterNodeResponse> createRegisterNodeResponse(RegisterNodeResponse value) {
        return new JAXBElement<>(_RegisterNodeResponse_QNAME, RegisterNodeResponse.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "requestTranscription")
    public JAXBElement<RequestTranscription> createRequestTranscription(RequestTranscription value) {
        return new JAXBElement<>(_RequestTranscription_QNAME, RequestTranscription.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "requestTranscriptionResponse")
    public JAXBElement<RequestTranscriptionResponse> createRequestTranscriptionResponse(
        RequestTranscriptionResponse value) {
        return new JAXBElement<>(_RequestTranscriptionResponse_QNAME, RequestTranscriptionResponse.class, null, value);
    }

    @XmlElementDecl(namespace = "http://com.synapps.mojdarts.service.com", name = "response")
    public JAXBElement<DARTSResponse> createResponse(DARTSResponse value) {
        return new JAXBElement<>(_Response_QNAME, DARTSResponse.class, null, value);
    }

}
