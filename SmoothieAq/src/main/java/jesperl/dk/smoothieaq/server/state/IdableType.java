package jesperl.dk.smoothieaq.server.state;

import jesperl.dk.smoothieaq.shared.model.db.*;

public abstract class  IdableType implements Idable {
	
	private int id = 0;
	private String name;

	@Override
	public void setId(int id) {
		assert this.id == 0;
		this.id = id;
	}
	
	@Override
	public int getId() {
		assert id != 0;
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass().equals(this.getClass()) && id != 0 && id == ((IdableType)obj).getId();
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder(); 
		string.append(getClass().getSimpleName());
		string.append("#");
		string.append(getId());
		if (getName() != null) {
			string.append("/");
			string.append(getName());
		}
		return string.toString();
	}
	
	@Override
	public int hashCode() {
		assert id != 0;
		return id;
	}

}
