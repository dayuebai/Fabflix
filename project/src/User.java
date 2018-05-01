/**
 * This User class only has the username field in this example.
 * <p>
 * However, in the real project, this User class can contain many more things,
 * for example, the user's shopping cart items.
 */
import java.util.HashMap;
import java.util.ArrayList;

public class User {

    private final String username;
    private final int id;
    private HashMap<String, ArrayList<String>> cart;

    public User(String username, int Id) {
        this.username = username;
        this.id = Id;
        this.cart = new HashMap<>();
    }
    
    public int getUserId() {
    	return this.id;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void clearCart() {
    	this.cart = new HashMap<>();
    }
    
    public HashMap<String, ArrayList<String>> getCart() {
    	return cart;
    }
    
    public String getItemName(String itemId) {
    	return cart.get(itemId).get(0);
    }
    
    public int getItemAmount(String itemId) {
    	return Integer.parseInt(cart.get(itemId).get(1));
    }
    
    public void changeAmount(String itemId, String itemName, int amount, int delta) {
    	if (amount != -1) {
    		if (amount != 0) {
    			ArrayList<String> i = new ArrayList<String>();
    			i.add(cart.get(itemId).get(0));
    			i.add(Integer.toString(amount));
    			cart.put(itemId, i);
    		}
    		else if (amount == 0)
    			cart.remove(itemId);
    	}
    	else if (delta != -1) {
    		if (cart.containsKey(itemId)) {
    			ArrayList<String> i = new ArrayList<String>();
    			i.add(cart.get(itemId).get(0));
    			int j = Integer.parseInt(cart.get(itemId).get(1)) + delta;
    			i.add(Integer.toString(j));
    			cart.put(itemId, i);
    		}
    		else {
    			ArrayList<String> i = new ArrayList<String>();
    			i.add(itemName);
    			i.add(Integer.toString(delta));
    			cart.put(itemId, i);
    		}
    	}
    	
    }

}
