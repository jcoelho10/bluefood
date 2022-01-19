package br.com.softblue.bluefood.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class IOUtils {

	//M�todo para gravar o conte�do de um arquivo em um diret�rio de destino
	public static void copy(InputStream in, String fileName, String outputDir) throws IOException {
		Files.copy(in, Paths.get(outputDir, fileName), StandardCopyOption.REPLACE_EXISTING);
	}

	//M�todo para pegar o caminho de um arquivo/imagem
	public static byte[] getBytes(Path path) throws IOException {
		return Files.readAllBytes(path);
	}
	
}
