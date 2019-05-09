/*
 * SampleTest.java
 * 
 * Copyright (C) 2018 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package services;

import datatype.CreditCard;
import domain.Administrator;
import domain.Auditor;
import forms.AdministratorForm;
import forms.AuditorForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DataBinder;
import utilities.AbstractTest;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class RegisterAndEditAuditorTest extends AbstractTest {

	@Autowired
	private ActorService			actorService;
	@Autowired
	private AuditorService	auditorService;


	/*
	 * Testing functional requirement : requirement 4.2 (auditor register)
	 * Positive:An actor who is authenticated as Administrator can register other auditor
	 * Negative:An actor who is not authenticated can not register other auditor
	 * Sentence coverage: 100%
	 * Data coverage: 30%
	 */

	@Test
	public void registerAuditorDriver() {
		final Object testingData[][] = {
			{
				"prueba1", "123456", "123456", "prueba1", "prueba", "", "prueba@prueba.com", "600102030", "", 21, "prueba1", "MASTERCARD", "5473259551394900", "2026/10/20", 841, "admin1", null
			}
			,
				{
				"", "123456", "123456", "prueba1", "prueba", "", "prueba@prueba.com", "600102030", "", 21, "prueba1", "MASTERCARD", "5473259551394900", "2026/10/20", 841, "", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateRegisterAuditor((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], (String) testingData[i][8], (int) testingData[i][9], (String) testingData[i][10], (String) testingData[i][11], (String) testingData[i][12], (String) testingData[i][13], (Integer) testingData[i][14],
				(String) testingData[i][15], (Class<?>) testingData[i][16]);
	}

	/*
	 * Testing functional requirement : requirement 8.2 [Acme-Hacker-Rank] (auditor edit personal data)
	 * Positive:An actor who is authenticated as Auditor can edit your personal data
	 * Negative:An actor who is authenticated as Rookie can not edit edit personal data of any auditor
	 * Sentence coverage: 80%
	 * Data coverage: 18%
	 */

	@Test
	public void editAuditorDriver() {
		final Object testingData[][] = {
				{
						"auditor1", "newName", null
				}
				,
				{
				"rookie1", "newName", NullPointerException.class
				}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateEditAuditor((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	// Ancillary methods ------------------------------------------------------

	public void templateRegisterAuditor(final String username, final String password, final String comfirmPass, final String name, final String surname, final String photo, final String email, final String phoneNumber, final String address,
		final int vatNumber, final String holderName, final String brandName, final String creditCardNumber, final String expiration, final Integer cvvCode, final String admin, final Class<?> expected) {

		Class<?> caught;
		caught = null;
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		try {
			this.authenticate(admin);
			final Date fecha = sdf.parse(expiration);
			final AuditorForm aForm = new AuditorForm();

			final DataBinder binding = new DataBinder(new Auditor());

			aForm.setName(name);
			aForm.setSurname(surname);
			aForm.setUsername(username);
			aForm.setPassword(password);
			aForm.setConfirmPass(comfirmPass);
			aForm.setEmail(email);
			aForm.setAddress(address);
			aForm.setPhoneNumber(phoneNumber);
			aForm.setPhoto(photo);
			aForm.setVatNumber(vatNumber);

			final CreditCard c = new CreditCard();
			c.setBrandName(brandName);
			c.setCvv(cvvCode);
			c.setExpirationYear(fecha);
			c.setHolder(holderName);
			c.setNumber(creditCardNumber);
			final Auditor a = this.auditorService.reconstruct(aForm, binding.getBindingResult());
			a.setCreditCard(c);

			this.auditorService.save(a);

		} catch (final Throwable e) {
			caught = e.getClass();
		}
		super.checkExceptions(expected, caught);

	}

	private void templateEditAuditor(final String auditor, final String newName, final Class<?> class1) {
		Class<?> caught;
		caught = null;
		try {

			super.authenticate(auditor);
			final DataBinder binding = new DataBinder(new Auditor());
			final Auditor a = this.auditorService.findOne(this.actorService.getActorLogged().getId());
			a.setName(newName);
			final Auditor result = this.auditorService.reconstruct(a, binding.getBindingResult());

			this.auditorService.save(result);
			this.auditorService.flush();

		} catch (final Exception e) {
			caught = e.getClass();
		}
		super.checkExceptions(class1, caught);
	}
}
