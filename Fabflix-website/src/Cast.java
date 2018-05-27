
public class Cast {

	private String id = "";
	private String name = "";
	
	public Cast(){
		
	}
	
	public Cast(String id, String name) {
		this.id = id;
		this.name = name;		
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


	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Cast Details - ");
		sb.append("xml movie Id:" + getId());
		sb.append(", ");
		sb.append("stagename:" + getName());
		sb.append(".");
		
		return sb.toString();
	}
}

