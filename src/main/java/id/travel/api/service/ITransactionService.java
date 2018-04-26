package id.travel.api.service;

import java.util.List;
import java.util.Set;

import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;
import org.warungikan.db.model.TransactionState;

public interface ITransactionService {

	public Transaction addTransaction(String customer_id, String agent, Set<TransactionDetail> details, Long transportPrice);
	public List<Transaction> getTransactionCustomer(String user_id);
	public List<TransactionState> getTransactionStateByTransaction(Long oid);
	public Transaction markTransactionAsPaid(Long trxId);
	public Transaction markTransactionAsProcessing(Long trxId);
	public Transaction markTransactionAsDelivering(Long trxId);
	public Transaction markTransactionAsReceiving(Long trxId);
	public Transaction cancelTransaction(Long trxId);
}
