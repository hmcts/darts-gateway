package uk.gov.hmcts.darts.testutils.request;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import uk.gov.hmcts.darts.common.function.ConsumerWithIoException;
import uk.gov.hmcts.darts.common.multipart.SizeableInputSource;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({"PMD.ExcessivePublicCount", "PMD.CyclomaticComplexity", "PMD.TooManyMethods"})
public class XmlWithFileMultiPartRequestGuard implements XmlWithFileMultiPartRequest {

    @Override
    public boolean consumeFileBinary(ConsumerWithIoException<java.io.File> file)  throws IOException {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public boolean consumeFileBinaryStream(ConsumerWithIoException<SizeableInputSource> fileInputStream) throws IOException {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public void consumeXmlBody(ConsumerWithIoException<InputStream> fileInputStream) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public long getBinarySize() throws IOException {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getAuthType() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public Cookie[] getCookies() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public long getDateHeader(String name) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getHeader(String name) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public int getIntHeader(String name) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getMethod() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getPathInfo() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getContextPath() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getQueryString() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getRequestURI() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getServletPath() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public HttpSession getSession(boolean create) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public void login(String username, String password) throws ServletException {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass) throws IOException, ServletException {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public Object getAttribute(String name) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public int getContentLength() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public long getContentLengthLong() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getParameter(String name) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public Enumeration<String> getParameterNames() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String[] getParameterValues(String name) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getScheme() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getServerName() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public int getServerPort() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getRemoteAddr() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getRemoteHost() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public void setAttribute(String name, Object object) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public void removeAttribute(String name) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public int getRemotePort() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getLocalName() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getLocalAddr() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public int getLocalPort() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public AsyncContext startAsync()  {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getRequestId() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public String getProtocolRequestId() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public ServletConnection getServletConnection() {
        throw new UnsupportedOperationException("Override to use");
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("Override to use");
    }
}
