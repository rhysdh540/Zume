package dev.rdh.pcl;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class PackedClassLoader extends ClassLoader {
	private final ConcurrentHashMap<String, Long> offsets = new ConcurrentHashMap<>();
	private final String bundleName;

	static {
		registerAsParallelCapable();
	}

	public PackedClassLoader(ClassLoader parent, String bundleName) {
		super(parent);
		this.bundleName = bundleName;

		try (InputStream is = parent.getResourceAsStream(bundleName)) {
			if (is == null) {
				throw new IllegalArgumentException("Bundle not found: " + bundleName);
			}

			if(is.available() <= 0) {
				throw new IllegalArgumentException("Bundle is empty: " + bundleName);
			}

			// populate offsets
			DataInputStream input = new DataInputStream(is);
			int currentPos = 0;
			while(input.available() > 0) {
				int nameLength = input.readInt();
				int length = input.readInt();
				currentPos += 8;

				byte[] nameBytes = new byte[nameLength];
				input.readFully(nameBytes);
				String name = new String(nameBytes);
				currentPos += nameLength;

				offsets.put(name, (long) currentPos << 32 | length); // store position and length in a single long
				currentPos += length;
				input.skipBytes(length);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to load bundle: " + bundleName, e);
		}
	}

	private byte[] getEntry(String name) {
		Long posAndLength = offsets.get(name);
		if (posAndLength == null) {
			return null;
		}

		int pos = (int) (posAndLength >> 32);
		int length = (int) (long) posAndLength;

		try (InputStream is = getParent().getResourceAsStream(bundleName)) {
			byte[] bytes = new byte[length];
			is.skip(pos);
			is.read(bytes);
			return bytes;
		} catch (Exception e) {
			throw new RuntimeException("Failed to load entry: " + name, e);
		}
	}

	@Override
	protected Class<?> findClass(String name) {
		byte[] bytes = getEntry(name.replace('.', '/') + ".class");
		if (bytes == null) {
			throw new ClassNotFoundException(name);
		}

		return defineClass(name, bytes, 0, bytes.length);
	}

	@Override
	protected URL findResource(String name) {
		byte[] bytes = getEntry(name);
		if (bytes == null) {
			return null;
		}

		return new URL(null, "bytes", new ByteArrayURLStreamHandler(bytes));
	}

	public static void main(String[] args) {
		PackedClassLoader loader = new PackedClassLoader(PackedClassLoader.class.getClassLoader(), "bundle");
		loader.loadClass("dev.rdh.pcl.packed.TestMain")
				.getMethod("main", String[].class).invoke(null, (Object) null);
	}
}

