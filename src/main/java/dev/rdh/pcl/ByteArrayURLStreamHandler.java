package dev.rdh.pcl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class ByteArrayURLStreamHandler extends URLStreamHandler {
	private final byte[] bytes;

	public ByteArrayURLStreamHandler(byte[] bytes) {
		super();
		this.bytes = bytes;
	}

	@Override
	protected URLConnection openConnection(URL u) {
		return new URLConnection(u) {
			@Override
			public void connect() {}

			@Override
			public InputStream getInputStream() {
				return new ByteArrayInputStream(bytes);
			}
		};
	}
}
