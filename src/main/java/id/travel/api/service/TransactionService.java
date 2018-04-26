package id.travel.api.service;

import java.util.List;

import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;
import org.warungikan.db.model.TransactionState;

public interface TransactionService {

	public Transaction addTransaction(String customer_id, String agent, List<TransactionDetail> details, Long transportPrice);
	public List<Transaction> getTransactionByUserId(String user_id);
	public List<TransactionState> getTransactionStateByTransaction(Long oid);
	public Transaction markTransactionAsPaid(Long trxId);
	public Transaction markTransactionAsProcessing(Long trxId);
	public Transaction markTransactionAsDelivering(Long trxId);
	public Transaction markTransactionAsReceiving(Long trxId);
	public Transaction cancelTransaction(Long trxId);
}
