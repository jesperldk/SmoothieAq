package jesperl.dk.smoothieaq.server.state;

import java.util.*;

import jesperl.dk.smoothieaq.shared.model.db.*;
import rx.functions.*;

public abstract class  FirstFilter<DBO extends DbObject> implements Func1<DBO, Boolean> {
	private Set<Object> keys = new HashSet<>();

	@Override public Boolean call(DBO dbo) {
		Object key = getKey(dbo);
		if (!keys.contains(key)) {
			keys.add(key);
			return true;
		}
		return false;
	}

	protected abstract Object getKey(DBO dbo);

}
