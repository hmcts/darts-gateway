package com.emc.documentum.fs.rt;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private static final QName _ServiceException_QNAME = new QName(
        "http://rt.fs.documentum.emc.com/",
        "ServiceException"
    );

    public ObjectFactory() {
    }

    public ServiceException createServiceException() {
        return new ServiceException();
    }

    public DfsExceptionHolder createDfsExceptionHolder() {
        return new DfsExceptionHolder();
    }

    public DfsAttributeHolder createDfsAttributeHolder() {
        return new DfsAttributeHolder();
    }

    public StackTraceHolder createStackTraceHolder() {
        return new StackTraceHolder();
    }

    @XmlElementDecl(namespace = "http://rt.fs.documentum.emc.com/", name = "ServiceException")
    public JAXBElement<ServiceException> createServiceException(ServiceException value) {
        return new JAXBElement<>(_ServiceException_QNAME, ServiceException.class, null, value);
    }

}
