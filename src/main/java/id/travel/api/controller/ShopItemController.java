package id.travel.api.controller;

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
import org.springframework.web.bind.annotation.RestController;
import org.warungikan.db.model.ShopItem;
import org.warungikan.db.model.ShopItemStock;
import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;

import id.travel.api.model.BasicResponse;
import id.travel.api.service.IShopItemService;
import id.travel.api.service.IUserService;
import id.travel.api.service.impl.ShopItemServiceImpl;

@RestController
@RequestMapping("/shop")
public class ShopItemController {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private IShopItemService shopService;
	
	@PostMapping("/item/{shop_name}/{description}/{url}/{price}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity createShopItem(@PathVariable(value = "shop_name", required=true) String name, 
										 @PathVariable(value = "description", required=false) String description, 
										 @PathVariable(value = "url", required=true) String url, 
										 @PathVariable(value = "price", required=true) String price){
		try {
			ShopItem shopItem = shopService.createShopItem(name, description, url, price);
			if(shopItem != null) {
				return new ResponseEntity(shopItem, HttpStatus.ACCEPTED);
			}else {
				return new ResponseEntity(new BasicResponse("Cannot create shop item","FAILED",""), HttpStatus.BAD_REQUEST);
			}
		}catch(Exception e) {
			return new ResponseEntity(new BasicResponse("Cannot create shop item","FAILED",""), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/item/{shop_id}/{shop_name}/{description}/{url}/{price}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity editShopItem(@PathVariable(value = "shop_id", required=true) String shopId,
										@PathVariable(value = "shop_name", required=true) String name, 
										 @PathVariable(value = "description", required=false) String description, 
										 @PathVariable(value = "url", required=true) String url, 
										 @PathVariable(value = "price", required=true) String price){
		try {
			ShopItem shopItem = shopService.updateShopItem(shopId, name, description, url, price);
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
