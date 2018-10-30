/**
 * 
 */
package utility;

/**
 * @author jam
 *
 */
public interface Locker {
	public Permit getReadPermit();
	public Permit getWritePermit();
}
