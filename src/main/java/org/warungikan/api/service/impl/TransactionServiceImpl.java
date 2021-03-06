package org.warungikan.api.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.warungikan.api.config.EmailConfig;
import org.warungikan.api.model.GeoCodeDistance;
import org.warungikan.api.model.response.AgentStock;
import org.warungikan.api.service.ITransactionService;
import org.warungikan.api.utils.Constant;
import org.warungikan.api.utils.Util;
import org.warungikan.db.model.AgentData;
import org.warungikan.db.model.ShopItem;
import org.warungikan.db.model.ShopItemStock;
import org.warungikan.db.model.TopupWalletHistory;
import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;
import org.warungikan.db.model.TransactionState;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.TransactionRepository;
import org.warungikan.db.repository.TransactionStateRepository;
import org.warungikan.db.repository.AgentDataRepository;
import org.warungikan.db.repository.RoleRepository;
import org.warungikan.db.repository.ShopItemRepository;
import org.warungikan.db.repository.ShopItemStockRepository;
import org.warungikan.db.repository.TopupWalletRepository;
import org.warungikan.db.repository.TransactionDetailRepository;
import org.warungikan.db.repository.UserRepository;

import com.google.gson.Gson;

@Service
@org.springframework.transaction.annotation.Transactional
public class TransactionServiceImpl implements ITransactionService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private TransactionStateRepository transactionStateRepository;

	@Autowired
	private TopupWalletRepository topupWalletRepository;
	
	@Autowired
	private ShopItemRepository shopItemRepository;
	
	@Autowired
	private ShopItemStockRepository stockRepository;
	
	@Autowired
	private TransactionDetailRepository trxDetailRepository;
	
	@Autowired
	private AgentDataRepository agentDataRepository;
	
	@Autowired
	private EmailConfig config;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Override
	public Transaction addTransaction(String customer_id, String agentId, Set<TransactionDetail> details,
			Long transportPrice, Long distance) {
		
		User customer =  userRepository.findUserByUserId(customer_id);
		User agent = userRepository.findUserByUserId(agentId);
		
		if(customer == null || agent == null) {
			return null;
		}
		
		Long salePrice = calculateTransactionDetails(details);
		Long totalPrice = salePrice + transportPrice;
		
		Long balance = calculateBalanceCustomer(customer_id);
		
		if(balance < totalPrice){
			return null;
		}
		
		Transaction t = new Transaction();
		String trxId = generateTrxId(agent);
		t.setAgent(agent).setCustomer(customer).setTransportPrice(transportPrice).
		setTotalPrice(totalPrice).setTransactionId(trxId).setDistance(distance).setCreationDate(new Date());
		t = transactionRepository.save(t);
		try{
			for(TransactionDetail d: details){
				
				TransactionDetail xd = new TransactionDetail();
				xd.setAmount(d.getAmount());
				xd.setCreationDate(new Date());
				ShopItem i = shopItemRepository.findOne(d.getItem().getId());
				xd.setItem(i);
				xd.setTransaction(t);

				trxDetailRepository.save(xd);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		List <TransactionDetail> trxDetails = trxDetailRepository.findTransactionDetailByTransactionId(t);
		
		TransactionState sentState = new TransactionState();
		sentState.setTransaction(t);
		sentState.setCreationDate(new Date());
		sentState.setState(TransactionState.TransactionStateEnum.SENT.getState());
		
		TransactionState paidState = new TransactionState();
		paidState.setTransaction(t);
		paidState.setCreationDate(new Date());
		paidState.setState(TransactionState.TransactionStateEnum.PAID.getState());
		transactionStateRepository.save(paidState);
		
		if(!updateStockItemByDetail(agent, trxDetails)){
			return null;
		}
		
		sendAgentNotification(t, agent, customer);
	
		return t;
	}
	private void sendAgentNotification(Transaction t, User agent, User customer) {
			String name = agent.getName();
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
					"<p>Anda mendapat pesanan dengan transaksi id "+t.getTransactionId()+". Mohon untuk segera diproses. Untuk lebih detailnya silahkan login di "+config.getWeb_ui()+
					"<p>Best regards,</p>"+
					"<p>WarungIkan admin</p>"+
					"</div>"+
					"</body>"+
					"</html>";
			MimeMessagePreparator preparator = new MimeMessagePreparator(){

				@Override
				public void prepare(MimeMessage mimeMessage) throws Exception {
					MimeMessageHelper msg = new MimeMessageHelper(mimeMessage);
					msg.setTo(agent.getEmail());
					msg.setSubject("Pesanan "+t.getTransactionId());
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
	private String generateTrxId(User agent) {
		String ab = agent.getName().substring(0,2).toUpperCase();
		String random = String.valueOf(new BigDecimal(Math.random() * 100000000)).substring(0,8).replace(".", "");
		String trxId = ab+"-"+random;
		Boolean isAvailable = false;
		while(!isAvailable){
			Transaction t = transactionRepository.findTransactionByTrxId(trxId);
			if(t == null){
				isAvailable = true;
			}else{
				random = String.valueOf(new BigDecimal(Math.random() * 100000000)).substring(0,8).replace(".", "");
				trxId = ab+"-"+random;
			}
		}
		
		return trxId;
	}
	
	private static String generaterandom() {
		String ab = "GR";
		String random = String.valueOf(new BigDecimal(Math.random() * 100000000)).substring(0,8).replace(".", "");
		ab.toUpperCase();
		return ab+"-"+random;
	}
	public static void main(String[] args) {
		for(int i=0; i<100; i++){
			System.out.println(generaterandom());
		}
	}
	@Override
	public Boolean isCustomerLegitimateForTransaction(String customer_id, String agent, String totalKm, Set<TransactionDetail> details) {
		Long transportPrice = calculateTransportPrice(totalKm, agent);
		Long salePrice = calculateTransactionDetails(details);
		Long totalPrice = salePrice + transportPrice;
		
		Long balance = calculateBalanceCustomer(customer_id);
		if(balance > totalPrice){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public Transaction checkTransaction(String customer_id, String agent_id, String totalKm, Set<TransactionDetail> details) {
		User customer = userRepository.findUserByUserId(customer_id);
		User agent = userRepository.findUserByUserId(agent_id);
		
		Long transportPrice = calculateTransportPrice(totalKm, agent_id);
		Long salePrice = calculateTransactionDetails(details);
		Long totalPrice = salePrice + transportPrice;
		
		Long balance = calculateBalanceCustomer(customer_id);
		
		Transaction t = new Transaction();
		t.setAgent(agent).setCustomer(customer).setTotalPrice(salePrice).setTransportPrice(transportPrice);
		
		return t;
	}

	private Long[] extractTrxDetailsId(Set<TransactionDetail> details) {
		List<Long> arr = new ArrayList();
		for(TransactionDetail d : details){
			arr.add(d.getOid());
		}
		return arr.toArray(new Long[]{});
	}


	private Boolean updateStockItemByDetail(User agent, List<TransactionDetail> details){
		Boolean value = false;
		for(TransactionDetail d: details){
			Integer amount = d.getAmount();
			try{
				ShopItem shopItem =  shopItemRepository.findOne(d.getItem().getId());
				ShopItemStock stock = stockRepository.findStockItemByAgentAndItem(agent,shopItem);
				if(stock != null){
					if(stock.getAmount() != null){
						stock.setAmount(stock.getAmount() - amount);			
						stockRepository.save(stock);
						value = true;
					}else{
						value = false;
						break;
					}
				}else{
					value = false;
					break;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return value;
		
	}

	private Long calculateTransactionDetails(Set<TransactionDetail> details) {
		long totalAll = 0;
		for(TransactionDetail d : details){
			long price = d.getAmount() * d.getItem().getPrice();
			totalAll = price + totalAll;
		}
		return totalAll;
	}

	@Override
	public List<Transaction> getTransactionCustomer(String user_id) {
		User user  = userRepository.findUserByUserId(user_id);
		List<Transaction> t = transactionRepository.findTransactionCustomer(user);
		final List<Transaction> trxNew = new ArrayList();
		for(Transaction o : t) {
			TransactionState latestState = transactionStateRepository.findLatestStateByTransaction(o);
			String state = TransactionState.TransactionStateEnum.getStateName(latestState.getState());
			o.setStatus(state);
			trxNew.add(o);
		}
		return trxNew;
	}

	@Override
	public List<Transaction> getTransactionAgent(String user_id) {
		User user  = userRepository.findUserByUserId(user_id);
		List<Transaction> t = transactionRepository.findTransactionAgent(user);
		final List<Transaction> trxNew = new ArrayList();
		for(Transaction o : t) {
			TransactionState latestState = transactionStateRepository.findLatestStateByTransaction(o);
			String state = TransactionState.TransactionStateEnum.getStateName(latestState.getState());
			o.setStatus(state);
			trxNew.add(o);
		}
		return trxNew;
	}

	@Override
	public List<TransactionState> getTransactionStateByTransaction(String trx_id) {
		Transaction t = transactionRepository.findTransactionByTrxId(trx_id);
		List<TransactionState> trxState = transactionStateRepository.findTransactionStateByTransaction(t);
		return trxState;
	}

	@Override
	public TransactionState markTransactionAsPaid(Long trxId) {
		Transaction t = transactionRepository.findOne(trxId);
		TransactionState state = transactionStateRepository.findTransactionState(t, TransactionState.TransactionStateEnum.PAID.getState());
		if(state != null){
			state.setLastModifiedDate(new Date());
		}else{
			state = createTransactionState(TransactionState.TransactionStateEnum.PAID, t);
		}
		transactionStateRepository.save(state);
		return state;
	}

	@Override
	public TransactionState markTransactionAsProcessing(Long trxId) {
		Transaction t = transactionRepository.findOne(trxId);
		TransactionState state = transactionStateRepository.findTransactionState(t, TransactionState.TransactionStateEnum.PROCESSING.getState());
		if(state != null){
			state.setLastModifiedDate(new Date());
		}else{
			state = createTransactionState(TransactionState.TransactionStateEnum.PROCESSING, t);
		}
		transactionStateRepository.save(state);
		return state;
	}

	@Override
	public TransactionState markTransactionAsDelivering(Long trxId) {
		Transaction t = transactionRepository.findOne(trxId);
		TransactionState state = transactionStateRepository.findTransactionState(t, TransactionState.TransactionStateEnum.DELIVERING.getState());
		if(state != null){
			state.setLastModifiedDate(new Date());
		}else{
			state = createTransactionState(TransactionState.TransactionStateEnum.DELIVERING, t);
		}
		transactionStateRepository.save(state);
		return state;
	}

	@Override
	public TransactionState markTransactionAsReceiving(Long trxId) {
		Transaction t = transactionRepository.findOne(trxId);
		TransactionState state = transactionStateRepository.findTransactionState(t, TransactionState.TransactionStateEnum.RECEIVED.getState());
		if(state != null){
			state.setLastModifiedDate(new Date());
		}else{
			state = createTransactionState(TransactionState.TransactionStateEnum.RECEIVED, t);
		}
		transactionStateRepository.save(state);
		
		return state;
	}

	@Override
	public TransactionState cancelTransaction(Long trxId) {
		Transaction t = transactionRepository.findOne(trxId);
		TransactionState state = transactionStateRepository.findTransactionState(t, TransactionState.TransactionStateEnum.CANCELED.getState());
		if(state != null){
			state.setLastModifiedDate(new Date());
		}else{
			state = createTransactionState(TransactionState.TransactionStateEnum.CANCELED, t);
		}
		transactionStateRepository.save(state);
		
//		Rollback transaction
		List<TransactionDetail> details = trxDetailRepository.findTransactionDetailByTransactionId(t);
		for(TransactionDetail d: details){
			TransactionDetail detail = trxDetailRepository.findOne(d.getOid());
			detail.setIsCanceled(true);
			detail.setLastModifiedDate(new Date());
			
			trxDetailRepository.save(detail);
			
			ShopItemStock stock = stockRepository.findStockItemByAgentAndItem(t.getAgent(), detail.getItem());
			stock.setAmount(stock.getAmount() + detail.getAmount());
			stockRepository.save(stock);
			
			trxDetailRepository.save(detail);
		}
		
		return state;
	}

	private TransactionState createTransactionState(TransactionState.TransactionStateEnum state, Transaction t){
		TransactionState trxState = new TransactionState();
		trxState.setCreationDate(new Date());
		trxState.setTransaction(t);
		trxState.setState(state.getState());
		return trxState;
	}

	@Override
	public Long calculateBalanceCustomer(String user_id) {
		long result = 0;
		long topupTotal = 0;
		long trxTotal = 0;
		
		User u = userRepository.findUserByUserId(user_id);
		List<TopupWalletHistory> topups = topupWalletRepository.findTopupsWalletByUser(u);
		for(TopupWalletHistory h: topups){
			topupTotal = topupTotal + h.getAmount();
		}
		
		List<Transaction> trx = transactionRepository.findTransactionCustomer(u);
		for(Transaction t: trx){
			TransactionState stateCanceled = transactionStateRepository.findTransactionState(t, TransactionState.TransactionStateEnum.CANCELED.getState());
			if(stateCanceled == null){
				trxTotal = trxTotal + t.getTotalPrice();
			}
		}
		
		result = topupTotal - trxTotal;
		return result;
	}

	@Override
	public Long calculateBalanceAgent(String user_id) {
		long result = 0;
		long topupTotal = 0;
		long trxTotal = 0;
		
		User u = userRepository.findUserByUserId(user_id);
		List<TopupWalletHistory> topups = topupWalletRepository.findTopupsWalletByUser(u);
		for(TopupWalletHistory h: topups){
			topupTotal = topupTotal + h.getAmount();
		}
		
		
		List<Transaction> trx = transactionRepository.findTransactionAgent(u);
		for(Transaction t: trx){
			TransactionState stateCanceled = transactionStateRepository.findTransactionState(t, TransactionState.TransactionStateEnum.CANCELED.getState());
			if(stateCanceled == null){
				trxTotal = trxTotal + (t.getTotalPrice() - t.getTransportPrice());
			}
		}
		
		result = topupTotal + trxTotal;
		return result;
	}


	@Override
	public List<TransactionState> getTransactionState(String trxId) {
		Transaction t = transactionRepository.findOne(Long.parseLong(trxId));
		return transactionStateRepository.findTransactionStateByTransaction(t);
	}

	@Override
	public Long calculateTransportPrice(String totalKm, String agent_id) {
		User agent = userRepository.findUserByUserId(agent_id);
		AgentData agentData = agentDataRepository.findDataByUser(agent);
		Map data = new Gson().fromJson(agentData.getData(), Map.class);
		String rateS = String.valueOf(data.get(Constant.AGENT_DATA_KEY_PRICE_PER_KM));
		Long rate = null;
		Long transportPrice = null;
		if(rateS!=null){
			try{
				rate = Long.parseLong(rateS);
				transportPrice = new Double(Math.ceil((Double.parseDouble(totalKm) / 1000))).longValue() * rate;
				return transportPrice;
			}catch(Exception e){
				return null;
			}
		}
			
		return null;
	}
	@Override
	public List<AgentStock> getAgentBasedCustomerLocation(Set<TransactionDetail> details, String customer_id) {
		User customer = userRepository.findUserByUserId(customer_id);
		
		List<Long> items = details.stream().map( e-> e.getItem().getId()).collect(Collectors.toList());
		List<User> agents = userRepository.findAgentByShopItem(items, items.size());
		List<AgentStock> stocks = new ArrayList();
		
		for(User agent: agents) {
			boolean isEnough = false;
			for(TransactionDetail detail : details) {
				ShopItem shopItem = shopItemRepository.findOne(detail.getItem().getId());
				ShopItemStock stock = stockRepository.findStockItemByAgentAndItemAndCount(agent, shopItem, detail.getAmount());
				if(stock != null) {
					isEnough = true;
				}else {
					isEnough = false;
					break;
				}
			}
			
			if(isEnough) {
				AgentData data = agentDataRepository.findDataByUser(agent);
				Map m = new Gson().fromJson(data.getData(), Map.class);
				String price_per_km = (String) m.get(Constant.AGENT_DATA_KEY_PRICE_PER_KM);
				GeoCodeDistance geoCode = Util.getDistance(agent.getLatitude()+","+String.valueOf(agent.getLongitude()), String.valueOf(customer.getLatitude())+","+String.valueOf(customer.getLongitude()));
				stocks.add(new AgentStock(agent, price_per_km,geoCode.getDistance().getValue(), geoCode.getStart_location().getLat(), geoCode.getStart_location().getLng()));													
			}
		}
		
		if(stocks.size() > 6){
			stocks = stocks.subList(0, 5);
		}
		return stocks;
	}
	@Override
	public List<TransactionDetail> getTransactionDetail(String trxId) {
		Transaction trx = transactionRepository.findTransactionByTrxId(trxId);
		List<TransactionDetail> list = trxDetailRepository.findTransactionDetailByTransactionId(trx);
		return list;
	}
	@Override
	public List<Transaction> getAllTransactions() {
	
		return transactionRepository.findAll();
	}
	@Override
	public List<TopupWalletHistory> getAllTopupHistory() {
		
		return topupWalletRepository.findAll();
	}
	@Override
	public List<TopupWalletHistory> getTopupHistoryByUser(String user_id) {
		User customer = userRepository.findUserByUserId(user_id);
		return topupWalletRepository.findTopupsWalletByUser(customer);
	}
	@Override
	public Boolean releaseTopup(Long topUpId) {
		try{
			TopupWalletHistory topUp = topupWalletRepository.findOne(topUpId);
			topUp.setLastModifiedDate(new Date());
			topUp.setRelease(true);
			topupWalletRepository.save(topUp);
			return true;
		}catch(Exception e){
			return false;
		}
	}





}
