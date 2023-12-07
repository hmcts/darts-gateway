package uk.gov.hmcts.darts.common.multipart;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

public final class MultIPartUtil {

    private MultIPartUtil() {

    }

    public static BodyPart getXml(MimeMultipart mimeMultipart) throws MessagingException {
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            String[] headers = mimeMultipart.getBodyPart(i).getHeader("Content-Type");
            if (headers.length > 0 && headers[0].contains("application/xop+xml")) {
                return mimeMultipart.getBodyPart(i);
            }
        }

        return null;
    }

    public static BodyPart getBinary(MimeMultipart mimeMultipart) throws MessagingException {
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            String[] headers = mimeMultipart.getBodyPart(i).getHeader("Content-Type");
            if (headers.length > 0 && headers[0].contains("application/octet-stream")) {
                return mimeMultipart.getBodyPart(i);
            }
        }

        return null;
    }
}
