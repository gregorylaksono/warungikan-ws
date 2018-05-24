package org.warungikan.api.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.warungikan.db.model.TopupWalletHistory;
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
	
	@PostMapping("/balance")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity addBalanceUser(@RequestParam(value = "user_id", required = true) String user_id,
										@RequestParam(value = "balance", required = true) String balance,
										@RequestParam(value = "topup_date", required = true) @DateTimeFormat(pattern="dd-MM-yyyy HH:mm") Date topupDate,
										@RequestParam(value = "ref_bank_no", required = true) String refbankNo){
		Long bal = null;
		try{
			bal = Long.parseLong(balance);
			Boolean result = userService.addBalance(user_id, bal,topupDate, refbankNo);
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
	
	
	@PostMapping("/{agent_id}/{transport_price}/{distance}")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity addTransaction(@PathVariable(value="agent_id", required=true) String agent_id,
										 @PathVariable(value="transport_price", required=true) String transport_price,
										 @PathVariable(value="distance", required=true) String distance,
										 @RequestBody Set<TransactionDetail> details , HttpServletRequest request){
		try{
			String token = request.getHeader(Constant.HEADER_STRING);
			String customer_id = SecurityUtils.getUsernameByToken(token);
			Long transportPrice = Long.parseLong(transport_price);
			Transaction trx = transactionService.addTransaction(customer_id, agent_id, details, transportPrice, Long.parseLong(distance));
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
	public ResponseEntity getTransactionState(@RequestParam(value = "state_id", required = true) String trx_id){
		try{
			List<TransactionState> trxsState = transactionService.getTransactionStateByTransaction(trx_id);
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
			return new ResponseEntity(new BasicResponse("Transaction is successfully marked as paid", "SUCCESS", ""), HttpStatus.ACCEPTED);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/mark_processing/{trxId}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT') or hasRole('ROLE_ADMIN')")
	public ResponseEntity markTransactionAsProcessing(@PathVariable(value="trxId", required=true) String trxId){
		try{
			TransactionState result = transactionService.markTransactionAsProcessing(Long.parseLong(trxId));	
			return new ResponseEntity(new BasicResponse("Transaction is successfully marked as processed", "SUCCESS", ""), HttpStatus.ACCEPTED);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/mark_delivering/{trxId}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT') or hasRole('ROLE_ADMIN')")
	public ResponseEntity markTransactionAsDelivering(@PathVariable(value="trxId", required=true) String trxId){
		try{
			TransactionState result = transactionService.markTransactionAsDelivering(Long.parseLong(trxId));	
			return new ResponseEntity(new BasicResponse("Transaction is successfully marked as delievered", "SUCCESS", ""), HttpStatus.ACCEPTED);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/mark_receiving/{trxId}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
	public ResponseEntity markTransactionAsReceiving(@PathVariable(value="trxId", required=true) String trxId){
		try{
			TransactionState result = transactionService.markTransactionAsReceiving(Long.parseLong(trxId));	
			return new ResponseEntity(new BasicResponse("Transaction is successfully marked as received", "SUCCESS", ""), HttpStatus.ACCEPTED);
		}catch(Exception e){
			return new ResponseEntity(new BasicResponse("Error on processing transaction", "FAILED", ""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/mark_cancel/{trxId}")
	@PreAuthorize("hasRole('ROLE_AGENT') or hasRole('ROLE_ADMIN')")
	public ResponseEntity cancelTransaction(@PathVariable(value="trxId", required=true) String trxId){
		try{
			TransactionState result = transactionService.cancelTransaction(Long.parseLong(trxId));	
			return new ResponseEntity(new BasicResponse("Transaction is successfully marked as canceled", "SUCCESS", ""), HttpStatus.ACCEPTED);
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
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity getAllTransaction() {
		try {
			 List<Transaction> trx = transactionService.getAllTransactions();
			 return new ResponseEntity(trx, HttpStatus.ACCEPTED);	
		}catch(Exception e) {
			return new ResponseEntity<>(new BasicResponse("Request can not be processed","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/topup")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity getAlltopup() {
		try {
			 List<TopupWalletHistory> topups = transactionService.getAllTopupHistory();
			 return new ResponseEntity(topups, HttpStatus.ACCEPTED);	
		}catch(Exception e) {
			return new ResponseEntity<>(new BasicResponse("Request can not be processed","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/topup/user")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity getTopupById(HttpServletRequest request) {
		try {
			String token = request.getHeader(Constant.HEADER_STRING);
			String customer_id = SecurityUtils.getUsernameByToken(token);
		    List<TopupWalletHistory> topups = transactionService.getTopupHistoryByUser(customer_id);
			return new ResponseEntity(topups, HttpStatus.ACCEPTED);	
		}catch(Exception e) {
			return new ResponseEntity<>(new BasicResponse("Request can not be processed","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/detail/{trx_id}")
	public ResponseEntity getTransactionDetail(HttpServletRequest request, @PathVariable("trx_id") String trxId) {
		try {
			String token = request.getHeader(Constant.HEADER_STRING);
			String user_id = SecurityUtils.getUsernameByToken(token);
		    List<TransactionDetail> details = transactionService.getTransactionDetail(trxId);
			return new ResponseEntity(details, HttpStatus.ACCEPTED);	
		}catch(Exception e) {
			return new ResponseEntity<>(new BasicResponse("Request can not be processed","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}
}
