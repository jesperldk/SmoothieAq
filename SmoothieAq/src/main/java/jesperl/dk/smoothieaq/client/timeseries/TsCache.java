package jesperl.dk.smoothieaq.client.timeseries;

import rx.*;
import rx.functions.*;

public abstract class TsCache<E extends TsElement> {
	
	/*friend*/ abstract E[] allocate(int size);
	
	Observable<E> search(long from, int count, Func1<E, Boolean> filter) {
		return null;
	}

}
