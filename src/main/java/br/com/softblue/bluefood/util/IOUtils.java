package br.com.softblue.bluefood.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class IOUtils {

	//Método para gravar o conteúdo de um arquivo em um diretório de destino
	public static void copy(InputStream in, String fileName, String outputDir) throws IOException {
		Files.copy(in, Paths.get(outputDir, fileName), StandardCopyOption.REPLACE_EXISTING);
	}

	//Método para pegar o caminho de um arquivo/imagem
	public static byte[] getBytes(Path path) throws IOException {
		return Files.readAllBytes(path);
	}
	
}
