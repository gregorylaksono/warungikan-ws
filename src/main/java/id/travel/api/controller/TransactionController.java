package id.travel.api.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;
import org.warungikan.db.model.TransactionState;
import org.warungikan.db.model.User;

import id.travel.api.model.BasicResponse;
import id.travel.api.service.ITransactionService;
import id.travel.api.service.IUserService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	@Autowired
	private IUserService userService;

	@Autowired
	private ITransactionService transactionService;
	
	@PostMapping("/transaction/balance/{user_id}/{balance}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity addBalanceUser(@PathVariable( value = "user_id", required = true) String user_id,
										 @PathVariable(value = "balance", required = true) String balance){
		Long bal = null;
		try{
			bal = Long.parseLong(balance);
			Boolean result = userService.addBalance(user_id, bal);
			if(result){
				return new ResponseEntity<BasicResponse>(new BasicResponse("Balance is added", "SUCCESS", ""), HttpStatus.ACCEPTED);
			}else{
				return new ResponseEntity<BasicResponse>(new BasicResponse("Balance is failed to add", "FAILEd", ""), HttpStatus.BAD_REQUEST);
			}
		}catch(Exception e){
			return new ResponseEntity<BasicResponse>(new BasicResponse("Balance is failed to add", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/transaction/check/{customer_id}/{agent_id}/{total_km}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT')")
	public ResponseEntity checkTransaction(@PathVariable(value = "customer_id", required = true) String customer_id,
										   @PathVariable(value = "agent_id", required = true) String agent_id,
										   @PathVariable(value = "total_km", required = true) String total_km,
										   @RequestBody Set<TransactionDetail> details){
		Transaction transaction = transactionService.checkTransaction(customer_id, agent_id, total_km, details);
		return new ResponseEntity(transaction, HttpStatus.ACCEPTED);
	}
	
	
	@PostMapping("/transaction/{customer_id}/{agent_id}/{total_km}")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity addTransaction(@PathVariable(value="customer_id", required=true) String customer_id,
										 @PathVariable(value="agent_id", required=true) String agent_id,
										 @PathVariable(value="transport_price", required=true) String transport_price,
										 @RequestBody Set<TransactionDetail> details ){
		try{
			Long transportPrice = Long.parseLong(transport_price);
			Transaction trx = transactionService.addTransaction(customer_id, agent_id, details, transportPrice);
			return new ResponseEntity(trx, HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("transaction/{user_id}")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity getTransactionCustomer(@PathVariable(value = "user_id", required = true) String user_id){
		
		List<Transaction> trxs = transactionService.getTransactionCustomer(user_id);
		return new ResponseEntity(trxs, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("transaction/state/{user_id}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT')")
	public ResponseEntity getTransactionState(@PathVariable(value = "user_id", required = true) String user_id){
		try{
			Long oid = Long.parseLong(user_id);
			List<TransactionState> trxsState = transactionService.getTransactionStateByTransaction(oid);
			return new ResponseEntity(trxsState, HttpStatus.ACCEPTED);	
		}catch(Exception e){
			return new ResponseEntity<>(new BasicResponse("Request can not be processed","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}
	
//	Agent
//	public List<Transaction> getTransactionAgent(String user_id);
}
