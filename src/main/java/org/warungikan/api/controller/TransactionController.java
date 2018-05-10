package org.warungikan.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.warungikan.api.model.BasicResponse;
import org.warungikan.api.model.response.AgentStock;
import org.warungikan.api.service.ITransactionService;
import org.warungikan.api.service.IUserService;
import org.warungikan.api.utils.Constant;
import org.warungikan.api.utils.SecurityUtils;
import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;
import org.warungikan.db.model.TransactionState;
import org.warungikan.db.model.User;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	@Autowired
	private IUserService userService;

	@Autowired
	private ITransactionService transactionService;
	
	@PostMapping("/balance/{user_id}/{balance}")
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
	
	@PostMapping("/check/{customer_id}/{agent_id}/{total_km}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT')")
	public ResponseEntity checkTransaction(@PathVariable(value = "customer_id", required = true) String customer_id,
										   @PathVariable(value = "agent_id", required = true) String agent_id,
										   @PathVariable(value = "total_km", required = true) String total_km,
										   @RequestBody Set<TransactionDetail> details){
		try {
			Transaction transaction = transactionService.checkTransaction(customer_id, agent_id, total_km, details);
			return new ResponseEntity(transaction, HttpStatus.ACCEPTED);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@PostMapping("/{agent_id}/{transport_price}")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity addTransaction(@PathVariable(value="agent_id", required=true) String agent_id,
										 @PathVariable(value="transport_price", required=true) String transport_price,
										 @RequestBody Set<TransactionDetail> details , HttpServletRequest request){
		try{
			String token = request.getHeader(Constant.HEADER_STRING);
			String customer_id = SecurityUtils.getUsernameByToken(token);
			Long transportPrice = Long.parseLong(transport_price);
			Transaction trx = transactionService.addTransaction(customer_id, agent_id, details, transportPrice);
			return new ResponseEntity(trx, HttpStatus.ACCEPTED);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/customer")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
	public ResponseEntity getTransactionCustomer(HttpServletRequest request){
		try {
			String token = request.getHeader(Constant.HEADER_STRING);
			String user_id = SecurityUtils.getUsernameByToken(token);
			List<Transaction> trxs = transactionService.getTransactionCustomer(user_id);
			return new ResponseEntity(trxs, HttpStatus.ACCEPTED);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/state")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT')")
	public ResponseEntity getTransactionState(@RequestParam(value = "state_id", required = true) String trx_id){
		try{
			Long trxId = Long.parseLong(trx_id);
			List<TransactionState> trxsState = transactionService.getTransactionStateByTransaction(trxId);
			return new ResponseEntity(trxsState, HttpStatus.ACCEPTED);	
		}catch(Exception e){
			return new ResponseEntity<>(new BasicResponse("Request can not be processed","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/agent")
	@PreAuthorize("hasRole('ROLE_AGENT')")
	public ResponseEntity getTransactionAgent(HttpServletRequest request){
		try {
			String token = request.getHeader(Constant.HEADER_STRING);
			String user_id = SecurityUtils.getUsernameByToken(token);
			List<Transaction> trxs = transactionService.getTransactionAgent(user_id);
			return new ResponseEntity(trxs, HttpStatus.ACCEPTED);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/mark_paid/{trxId}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT') or hasRole('ROLE_ADMIN')")
	public ResponseEntity markTransactionAsPaid(@PathVariable(value="trxId", required=true) String trxId){
		try{
			TransactionState result = transactionService.markTransactionAsPaid(Long.parseLong(trxId));	
			return new ResponseEntity(new BasicResponse("Transaction is successfully marked as paid", "SUCCESS", ""), HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/mark_processing/{trxId}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT') or hasRole('ROLE_ADMIN')")
	public ResponseEntity markTransactionAsProcessing(@PathVariable(value="trxId", required=true) String trxId){
		try{
			TransactionState result = transactionService.markTransactionAsProcessing(Long.parseLong(trxId));	
			return new ResponseEntity(new BasicResponse("Transaction is successfully marked as processed", "SUCCESS", ""), HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/mark_delivering/{trxId}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT') or hasRole('ROLE_ADMIN')")
	public ResponseEntity markTransactionAsDelivering(@PathVariable(value="trxId", required=true) String trxId){
		try{
			TransactionState result = transactionService.markTransactionAsDelivering(Long.parseLong(trxId));	
			return new ResponseEntity(new BasicResponse("Transaction is successfully marked as delievered", "SUCCESS", ""), HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/mark_receiving/{trxId}")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity markTransactionAsReceiving(@PathVariable(value="trxId", required=true) String trxId){
		try{
			TransactionState result = transactionService.markTransactionAsReceiving(Long.parseLong(trxId));	
			return new ResponseEntity(new BasicResponse("Transaction is successfully marked as received", "SUCCESS", ""), HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/mark_cancel/{trxId}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT') or hasRole('ROLE_ADMIN')")
	public ResponseEntity cancelTransaction(@PathVariable(value="trxId", required=true) String trxId){
		try{
			TransactionState result = transactionService.cancelTransaction(Long.parseLong(trxId));	
			return new ResponseEntity(new BasicResponse("Transaction is successfully marked as canceled", "SUCCESS", ""), HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/balance/customer")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
	public ResponseEntity getBalanceCustomer(HttpServletRequest request){
		try{
			String token = request.getHeader(Constant.HEADER_STRING);
			String user_id = SecurityUtils.getUsernameByToken(token);
			Long balance = transactionService.calculateBalanceCustomer(user_id);
			Map<String, String> response = new HashMap<String, String>();
			response.put("balance", String.valueOf(balance));
			return new ResponseEntity(response, HttpStatus.ACCEPTED);	
		}catch(Exception e){
			return new ResponseEntity<>(new BasicResponse("Request can not be processed","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/balance/agent")
	@PreAuthorize("hasRole('ROLE_AGENT') or hasRole('ROLE_ADMIN')")
	public ResponseEntity getBalanceAgent(HttpServletRequest request){
		try{
			String token = request.getHeader(Constant.HEADER_STRING);
			String user_id = SecurityUtils.getUsernameByToken(token);
			Long balance = transactionService.calculateBalanceAgent(user_id);
			Map<String, String> response = new HashMap<String, String>();
			response.put("balance", String.valueOf(balance));
			return new ResponseEntity(response, HttpStatus.ACCEPTED);	
		}catch(Exception e){
			return new ResponseEntity<>(new BasicResponse("Request can not be processed","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/transport_price")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
	public ResponseEntity calculateTransportPrice(@RequestParam(value = "agent_id", required = true) String user_id,
												  @RequestParam(value = "total_km", required = true) String total_km){
		try{
			Long price = transactionService.calculateTransportPrice(total_km, user_id);
			Map<String, String> response = new HashMap<String, String>();
			response.put("transport_price", String.valueOf(price));
			return new ResponseEntity(response, HttpStatus.ACCEPTED);	
		}catch(Exception e){
			return new ResponseEntity<>(new BasicResponse("Request can not be processed","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/is_legit")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
	public ResponseEntity isCustomerLegitimateForTransaction(
						@RequestParam(value = "customer_id", required = true) String customer_id,
						@RequestParam(value = "agent", required = true) String agent,
						@RequestParam(value = "total_km", required = true) String total_km,
						@RequestBody Set<TransactionDetail> details){
		try{
			Boolean isLegit = transactionService.isCustomerLegitimateForTransaction(customer_id, agent, total_km, details);
			Map<String, String> response = new HashMap<String, String>();
			response.put("is_legit", String.valueOf(isLegit));
			return new ResponseEntity(response, HttpStatus.ACCEPTED);	
		}catch(Exception e){
			return new ResponseEntity<>(new BasicResponse("Request can not be processed","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/agents")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity getAgentsBasedCustomerLocation(@RequestBody Set<TransactionDetail> details, HttpServletRequest request) {
		try {
			String token = request.getHeader(Constant.HEADER_STRING);
			String customer_id = SecurityUtils.getUsernameByToken(token);
			 List<AgentStock> agentStock = transactionService.getAgentBasedCustomerLocation(details, customer_id);
			 return new ResponseEntity(agentStock, HttpStatus.ACCEPTED);	
		}catch(Exception e) {
			return new ResponseEntity<>(new BasicResponse("Request can not be processed","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}
	
	
}
