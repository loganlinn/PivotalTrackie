package com.loganlinn.pivotaltrackie.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.format.DateUtils;
import android.util.Log;

import com.loganlinn.pivotaltrackie.R;

public class HttpUtils {
	private static final int SECOND_IN_MILLIS = (int) DateUtils.SECOND_IN_MILLIS;
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";
	
	public static class PTHttpClient extends DefaultHttpClient {
		private static final String TAG = "PTHttpClient";
		
		final Context context;

		public PTHttpClient(HttpParams params, Context context) {
			super(params);
			this.context = context;
		}

		@Override
		protected ClientConnectionManager createClientConnectionManager() {
			Log.i(TAG, "createClientConnectionManager");
			
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			// Register for port 443 our SSLSocketFactory with our keystore
			// to the ConnectionManager
			registry.register(new Scheme("https", newSSLSocketFactory(), 443));
			return new SingleClientConnManager(getParams(), registry);
		}

		private SSLSocketFactory newSSLSocketFactory() {
			try {
				// Get an instance of the Bouncy Castle KeyStore format
				KeyStore trusted = KeyStore.getInstance("BKS");
				// Get the raw resource, which contains the keystore with
				// your trusted certificates (root and any intermediate certs)
				InputStream in = context.getResources().openRawResource(R.raw.pivotaltracker);
				try {
					// Initialize the keystore with the provided trusted certificates
					// Also provide the password of the keystore
					trusted.load(in, "mysecret".toCharArray());
				} finally {
					in.close();
				}
				// Pass the keystore to the SSLSocketFactory. The factory is responsible
				// for the verification of the server certificate.
				SSLSocketFactory sf = new SSLSocketFactory(trusted);
				// Hostname verification from certificate
				// http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
				sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
				return sf;
			} catch (Exception e) {
				throw new AssertionError(e);
			}
		}
	}

	/**
	 * Generate and return a {@link HttpClient} configured for general use,
	 * including setting an application-specific user-agent string.
	 */
	public static DefaultHttpClient getHttpClient(Context context) {
		final HttpParams params = new BasicHttpParams();

		// Use generous timeouts for slow mobile networks
		HttpConnectionParams
				.setConnectionTimeout(params, 20 * SECOND_IN_MILLIS);
		HttpConnectionParams.setSoTimeout(params, 20 * SECOND_IN_MILLIS);

		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpProtocolParams.setUserAgent(params, buildUserAgent(context));
		
		final DefaultHttpClient client = new DefaultHttpClient(params);
		
//		final PTHttpClient client = new PTHttpClient(params, context);
//		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
//
//        DefaultHttpClient _client = new DefaultHttpClient();
//
//        SchemeRegistry registry = new SchemeRegistry();
//        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
//        socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
//        registry.register(new Scheme("https", socketFactory, 443));
//        SingleClientConnManager mgr = new SingleClientConnManager(_client.getParams(), registry);
//        DefaultHttpClient client = new DefaultHttpClient(mgr, _client.getParams());
//
//        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
//
//		client.addRequestInterceptor(new HttpRequestInterceptor() {
//			public void process(HttpRequest request, HttpContext context) {
//				// Add header to accept gzip content
//				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
//					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
//				}
//
//			}
//		});
//
//		client.addResponseInterceptor(new HttpResponseInterceptor() {
//			public void process(HttpResponse response, HttpContext context) {
//				// Inflate any responses compressed with gzip
//				final HttpEntity entity = response.getEntity();
//				final Header encoding = entity.getContentEncoding();
//				if (encoding != null) {
//					for (HeaderElement element : encoding.getElements()) {
//						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
//							response.setEntity(new InflatingEntity(response
//									.getEntity()));
//							break;
//						}
//					}
//				}
//			}
//		});

		return client;
	}

	/**
	 * Build and return a user-agent string that can identify this application
	 * to remote servers. Contains the package name and version code.
	 */
	private static String buildUserAgent(Context context) {
		try {
			final PackageManager manager = context.getPackageManager();
			final PackageInfo info = manager.getPackageInfo(context
					.getPackageName(), 0);

			// Some APIs require "(gzip)" in the user-agent string.
			return info.packageName + "/" + info.versionName + " ("
					+ info.versionCode + ") (gzip)";
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * Simple {@link HttpEntityWrapper} that inflates the wrapped
	 * {@link HttpEntity} by passing it through {@link GZIPInputStream}.
	 */
	private static class InflatingEntity extends HttpEntityWrapper {
		public InflatingEntity(HttpEntity wrapped) {
			super(wrapped);
		}

		@Override
		public InputStream getContent() throws IOException {
			return new GZIPInputStream(wrappedEntity.getContent());
		}

		@Override
		public long getContentLength() {
			return -1;
		}
	}
}
