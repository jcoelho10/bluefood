package br.com.softblue.bluefood.infrastructure.web.validator;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import br.com.softblue.bluefood.util.FileType;

/**
 * 
 * @author Nido
 * 
 * Classe que valida se pode ou n�o, realizar o Upload do arquivo
 *
 */
public class UploadValidator implements ConstraintValidator<UploadConstraint, MultipartFile>{

	private List<FileType> acceptedFileTypes;
	
	
	
	@Override
	public void initialize(UploadConstraint constraintAnnotation) {
		acceptedFileTypes = Arrays.asList(constraintAnnotation.acceptedTypes());
	}



	@Override
	public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {		
		if (multipartFile == null) {
			return true;
		}
		
		for (FileType filetype : acceptedFileTypes) {
			if (filetype.sameOf(multipartFile.getContentType())) {
				return true;
			}
		}
		
		return false;
		
	}

	
}
