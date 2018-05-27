
public class Star {

	private String id;
	private String name;
	private int birth;
	
	public Star(){
		
	}
	
	public Star(String id, String name, int birth) {
		this.id = id;
		this.name = name;
		this.birth  = birth;		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBirth() {
		return birth;
	}

	public void setBirth(int birth) {
		this.birth = birth;
	}	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Star Details - ");
		sb.append("id:" + getId());
		sb.append(", ");
		sb.append("name:" + getName());
		sb.append(", ");
		sb.append("birth:" + getBirth());
		sb.append(".");
		
		return sb.toString();
	}
}
