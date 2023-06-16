package com.emc.documentum.fs.datamodel.core.content;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActivityInfo")
public class ActivityInfo {

    @XmlAttribute(name = "sessionId", required = true)
    protected String sessionId;
    @XmlAttribute(name = "initiatorSessionId")
    protected String initiatorSessionId;
    @XmlAttribute(name = "activityId", required = true)
    protected String activityId;
    @XmlAttribute(name = "autoCloseConnection", required = true)
    protected boolean autoCloseConnection;
    @XmlAttribute(name = "closed", required = true)
    protected boolean closed;
    @XmlAttribute(name = "targetDeploymentId")
    protected String targetDeploymentId;
    @XmlAttribute(name = "initiatorDeploymentId")
    protected String initiatorDeploymentId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String value) {
        this.sessionId = value;
    }

    public String getInitiatorSessionId() {
        return initiatorSessionId;
    }

    public void setInitiatorSessionId(String value) {
        this.initiatorSessionId = value;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String value) {
        this.activityId = value;
    }

    public boolean isAutoCloseConnection() {
        return autoCloseConnection;
    }

    public void setAutoCloseConnection(boolean value) {
        this.autoCloseConnection = value;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean value) {
        this.closed = value;
    }

    public String getTargetDeploymentId() {
        return targetDeploymentId;
    }

    public void setTargetDeploymentId(String value) {
        this.targetDeploymentId = value;
    }

    public String getInitiatorDeploymentId() {
        return initiatorDeploymentId;
    }

    public void setInitiatorDeploymentId(String value) {
        this.initiatorDeploymentId = value;
    }

}
