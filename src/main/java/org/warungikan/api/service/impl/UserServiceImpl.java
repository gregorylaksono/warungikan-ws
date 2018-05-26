package org.warungikan.api.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.warungikan.api.config.EmailConfig;
import org.warungikan.api.service.IUserService;
import org.warungikan.api.utils.Constant;
import org.warungikan.db.model.AgentData;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.TopupWalletHistory;
import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.AgentDataRepository;
import org.warungikan.db.repository.RoleRepository;
import org.warungikan.db.repository.TopupWalletRepository;
import org.warungikan.db.repository.UserRepository;

import com.google.gson.Gson;

@Service
@Transactional
public class UserServiceImpl implements IUserService{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private TopupWalletRepository topUpWalletRepository;

	@Autowired
	private AgentDataRepository agentDataRepository;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private EmailConfig config;

	@Override
	public User login(String email, String password) {

		return null;
	}

	@Override
	public User registerUser(User user,List<String> roles, String pricePerKm) {
		try {
			InternetAddress emailAddr = new InternetAddress(user.getEmail());
			emailAddr.validate();
		} catch (AddressException e) {
			return null;
		}
		if(!isUserIdExist(user.getEmail())){
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String password  = user.getPassword();
			if(password != null) user.setPassword(passwordEncoder.encode(password));
			List<Role> rolesDb = roleRepository.findRolesByArrayName(roles);
			user.setCreationDate(new Date());
			user.setEnable(false);
			user.setBalance(0l);
			user.addAllRole(rolesDb);
			String random_confirmation_key = generateRandomConfirmationKey();
			user.setRandomConfirmationKey(random_confirmation_key);

			if(pricePerKm != null){
				Long.parseLong(pricePerKm);
				AgentData d = new AgentData();
				Map<String, String> data = new HashMap<>();
				data.put(Constant.AGENT_DATA_KEY_PRICE_PER_KM, pricePerKm);
				String json = new Gson().toJson(data);
				d.setData(json);
				d.setAgent(user);
				String generatedPwd = RandomStringUtils.randomAlphabetic(10);
				user.setPassword(passwordEncoder.encode(generatedPwd));

				user = userRepository.save(user);
				agentDataRepository.save(d);
				sendAgentMessage(user, config.getWeb_ui_reg_conf()+"/"+user.getRandomConfirmationKey(), generatedPwd);
			}else{
				user = userRepository.save(user);
				sendUserMessage(user, config.getWeb_ui_reg_conf()+"/"+user.getRandomConfirmationKey());
			}
			return user;
		}else{
			return null;
		}

	}

	

