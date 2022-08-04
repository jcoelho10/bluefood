package br.com.softblue.bluefood.application.service;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.softblue.bluefood.util.IOUtils;

@Service
public class ImageService {

	@Value("${bluefood.files.logotipo}")
	private String logotipoDir;
	
	@Value("${bluefood.files.categoria}")
	private String categoriaDir;	
	
	@Value("${bluefood.files.comida}")
	private String comidaDir;
	
	public void uploadLogotipo(MultipartFile multipartFile, String fileName) {
		try {
			IOUtils.copy(multipartFile.getInputStream(), fileName, logotipoDir);
		} catch (IOException e) {
			throw new ApplicationServiceException(e);
		}
	}
	
	public void uploadComida(MultipartFile multipartFile, String fileName) {
		try {
			IOUtils.copy(multipartFile.getInputStream(), fileName, comidaDir);
		} catch (IOException e) {
			throw new ApplicationServiceException(e);
		}
	}
	
	
	public byte[] getBytes(String type, String imgName) {
		
		try {
			String dir;
			
			if ("comida".equals(type)) {
				dir = comidaDir;
			} else if ("logotipo".equals(type)) {
				dir = logotipoDir;
			} else if ("categoria".equals(type)) {
				dir = categoriaDir;
			} else {
				throw new Exception(type + " não é um tipo de imagem válido");
			}
			
			return IOUtils.getBytes(Paths.get(dir, imgName));
		} catch (Exception e) {
			throw new ApplicationServiceException(e);
		}
	}
}
