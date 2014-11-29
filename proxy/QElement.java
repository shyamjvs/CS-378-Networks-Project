package proxy;

// This class represents a particular element of the request queue(buffer) at the proxy server
public class QElement implements Comparable<QElement> {

    private ProxyThread proxythread;
    private int priority;

    // Constructor for the class
    public QElement(ProxyThread proxythread, int priority) {
        this.proxythread = proxythread;
        this.priority = priority;
    }

    // Returns the proxy thread
    public ProxyThread getproxythread() {
        return proxythread;
    }

    // Returns the thread's priority
    public int getpriority() {
        return priority;
    }

    // Overloaded operator equals
    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QElement other = (QElement) obj;
        if ((this.proxythread == null) ? (other.proxythread != null) : !this.proxythread.equals(other.proxythread)) {
            return false;
        }
        if (this.priority != other.priority) {
            return false;
        }
        return true;
    }

    // Hash function
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97*hash + (this.proxythread != null ? this.proxythread.hashCode() : 0);
        hash = 97*hash + this.priority;
        return hash;
    }

    // Comparison operator
    @Override
    public int compareTo(QElement i) {
        if (this.priority == i.priority) {
            return this.proxythread.toString().compareTo(i.proxythread.toString());
        }

        return this.priority - i.priority;
    }

    // Print the thread + its priority in string format
    @Override
    public String toString() {
        return String.format("%s: $%d", proxythread, priority);
    }      
  
}