	private void sendUserMessage(User user, String link) {
		String name = user.getName();
		String htmlMessage = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"+
				"<html lang=\"en\">"+
				"<head>"+
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"+
				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"+
				"<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"+
				"<link href=\"https://fonts.googleapis.com/css?family=Roboto\" rel=\"stylesheet\">"+
				"<title></title>"+
				"<style type=\"text/css\">"+
				".logo{display:block;width:100%;}"+
				".message{width:700px;margin:0 auto;}"+
				"p{font-family: 'Roboto', sans-serif;}"+
				"</style>"+
				"</head>"+
				"<body style=\"margin:0; padding:0; background-color:#F2F2F2;\">"+
				"<img src=\"http://warungikan.com/images/headweb3.png\" class=\"logo\">"+
				"<div class=\"message\">"+
				"<p>Hi "+name+",</p>"+
				"<p>Selamat datang di warungikan.com. Terima kasih telah mendaftar di toko kami. Pendaftaran anda hampir selesai. Kunjungi <a href=\""+link+"\">link ini</a> sebagai konfirmasi pendaftaran anda</p>"+
				"<br>"+
				"<p>Best regards,</p>"+
				"<p>WarungIkan admin</p>"+
				"</div>"+
				"</body>"+
				"</html>";
		MimeMessagePreparator preparator = new MimeMessagePreparator(){

			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper msg = new MimeMessageHelper(mimeMessage);
				msg.setTo(user.getEmail());
				msg.setSubject("Konfirmasi email anda");
				msg.setFrom("admin@warungikan.com");
				msg.setText(htmlMessage, true);

			}

		};
		try{
			mailSender.send(preparator);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void sendAgentMessage(User user, String link, String password) {
		String name = user.getName();
		String htmlMessage = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"+
				"<html lang=\"en\">"+
				"<head>"+
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"+
				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"+
				"<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"+
				"<link href=\"https://fonts.googleapis.com/css?family=Roboto\" rel=\"stylesheet\">"+
				"<title></title>"+
				"<style type=\"text/css\">"+
				".logo{display:block;width:100%;}"+
				".message{width:700px;margin:0 auto;}"+
				"p{font-family: 'Roboto', sans-serif;}"+
				"</style>"+
				"</head>"+
				"<body style=\"margin:0; padding:0; background-color:#F2F2F2;\">"+
				"<img src=\"http://warungikan.com/images/headweb3.png\" class=\"logo\">"+
				"<div class=\"message\">"+
				"<p>Hi "+name+",</p>"+
				"<p>Selamat datang di warungikan.com. Terima kasih telah mendaftar di toko kami. Pendaftaran anda hampir selesai. Kunjungi <a href=\""+link+"\">link ini</a> sebagai konfirmasi pendaftaran anda. Berikut login data anda:</p>"+
				"<pre> Username:"+user.getEmail()+
				"<pre> Password:"+password+
				"<pre> Ubah password anda secepatnya. Demi keamanan anda"+
				"<br>"+
				"<p>Best regards,</p>"+
				"<p>WarungIkan admin</p>"+
				"</div>"+
				"</body>"+
				"</html>";
		MimeMessagePreparator preparator = new MimeMessagePreparator(){

			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper msg = new MimeMessageHelper(mimeMessage);
				msg.setTo(user.getEmail());
				msg.setSubject("Konfirmasi email anda");
				msg.setFrom("admin@warungikan.com");
				msg.setText(htmlMessage, true);

			}

		};
		try{
			mailSender.send(preparator);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
		private String generateRandomConfirmationKey() {
		String random = UUID.randomUUID().toString()+UUID.randomUUID().toString().replace("-", "");
		User u = userRepository.findUserByConfirmationKey(random);
		while(u!=null){
			random = UUID.randomUUID().toString()+UUID.randomUUID().toString().replace("-", "");
			u = userRepository.findUserByConfirmationKey(random);
		}
		return random;
	}

	@Override
	public User generateUserId() {

		return null;
	}

	@Override
	public List<User> getAllAgents() {
		return userRepository.findAllAgent();
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAllUsersEnabled();
	}

	@Override
	public Role getRoleByName(String name) {

		return roleRepository.findByName(name);
	}

	@Override
	public User getUserById(String userId) {
		return userRepository.findUserByUserId(userId);
	}

	@Override
	public void setAvailableUser(String userId, Boolean enabled) {
		User user = userRepository.findUserByUserId(userId);
		user.setEnable(enabled);
		user.setLastModifiedDate(new Date());
		userRepository.save(user);
	}

	@Override
	public User update(String user_id, User user) {
		User u = userRepository.findUserByUserId(user_id);
		if(!u.getEmail().equals(user.getEmail())){
			if(!isUserIdExist(user.getEmail())){
				u.setEmail(user.getEmail());
			}else{
				return null;
			}
		}
		u.setLastModifiedDate(new Date());
		u.setAddress(user.getAddress());
		u.setEmail(user.getEmail());
		u.setName(user.getName());
		u.setTelpNo(user.getTelpNo());
		u.setCity(user.getCity());
		return userRepository.save(u);		
						
	}

	private List<Role> getRoles(Collection<Role> roles){
		List<Role> r = new ArrayList();
		for(Role s : roles){
			Role role = roleRepository.findByName(s.getName());
			r.add(role);
		}
		return r;
	}

	private boolean isUserIdExist(String user_id){
		User u = userRepository.findUserByUserId(user_id);
		return (u != null);
	}

	@Override
	public User delete(String email) {
		User user = userRepository.findUserByUserId(email);
		if(user != null) {
			user.setEnable(false);
			user.setLastModifiedDate(new Date());			
			return userRepository.save(user);
		}

		return null;

	}

	@Override
	public Boolean changePassword(String user_id, String password, String newPassword) {
		User u = userRepository.findUserByUserId(user_id);
		if(BCrypt.checkpw(password,u.getPassword())){
			u.setPassword(newPassword);
			userRepository.save(u);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Boolean addBalance(String user_id, Long amount, Date topupDate, String refNo) {
		User u = userRepository.findUserByUserId(user_id);
		if(u!=null){
			List<Role> roles = roleRepository.findRoleByUser(user_id);
			if(roles.stream().filter( r -> r.getName().equals("ROLE_USER")).collect(Collectors.toList()).size() > 0) {
				TopupWalletHistory t = new TopupWalletHistory();
				t.setAmount(amount);
				t.setUser(u);
				t.setTop_up_id(generateTopupId(u));
				t.setReferenceBankNo(refNo);
				t.setTopupDate(topupDate);
				t.setCreationDate(new Date());
				u.setBalance(u.getBalance() + amount);
				topUpWalletRepository.save(t);
				userRepository.save(u);
				return true;
			}
		}
		return false;
	}
	@Override
	public Boolean doTopup(String userId, TopupWalletHistory topup) {
		try{
			User u = userRepository.findUserByUserId(userId);
			topup.setUser(u);
			topup.setCreationDate(new Date());
			topup.setRelease(false);
			topup.setTop_up_id(generateTopupId(u));
			topUpWalletRepository.save(topup);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	private String generateTopupId(User customer) {
		String ab = "TP"+customer.getName().substring(0,2).toUpperCase();
		String random = String.valueOf(new BigDecimal(Math.random() * 1000000)).substring(0,8).replace(".", "");
		String topupId = ab+"-"+random;
		Boolean isAvailable = false;
		while(!isAvailable){
			TopupWalletHistory t = topUpWalletRepository.findTopupByTopupId(topupId);
			if(t == null){
				isAvailable = true;
			}else{
				random = String.valueOf(new BigDecimal(Math.random() * 1000000)).substring(0,8).replace(".", "");
				topupId = ab+"-"+random;
			}
		}

		return topupId;
	}

	@Override
	public User registerUser(User user) {

		registerUser(user, Arrays.asList(new String[]{"ROLE_USER"}), null);
		return userRepository.save(user);
	}

	@Override
	public Long getPricePerKm(String agent_id) {
		User u = userRepository.findUserByUserId(agent_id);
		AgentData data =agentDataRepository.findDataByUser(u);
		if(data != null){
			Map m = new Gson().fromJson(data.getData(), Map.class);
			String sAgentRate = String.valueOf( m.get(Constant.AGENT_DATA_KEY_PRICE_PER_KM));
			return Long.parseLong(sAgentRate);
		}else{
			return null;
		}
	}

	@Override
	public List<Role> getRoles(String userId) {
		List<Role>roles = roleRepository.findRoleByUser(userId);
		return roles;
	}

	@Override
	public Boolean enableUser(String veification_id) {
		try{
			User user = userRepository.findUserByConfirmationKey(veification_id);
			if(user == null) return false;
			user.setEnable(true);
			user.setLastModifiedDate(new Date());
			userRepository.save(user);
			return true;
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public AgentData getAgentData(String user_id) {
		User user = userRepository.findUserByUserId(user_id);
		AgentData agent = agentDataRepository.findDataByUser(user);
		return agent;
	}

	@Override
	public Boolean changeCoordinate(String user_id, Double lat, Double lng) {
		try{
			User user = userRepository.findUserByUserId(user_id);
			user.setLatitude(lat);
			user.setLongitude(lng);
			userRepository.save(user);
			return true;
		}catch(Exception e){
			return false;
		}
	}



}
