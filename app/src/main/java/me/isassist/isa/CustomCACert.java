package me.isassist.isa;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * A class that allows to use a custom CA certificate for HTTPS connections.
 * The certificate must be added in project resources.
 */
public class CustomCACert
{
    private final String TAG = this.getClass().getSimpleName();
    Context mContext;
    int mCertResourceID;

    /**
     * Class only constructor
     * @param context application context
     * @param certResourceID resource ID of certificate file in raw resources
     */
    public CustomCACert(Context context, int certResourceID)
    {
        mContext = context;
        mCertResourceID = certResourceID;
    }

    /**
     * Method returns SSLSocketFactory that is needed to make HTTPSURLConnection
     * with custom CA certificate
     * @return valid SSLSocketFactory that accepts a new CA certificate
     */
    public SSLSocketFactory getSSLSocketFactory()
    {
        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = null;
            cf = CertificateFactory.getInstance("X.509");

            InputStream caInput = new BufferedInputStream(mContext.getResources().openRawResource(mCertResourceID));
            Certificate ca;

            ca = cf.generateCertificate(caInput);
            caInput.close();


            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            return context.getSocketFactory();
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage());
            return null;
        }
    }
}
