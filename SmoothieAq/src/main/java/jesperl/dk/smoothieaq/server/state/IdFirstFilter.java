package jesperl.dk.smoothieaq.server.state;

import jesperl.dk.smoothieaq.shared.model.db.*;

public class  IdFirstFilter<DBO extends DbObject> extends FirstFilter<DBO> {
	private short max = 0;

	@Override protected Object getKey(DBO dbo) {
		Idable idable = (Idable) dbo;
		if (idable.getId() > max) max = idable.getId();
		return idable.getId();
	}

	public short getMax() { return max; }
}
