package org.warungikan.api.service;

import java.util.List;
import java.util.Set;

import org.warungikan.api.model.response.AgentStock;
import org.warungikan.db.model.TopupWalletHistory;
import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;
import org.warungikan.db.model.TransactionState;

public interface ITransactionService {

	//Agent, Customer
	public Transaction checkTransaction(String customer_id, String agent_id, String totalKm, Set<TransactionDetail> details);
	
	//Customer
	public Transaction addTransaction(String customer_id, String agent, Set<TransactionDetail> details, Long transportPrice, Long distance);
	
	//Customer
	public List<Transaction> getTransactionCustomer(String user_id);
	
	//Customer, Agent
	public List<TransactionState> getTransactionStateByTransaction(String oid);
	
	//Agent
	public List<Transaction> getTransactionAgent(String user_id);
	
	//Customer
	public TransactionState markTransactionAsPaid(Long trxId);
	
	//Agent
	public TransactionState markTransactionAsProcessing(Long trxId);
	
	//Agent
	public TransactionState markTransactionAsDelivering(Long trxId);
	
	//Customer
	public TransactionState markTransactionAsReceiving(Long trxId);
	
	//Admin
	public TransactionState cancelTransaction(Long trxId);
	
	//Customer
	public Long calculateBalanceCustomer(String user_id);
	
	//Agent
	public Long calculateBalanceAgent(String user_id);
	
	//Agent, Customer, Admin 
	public List<TransactionState> getTransactionState(String trxId);
	
	//Agent, Customer
	public Long calculateTransportPrice(String customer_id, String agent_id);
	
	//Customer
	public Boolean isCustomerLegitimateForTransaction(String customer_id, String agent, String totalKm, Set<TransactionDetail> details);

	public List<AgentStock> getAgentBasedCustomerLocation(Set<TransactionDetail> details, String customer_id);
	
	public List<TransactionDetail> getTransactionDetail(String trxId);

	public List<Transaction> getAllTransactions();
	
	public List<TopupWalletHistory> getAllTopupHistory();
	
	public List<TopupWalletHistory> getTopupHistoryByUser(String user_id);
}
