package com.myapp.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.myapp.entity.Employee;
import com.myapp.repository.EmployeeRepo;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepo repository;

	@Autowired
	private JavaMailSender mailSender;

	public Employee findEmployeeByEmail(String email) {
		return repository.findByEmail(email);
	}

	public Employee findByVerificationCode(String code) {
		return repository.findByVerificationCode(code);
	}

	public Employee findByResetPasswordToken(String token) {
		return repository.findByResetPasswordToken(token);
	}

	public void registerUser(Employee employee, String siteUrl) {
		String randomCode = UUID.randomUUID().toString();
		employee.setVerificationCode(randomCode);
		employee.setEnabled(false);
		repository.save(employee);
		sendVerificationEmail(employee, siteUrl);
	}

	public void sendVerificationEmail(Employee employee, String siteUrl) {
		String toAddress = employee.getEmail();
		String fromAddress = "xdsunilrathod@gmail.com";
		String senderName = "Sunil Rathod";
		String subject = "Please verify your registration";
		String content = "Dear [[name]],<br>" + "Please click the link below to verify your registration:<br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>" + "Thank you,<br>" + "Sunil Rathod.";

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setFrom(fromAddress, senderName);
			helper.setTo(toAddress);
			helper.setSubject(subject);

			content = content.replace("[[name]]", employee.getFirstName());
			String verifyURL = siteUrl + "/verify?code=" + employee.getVerificationCode();

			content = content.replace("[[URL]]", verifyURL);

			helper.setText(content, true);

			mailSender.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean verify(String verificationCode) {
		Employee employee = repository.findByVerificationCode(verificationCode);

		if (employee == null || employee.isEnabled()) {
			return false;
		} else {
			employee.setVerificationCode(null);
			employee.setEnabled(true);
			repository.save(employee);
			return true;
		}
	}

	public void updateResetPasswordToken(String token, String email) {
		Employee employee = repository.findByEmail(email);
		if (employee != null) {
			employee.setResetPasswordToken(token);
			repository.save(employee);
		}
	}

	public void updatePassword(Employee employee, String newPassword) {
		employee.setPassword(newPassword);
		employee.setResetPasswordToken(null);
		repository.save(employee);
	}

	public void sendResetPasswordEmail(Employee employee, String siteUrl) {
		String toAddress = employee.getEmail();
		String fromAddress = "xdsunilrathod@gmail.com";
		String senderName = "Sunil Rathod";
		String subject = "Reset Password";
		String content = "Dear [[name]],<br>" + "Please click the link below to reset your password:<br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">RESET PASSWORD</a></h3>" + "Thank you,<br>"
				+ "Sunil Rathod.";

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setFrom(fromAddress, senderName);
			helper.setTo(toAddress);
			helper.setSubject(subject);

			content = content.replace("[[name]]", employee.getFirstName());
			String resetPasswordURL = siteUrl + "/reset_password?token=" + employee.getResetPasswordToken();

			content = content.replace("[[URL]]", resetPasswordURL);

			helper.setText(content, true);

			mailSender.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
