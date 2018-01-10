package com.scalefocus.shutterfly.aws.idgeneration.services.util;


import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;

public class GatewaySslSocketFactory extends SSLSocketFactory {

    GatewaySslSocketFactory(SSLContext sslContext, X509HostnameVerifier hostnameVerifier) {
        super(sslContext, hostnameVerifier);
    }

    @Override
    public Socket createSocket(HttpParams params) throws IOException {
        SSLSocket sslSocket = (SSLSocket) super.createSocket(params);

        // Set the encryption protocol
        sslSocket.setEnabledProtocols(new String[]{"TLSv1.2"});

        // Configure SNI
        SNIHostName serverName = new SNIHostName("aavztyxxof.execute-api.eu-west-1.amazonaws.com");
        SSLParameters sslParams = sslSocket.getSSLParameters();
        sslParams.setServerNames(Collections.<SNIServerName>singletonList(serverName));
        sslSocket.setSSLParameters(sslParams);

        return sslSocket;
    }

}
