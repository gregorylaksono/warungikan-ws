package id.travel.api.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

import id.travel.api.service.ITransactionService;
import id.travel.api.test.Constant;

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
	
	@Override
	public Transaction addTransaction(String customer_id, String agentId, Set<TransactionDetail> details,
			Long transportPrice) {
		
		User customer =  userRepository.findUserByUserId(customer_id);
		User agent = userRepository.findUserByUserId(agentId);
		
		if(customer == null || agent == null) {
			return null;
		}
		
		Long salePrice = calculateTransactionDetails(details);
		Long totalPrice = salePrice + transportPrice;
		
		Transaction t = new Transaction();
		t.setAgent(agent).setCustomer(customer).setTransportPrice(transportPrice).
		setTransactionDetails(details).setTotalPrice(totalPrice).setCreationDate(new Date());
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
		Long[] ids = extractTrxDetailsId(details);
		List <TransactionDetail> trxDetails = trxDetailRepository.findTransactionDetailByDetailsId(ids);
		
		TransactionState state = new TransactionState();
		state.setTransaction(t);
		state.setCreationDate(new Date());
		state.setState(TransactionState.TransactionStateEnum.SENT.getState());
		transactionStateRepository.save(state);
		
		if(!updateStockItemByDetail(agent, trxDetails)){
			return null;
		}
		
	
		return t;
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
		return t;
	}

	@Override
	public List<Transaction> getTransactionAgent(String user_id) {
		User user  = userRepository.findUserByUserId(user_id);
		List<Transaction> t = transactionRepository.findTransactionAgent(user);
		return t;
	}

	@Override
	public List<TransactionState> getTransactionStateByTransaction(Long oid) {
		Transaction t = transactionRepository.findOne(oid);
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
		TransactionState state = transactionStateRepository.findTransactionState(t, TransactionState.TransactionStateEnum.RECEIVED.getState());
		if(state != null){
			state.setLastModifiedDate(new Date());
		}else{
			state = createTransactionState(TransactionState.TransactionStateEnum.CANCELED, t);
		}
		transactionStateRepository.save(state);
		
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
				transportPrice = Long.parseLong(totalKm) * rate;
				return transportPrice;
			}catch(Exception e){
				return null;
			}
		}
			
		return null;
	}




}
