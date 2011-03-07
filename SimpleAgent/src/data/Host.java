/**
 * 
 */
package data;

/**
 * @author sebastian
 * Represents a unique network identifier.
 */
public class Host
{
    String hostName;
    Integer portNumber;
    
    /**
     * Creates a Host.
     * @param newhost The network name, e.g. "localhost" or "192.168.2.1"
     * @param newPort The network (socket) port on the given host.
     */
    public Host(String newhost, int newPort)
    {
        hostName = newhost;
        portNumber = newPort;
    }
    
    /**
     * @return the hostName
     */
    public String getHostName() {
        return hostName;
    }
    /**
     * @param hostName the hostName to set
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    /**
     * @return the portNumber
     */
    public Integer getPortNumber() {
        return portNumber;
    }
    /**
     * @param portNumber the portNumber to set
     */
    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }
}
