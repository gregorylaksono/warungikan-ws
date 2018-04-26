package id.travel.api.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;
import org.warungikan.db.model.TransactionState;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.TransactionRepository;
import org.warungikan.db.repository.TransactionStateRepository;
import org.warungikan.db.repository.TransactionDetailRepository;
import org.warungikan.db.repository.UserRepository;

import id.travel.api.service.ITransactionService;

public class TransactionServiceImpl implements ITransactionService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	private TransactionStateRepository transactionStateRepository;
	
	@Override
	public Transaction addTransaction(String customer_id, String agentId, Set<TransactionDetail> details,
			Long transportPrice) {
		User customer =  userRepository.findUserByUserId(customer_id);
		User agent = userRepository.findUserByUserId(agentId);
		
		if(customer == null || agent == null) {
			return null;
		}
		
		Long totalPrice = calculateTransactionDetails(details);
		details.stream().forEach( d -> d.setCreationDate(new Date()));
		
		Transaction t = new Transaction();
		t.setAgent(agent).setCustomer(customer).setTransportPrice(transportPrice).
		setTransactionDetails(details).setTotalPrice(totalPrice).setCreationDate(new Date());
		t = transactionRepository.save(t);
		
		TransactionState state = new TransactionState();
		state.setTransaction(t);
		state.setCreationDate(new Date());
		state.setState(TransactionState.TransactionStateEnum.SENT.getState());
		transactionStateRepository.save(state);
		return t;
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
	public List<TransactionState> getTransactionStateByTransaction(Long oid) {
		Transaction t = transactionRepository.findOne(oid);
		List<TransactionState> trxState = transactionStateRepository.findTransactionStateByTransaction(t);
		return trxState;
	}

	@Override
	public Transaction markTransactionAsPaid(Long trxId) {
		Transaction t = transactionRepository.findOne(trxId);
		TransactionState trxState = createTransactionState(TransactionState.TransactionStateEnum.PAID, t);
		transactionStateRepository.save(trxState);
		return t;
	}

	@Override
	public Transaction markTransactionAsProcessing(Long trxId) {
		Transaction t = transactionRepository.findOne(trxId);
		TransactionState trxState = createTransactionState(TransactionState.TransactionStateEnum.PROCESSING, t);
		transactionStateRepository.save(trxState);
		return t;
	}

	@Override
	public Transaction markTransactionAsDelivering(Long trxId) {
		Transaction t = transactionRepository.findOne(trxId);
		TransactionState trxState = createTransactionState(TransactionState.TransactionStateEnum.DELIVERING, t);
		transactionStateRepository.save(trxState);
		return t;
	}

	@Override
	public Transaction markTransactionAsReceiving(Long trxId) {
		Transaction t = transactionRepository.findOne(trxId);
		TransactionState trxState = createTransactionState(TransactionState.TransactionStateEnum.RECEIVED, t);
		transactionStateRepository.save(trxState);
		return t;
	}

	@Override
	public Transaction cancelTransaction(Long trxId) {
		Transaction t = transactionRepository.findOne(trxId);
		TransactionState trxState = createTransactionState(TransactionState.TransactionStateEnum.CANCELED, t);
		transactionStateRepository.save(trxState);
		return t;
	}

	private TransactionState createTransactionState(TransactionState.TransactionStateEnum state, Transaction t){
		TransactionState trxState = new TransactionState();
		trxState.setCreationDate(new Date());
		trxState.setTransaction(t);
		trxState.setState(state.getState());
		return trxState;
	}
}
