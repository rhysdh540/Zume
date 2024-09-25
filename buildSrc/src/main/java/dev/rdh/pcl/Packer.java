package dev.rdh.pcl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Packer {
	/**
	 * Concatenates all the files in the specified stream into the specified output.
	 *
	 * The output file is structured as such:
	 * <ul>
	 *     <li>for each item:
	 *     <ol>
	 *         <li>length of the name of the file (4 bytes)</li>
	 *         <li>length of the contents of the file (4 bytes)</li>
	 *         <li>name of the file</li>
	 *         <li>contents of the file</li>
	 *     </ol>
	 *     </li>
	 * </ul>
	 *
	 * @param files a map of file names to file contents
	 */
	public static byte[] pack(Map<String, byte[]> files) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(DataOutputStream out = new DataOutputStream(baos)) {
			files.forEach((name, data) -> {
				try {

				System.out.println("Packing: " + name);
				byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
				
				out.writeInt(nameBytes.length);
				out.writeInt(data.length);

				out.write(nameBytes);
				out.write(data);
				} catch (IOException e) {
					throw new RuntimeException("Failed to pack file: " + name, e);
				}
			});
		}
		
		return baos.toByteArray();
	}
}
