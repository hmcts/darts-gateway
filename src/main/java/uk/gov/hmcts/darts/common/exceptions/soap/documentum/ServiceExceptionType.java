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

    @SuppressWarnings("PMD.CallSuperInConstructor")
    public ServiceExceptionType() {
        //Empty constructor
    }

    @SuppressWarnings({"PMD.ConstructorCallsOverridableMethod", "PMD.CallSuperInConstructor"})
    public ServiceExceptionType(String code, Throwable cause, String arg) {
        setMessageId(code);
        setMessage(SoapFaultServiceException.getMessage(code, arg));
        setMessageArgs(arg);

        if (cause != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            cause.printStackTrace(pw);
            String stackTrace = sw.toString(); // stack trace as a string
            setStackTraceAsString(stackTrace);
        } else {
            setStackTraceAsString("");
        }

        List<DfsExceptionHolder> exceptionHolders = new ArrayList<>();
        setExceptionBean(exceptionHolders);

        DfsExceptionHolder holder = addHolder(getMessage(), getMessageId(), cause, arg);
        getExceptionBean().add(holder);
    }

    public DfsExceptionHolder addHolder(String message, String messageId, Throwable cause, String arg) {
        DfsAttributeHolder exceptionTypeAttribute = new DfsAttributeHolder();
        exceptionTypeAttribute.setName(ATTRIBUTE_EXCEPTION_TYPE);
        exceptionTypeAttribute.setType(String.class.getName());

        DfsAttributeHolder messageArgsAttribute = new DfsAttributeHolder();
        messageArgsAttribute.setName(ATTRIBUTE_MESSAGE_ARGS);
        messageArgsAttribute.setType(String.class.getName());
        messageArgsAttribute.setValue(arg);

        DfsAttributeHolder messageIdAttribute = new DfsAttributeHolder();
        messageIdAttribute.setName(ATTRIBUTE_MESSAGE_ID);
        messageIdAttribute.setType(message.getClass().getCanonicalName());
        messageIdAttribute.setValue(messageId);

        // add one exception
        DfsExceptionHolder holder = new DfsExceptionHolder();
        holder.setExceptionClass(cause.getClass().getCanonicalName());
        holder.setGenericType(Exception.class.getCanonicalName());
        holder.setMessage(message);
        holder.setMessageId(messageId);

        holder.getAttribute().add(exceptionTypeAttribute);
        holder.getAttribute().add(messageArgsAttribute);
        holder.getAttribute().add(messageIdAttribute);

        return holder;
    }

    public void addHolderCause(String message, String messageId, Throwable cause, String arg) {
        DfsExceptionHolder holder = addHolder(message, messageId, cause, arg);
        getExceptionBean().addFirst(holder);
    }

    public void setMessageArgs(Object... args) {
        this.messageArgs = Arrays.asList(args);
    }

    public void setExceptionBean(List<DfsExceptionHolder> exceptionBean) {
        this.exceptionBean = exceptionBean;
    }
}
