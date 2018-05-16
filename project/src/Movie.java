

public class Movie {

	private String id = "";
	private String title = "";
	private int year = -1;
	private String director = "";
	
	public Movie(){
		
	}
	
	public Movie(String id, String title, int year, String director) {
		this.id = id;
		this.title = title;
		this.year  = year;
		this.director = director;
		
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}


	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		//if (!director.equals(""))
		this.director = director;
	}	
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Movie Details - ");
		sb.append("MovieId:" + getId());
		sb.append(", ");
		sb.append("Title:" + getTitle());
		sb.append(", ");
		sb.append("Year:" + getYear());
		sb.append(", ");
		sb.append("Director:" + getDirector());
		
		return sb.toString();
	}
}
