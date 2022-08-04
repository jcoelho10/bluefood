package br.com.softblue.bluefood.infrastructure.web.converter;

import java.math.BigDecimal;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.softblue.bluefood.util.FormatUtils;

@Component
public class StringToBigDecimalConverter implements Converter<String, BigDecimal> {

	@Override
	public BigDecimal convert(String source) {			
		return FormatUtils.formatCurrencyBigDecimal(source);
	}
}
