package com.emc.documentum.fs.datamodel.core.content;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UcfContent", propOrder = {
    "localFilePath",
    "activityInfo"
})
public class UcfContent
    extends Content {

    @XmlElement(name = "LocalFilePath", required = true)
    protected String localFilePath;
    @XmlElement(name = "ActivityInfo")
    protected ActivityInfo activityInfo;

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String value) {
        this.localFilePath = value;
    }

    public ActivityInfo getActivityInfo() {
        return activityInfo;
    }

    public void setActivityInfo(ActivityInfo value) {
        this.activityInfo = value;
    }

}
