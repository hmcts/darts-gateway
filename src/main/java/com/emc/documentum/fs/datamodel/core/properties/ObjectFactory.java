package com.emc.documentum.fs.datamodel.core.properties;

import jakarta.xml.bind.annotation.XmlRegistry;


@XmlRegistry
public class ObjectFactory {


    public ObjectFactory() {
    }

    public PropertySet createPropertySet() {
        return new PropertySet();
    }

    public StringProperty createStringProperty() {
        return new StringProperty();
    }

    public NumberProperty createNumberProperty() {
        return new NumberProperty();
    }

    public DateProperty createDateProperty() {
        return new DateProperty();
    }

    public BooleanProperty createBooleanProperty() {
        return new BooleanProperty();
    }

    public ObjectIdProperty createObjectIdProperty() {
        return new ObjectIdProperty();
    }

    public StringArrayProperty createStringArrayProperty() {
        return new StringArrayProperty();
    }

    public ValueAction createValueAction() {
        return new ValueAction();
    }

    public NumberArrayProperty createNumberArrayProperty() {
        return new NumberArrayProperty();
    }

    public BooleanArrayProperty createBooleanArrayProperty() {
        return new BooleanArrayProperty();
    }

    public DateArrayProperty createDateArrayProperty() {
        return new DateArrayProperty();
    }

    public ObjectIdArrayProperty createObjectIdArrayProperty() {
        return new ObjectIdArrayProperty();
    }

    public RichTextProperty createRichTextProperty() {
        return new RichTextProperty();
    }

}
