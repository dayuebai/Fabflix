import java.util.ArrayList;

public class Movie {

	private String id = "";
	private String title = "";
	private int year = -1;
	private String director = "";
	private ArrayList<String> genres = new ArrayList<String>();
	
	public Movie(){
		
	}
	
	public Movie(String id, String title, int year, String director) {
		this.id = id;
		this.title = title;
		this.year  = year;
		this.director = director;
		
	}
	
	public boolean satisfy_requirement() {
		return !( this.id.equals("") || this.title.equals("") || this.year == -1);	
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
	
	public ArrayList<String> getGenres() {
		return genres;
	}

	public void addGenre(String genre) throws Exception {
		if (!genres.contains(genre))
			this.genres.add(genre);
		else
			throw new Exception();
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
		sb.append(", ");
		sb.append("Genres: " + genres.toString());
		return sb.toString();
	}
}
