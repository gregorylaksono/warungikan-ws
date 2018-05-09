package org.warungikan.api.controller;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.warungikan.api.model.BasicResponse;
import org.warungikan.api.model.request.VShopItem;
import org.warungikan.api.service.IShopItemService;
import org.warungikan.api.service.IUserService;
import org.warungikan.api.service.impl.ShopItemServiceImpl;
import org.warungikan.api.utils.Constant;
import org.warungikan.api.utils.SecurityUtils;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.ShopItem;
import org.warungikan.db.model.ShopItemStock;
import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;
import org.warungikan.db.model.User;

import id.travel.api.test.exception.UserSessionException;
import id.travel.api.test.exception.WarungIkanNetworkException;

@RestController
@RequestMapping("/shop")
public class ShopItemController {
	@Autowired
	private IUserService userService;

	@Autowired
	private IShopItemService shopService;

	@PostMapping("/item")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity createShopItem(@RequestBody VShopItem s){
		try {
			ShopItem shopItem = shopService.createShopItem(s.getName(), s.getDescription(), s.getUrl(), s.getPrice());
			if(shopItem != null) {
				return new ResponseEntity(shopItem, HttpStatus.ACCEPTED);
			}else {
				return new ResponseEntity(new BasicResponse("Cannot create shop item","FAILED",""), HttpStatus.BAD_REQUEST);
			}
		}catch(Exception e) {
			return new ResponseEntity(new BasicResponse("Cannot create shop item","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/item")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity editShopItem(@RequestBody VShopItem s){
		try {
			ShopItem shopItem = shopService.updateShopItem(s.getShopId(), s.getName(), s.getDescription(), s.getUrl(), s.getPrice());
			if(shopItem != null) {
				return new ResponseEntity(shopItem, HttpStatus.ACCEPTED);
			}else {
				return new ResponseEntity(new BasicResponse("Cannot update shop item","FAILED",""), HttpStatus.BAD_REQUEST);
			}
		}catch(Exception e) {
			return new ResponseEntity(new BasicResponse("Cannot update shop item","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/item")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity getShopItem() {
		try {
			List<ShopItem> items = shopService.getAllShopItem();
			return new ResponseEntity(items, HttpStatus.ACCEPTED);
		}catch(Exception e) {
			return new ResponseEntity(new BasicResponse("Cannot retrieve shop item","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/stock/{shop_id}/{user_id}/{amount}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity addStock(@PathVariable(value = "shop_id", required=true) String itemId, 
			@PathVariable(value = "user_id", required=true) String user_id, 
			@PathVariable(value = "amount", required=true) Integer amount ) {
		try {
			ShopItemStock stockAdded = shopService.addStock(itemId, user_id, amount);
			return new ResponseEntity(stockAdded, HttpStatus.ACCEPTED);
		}catch(Exception e) {
			return new ResponseEntity(new BasicResponse("Cannot add shop stock","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/stock")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_AGENT')")
	public ResponseEntity getStockByAgent(HttpServletRequest request) {
		try {
			String token = request.getHeader(Constant.HEADER_STRING);
			String username = SecurityUtils.getUsernameByToken(token);

			User u = userService.getUserById(username);
			List<Role> roles = userService.getRoles(u.getEmail());
			if(roles.stream().filter(r -> r.getName().equals("ROLE_AGENT")).collect(Collectors.toList()).size() > 0){
				List<ShopItemStock> allStock = shopService.getStockByAgent(username);
				return new ResponseEntity(allStock, HttpStatus.ACCEPTED);
			}else if(roles.stream().filter(r -> r.getName().equals("ROLE_ADMIN")).collect(Collectors.toList()).size() > 0){
				List<ShopItemStock> allStock = shopService.getAllStocks();
				return new ResponseEntity(allStock, HttpStatus.ACCEPTED);
			}
			return new ResponseEntity(new BasicResponse("Cannot get shop stock","FAILED",""), HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			return new ResponseEntity(new BasicResponse("Cannot get shop stock","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}



}
