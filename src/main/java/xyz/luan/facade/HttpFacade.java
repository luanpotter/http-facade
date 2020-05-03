package xyz.luan.facade;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import static xyz.luan.facade.Util.urlEncodeUTF8;

public class HttpFacade {

    private String method;
    private UrlFacade url;
    private List<Entry<String, String>> headers, formParams;
    private Object body;
    private String encoding = "UTF-8";

    private Integer timeout = 3 * 60 * 1000;
    private boolean isGzip = false;
    private boolean followRedirects = false;
    private boolean fixedSize = false;
    private boolean storeContent = true;
    private boolean disableSecurity = false;

    private List<InputStream> customKeystoreCertificates = new ArrayList<>();

    static {
        try {
            String[] methods = { "CONNECT", "TRACE", "PATCH" };

            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

            methodsField.setAccessible(true);

            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(methods));
            String[] newMethods = methodsSet.toArray(new String[0]);

            methodsField.set(null, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println("Additional verbs could not be loaded: " + e);
        }
    }

    public HttpFacade(String baseUrl) throws MalformedURLException {
        this.url = new UrlFacade(baseUrl);
        this.headers = new ArrayList<>();
        this.formParams = new ArrayList<>();
    }

    public HttpFacade timeout(int ms) {
        this.timeout = ms;
        return this;
    }

    public HttpFacade noTimeout() {
        this.timeout = null;
        return this;
    }

    public HttpFacade followRedirects() {
        this.followRedirects = true;
        return this;
    }

    public HttpFacade cookies(Map<String, String> cookies) {
        StringBuilder str = new StringBuilder();
        for (Entry<String, String> c : cookies.entrySet()) {
            str.append(c.getKey()).append("=").append(c.getValue()).append("; ");
        }
        header("Cookie", str.toString());
        return this;
    }

    public HttpFacade encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public HttpFacade header(String k, String v) {
        this.headers.add(new SimpleEntry<>(k, v));
        return this;
    }

    public HttpFacade gzip(String acceptContent) {
        isGzip = true;
        return header("Accept-Encoding", acceptContent);
    }

    public HttpFacade query(String k, String v) {
        this.url.getQueryParams().add(new SimpleEntry<>(k, v));
        return this;
    }

    public HttpFacade body(Object body) {
        this.body = body;
        return this;
    }

    public HttpFacade withFixedSize() {
        this.fixedSize = true;
        return this;
    }

    public HttpFacade user(String user, String pass) {
        String token = url.user(user, pass);
        header("Authentication", "Basic " + token);
        return this;
    }

    public HttpFacade noStoredContent() {
        this.storeContent = false;
        return this;
    }

    public HttpFacade storedContent() {
        this.storeContent = true;
        return this;
    }

    private void applyDisableSecurity(HttpsURLConnection conn) {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }

        } };

        SSLContext sc = getSSLContext();
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            throw new RuntimeException("Should never happen!", e);
        }

        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        conn.setHostnameVerifier(allHostsValid);
        conn.setSSLSocketFactory(sc.getSocketFactory());
    }

    private void addCustomKeystore(HttpsURLConnection conn) {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            int i = 0;
            for (InputStream is : customKeystoreCertificates) {
                Certificate certificate = cf.generateCertificate(is);
                keystore.setCertificateEntry("custom-certificate-" + (i++), certificate);
            }

            trustManagerFactory.init(keystore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, null);

            conn.setSSLSocketFactory(sslContext.getSocketFactory());
        } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException
                | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpFacade addCustomCertificate(InputStream cert) {
        this.customKeystoreCertificates.add(cert);
        return this;
    }

    private SSLContext getSSLContext() {
        try {
            return SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Don't have SSL?", e);
        }
    }

    public HttpURLConnection generateConnection() throws IOException {
        URL obj = new URL(getUrl());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setInstanceFollowRedirects(followRedirects);
        con.setRequestMethod(method);
        if (timeout != null) {
            con.setConnectTimeout(timeout);
        }
        if (fixedSize) {
            con.setFixedLengthStreamingMode(body.toString().length());
        }
        if (con instanceof HttpsURLConnection) {
            HttpsURLConnection httpsCon = (HttpsURLConnection) con;
            if (disableSecurity) {
                applyDisableSecurity(httpsCon);
            } else if (!customKeystoreCertificates.isEmpty()) {
                addCustomKeystore(httpsCon);
            }
        }
        setHeaders(con);
        setBody(con);
        return con;
    }

    public String getUrl() {
        return url.buildUrl();
    }

    private void setHeaders(HttpURLConnection con) {
        for (Entry<String, String> k : headers) {
            con.setRequestProperty(k.getKey(), k.getValue());
        }
    }

    private void setBody(HttpURLConnection con) throws IOException {
        if (!formParams.isEmpty() && body != null) {
            throw new RuntimeException("You can only specify body or form params, not both!");
        }
        String str = generateBody();
        if (str != null) {
            con.setDoOutput(true);
            con.addRequestProperty("Content-Length", str.length() + "");
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream(), encoding);
            wr.write(str);
            wr.flush();
            wr.close();
        }
    }

    private String generateBody() {
        if (body != null) {
            return body.toString();
        }
        if (!formParams.isEmpty()) {
            return urlEncodeUTF8(formParams);
        }
        return null;
    }

    public Response get() throws IOException {
        return method("GET").req();
    }

    public Response post() throws IOException {
        return method("POST").req();
    }

    public Response put() throws IOException {
        return method("PUT").req();
    }

    public Response delete() throws IOException {
        return method("DELETE").req();
    }

    public Response connect() throws IOException {
        return method("CONNECT").req();
    }

    public Response trace() throws IOException {
        return method("TRACE").req();
    }

    public Response patch() throws IOException {
        return method("PATCH").req();
    }

    public HttpFacade method(String method) {
        this.method = method;
        return this;
    }

    public Response req() throws IOException {
        return new Response(generateConnection(), isGzip, this.storeContent);
    }

    public String header(String name) {
        for (Entry<String, String> header : this.headers) {
            if (header.getKey().equals(name)) {
                return header.getValue();
            }
        }
        return null;
    }

    public HttpFacade formParam(String key, String val) {
        formParams.add(new SimpleEntry<>(key, val));
        header("Content-Type", "application/x-www-form-urlencoded");
        return this;
    }

    /*
     * This disables SSL certificate validation. Beware! Just use this if you know
     * what you are doing! It will make you susceptible to man-in-the-middle
     * attacks, and possibly other severe security concerns.
     *
     * I'm deprecating it for now to discourage usage, but it won't be removed.
     */
    @Deprecated
    public HttpFacade disableSecuritySSLCertificateValidation() {
        this.disableSecurity = true;
        return this;
    }
}
