package uk.gov.hmcts.darts.common.exceptions.soap.documentum;

import com.emc.documentum.fs.rt.DfsAttributeHolder;
import com.emc.documentum.fs.rt.DfsExceptionHolder;
import com.emc.documentum.fs.rt.ServiceException;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Setter;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XmlRootElement
@Setter
public class ServiceExceptionType extends ServiceException {
    public static final String ATTRIBUTE_EXCEPTION_TYPE = "exceptionType";
    public static final String ATTRIBUTE_MESSAGE_ARGS = "messageArgs";
    public static final String ATTRIBUTE_MESSAGE_ID = "messageId";

    private static final String DOCUMENTUM_SERVICE_CONTEXT_EXCEPTION_NAME = "com.emc.documentum.fs.rt.ServiceContextLookupException";

    public ServiceExceptionType() {
    }

    public ServiceExceptionType(String code, Throwable cause, String arg) {
        setMessageId(code);
        setMessage(SoapFaultServiceException.getMessage(code, arg));

        if (cause != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            cause.printStackTrace(pw);
            String stackTrace = sw.toString(); // stack trace as a string
            setStackTraceAsString(stackTrace);
        } else {
            setStackTraceAsString("");
        }

        DfsAttributeHolder exceptionTypeAttribute = new DfsAttributeHolder();
        exceptionTypeAttribute.setName(ATTRIBUTE_EXCEPTION_TYPE);
        exceptionTypeAttribute.setType(String.class.getName());

        DfsAttributeHolder messageArgsAttribute = new DfsAttributeHolder();
        messageArgsAttribute.setName(ATTRIBUTE_MESSAGE_ARGS);
        messageArgsAttribute.setType(String.class.getName());
        messageArgsAttribute.setValue(arg);

        DfsAttributeHolder messageIdAttribute = new DfsAttributeHolder();
        messageIdAttribute.setName(ATTRIBUTE_MESSAGE_ID);
        messageIdAttribute.setType(getMessage().getClass().getCanonicalName());
        messageIdAttribute.setValue(getMessageId());

        // add one exception
        DfsExceptionHolder holder = new DfsExceptionHolder();
        holder.setExceptionClass(DOCUMENTUM_SERVICE_CONTEXT_EXCEPTION_NAME);
        holder.setGenericType(Exception.class.getCanonicalName());
        holder.setMessage(getMessage());
        holder.setMessageId(getMessageId());

        holder.getAttribute().add(exceptionTypeAttribute);
        holder.getAttribute().add(messageArgsAttribute);
        holder.getAttribute().add(messageIdAttribute);

        List<DfsExceptionHolder> exceptionHolders = new ArrayList<>();
        exceptionHolders.add(holder);
        setExceptionBean(exceptionHolders);
    }

    public void setMessageArgs(Object... args) {
        this.messageArgs = Arrays.asList(args);
    }

    public void setExceptionBean(List<DfsExceptionHolder> exceptionBean) {
        this.exceptionBean = exceptionBean;
    }
}
