package org.warungikan.api.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.warungikan.api.model.BasicResponse;
import org.warungikan.api.model.request.VShopItem;
import org.warungikan.api.service.IShopItemService;
import org.warungikan.api.service.IUserService;
import org.warungikan.api.service.impl.ShopItemServiceImpl;
import org.warungikan.db.model.ShopItem;
import org.warungikan.db.model.ShopItemStock;
import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;

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
	
	@PostMapping("/stock")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity addStock(@PathVariable(value = "shop_id", required=true) String shopId, 
								   @PathVariable(value = "user_id", required=true) String user_id, 
								   @PathVariable(value = "amount", required=true) Integer amount ) {
		try {
			ShopItemStock stockAdded = shopService.addStock(shopId, user_id, amount);
			return new ResponseEntity(stockAdded, HttpStatus.ACCEPTED);
		}catch(Exception e) {
			return new ResponseEntity(new BasicResponse("Cannot add shop stock","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/stock")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity getStockByAgent(@PathVariable(value = "agent_id", required=true) String agent_id) {
		try {
			List<ShopItemStock> allStock = shopService.getStockByAgent(agent_id);
			return new ResponseEntity(allStock, HttpStatus.ACCEPTED);
		}catch(Exception e) {
			return new ResponseEntity(new BasicResponse("Cannot add shop stock","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}

}
