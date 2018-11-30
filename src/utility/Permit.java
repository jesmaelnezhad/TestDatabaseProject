package utility;

import java.util.concurrent.locks.Lock;

public class Permit {
	public Lock lock;
	public Permit more= null;
	public Permit(Lock lock) {
		this.lock = lock;
		this.lock.lock();
	}
	public Permit(Lock lock, Permit permit) {
		this.lock = lock;
		this.lock.lock();
		this.more = permit;
	}
	public void unlock() {
		lock.unlock();
		if(more != null) {
			more.unlock();
		}
	}
}
