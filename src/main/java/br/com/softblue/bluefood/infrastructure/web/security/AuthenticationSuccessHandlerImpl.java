package br.com.softblue.bluefood.infrastructure.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import br.com.softblue.bluefood.util.SecurityUtils;


/**
 * 
 * @author Nido
 * 
 * - Classe SecurityConfig - ".successHandler(null)" - vamos trabalhar com ele.
 * 
 * - AuthenticationSuccessHandlerImpl
 * É uma classe utilizada pelo SPRINGDATA para ser chamada
 * quando dá a ocorrência do evento da Autenticação feita com sucesso!
 * 
 * E aqui direcionamos o usuário para um lado ou outro, dependendo se a 
 * autenticação foi feita com sucesso e se é CLIENTE ou RESTAURANTE.
 *
 *	
 */

public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(
			HttpServletRequest request, 
			HttpServletResponse response,
			Authentication authentication) 
			throws IOException, ServletException {
		
		Role role = SecurityUtils.loggedUser().getRole();
		
		if (role == Role.CLIENTE) {
			response.sendRedirect("cliente/home");
			
		} else if (role == Role.RESTAURANTE) {
			response.sendRedirect("restaurante/home");
			
		} else {
			throw new IllegalStateException("Erro na autenticação");
		}
		
		
		
	}

	
}
