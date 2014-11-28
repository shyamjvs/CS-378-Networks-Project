package proxy;

public class QElement implements Comparable<QElement> {

    private ProxyThread proxythread;
    private int priority;

    public QElement(ProxyThread proxythread, int priority) {
        this.proxythread = proxythread;
        this.priority = priority;
    }

    public ProxyThread getproxythread() {
        return proxythread;
    }

    public int getpriority() {
        return priority;
    }

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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97*hash + (this.proxythread != null ? this.proxythread.hashCode() : 0);
        hash = 97*hash + this.priority;
        return hash;
    }

    @Override
    public int compareTo(QElement i) {
        if (this.priority == i.priority) {
            return this.proxythread.toString().compareTo(i.proxythread.toString());
        }

        return this.priority - i.priority;
    }

    @Override
    public String toString() {
        return String.format("%s: $%d", proxythread, priority);
    }      
  
}

