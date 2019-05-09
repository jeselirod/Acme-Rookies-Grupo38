
package converters;

import domain.Rookie;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import repositories.RookieRepository;

import javax.transaction.Transactional;

@Component
@Transactional
public class StringToRookieConverter implements Converter<String, Rookie> {

	@Autowired
	RookieRepository	rookieRepository;


	@Override
	public Rookie convert(final String text) {
		Rookie result;
		int id;

		try {
			if (StringUtils.isEmpty(text))
				result = null;
			else {
				id = Integer.valueOf(text);
				result = this.rookieRepository.findOne(id);
			}
		} catch (final Throwable oops) {
			throw new IllegalArgumentException(oops);
		}

		return result;
	}

}
