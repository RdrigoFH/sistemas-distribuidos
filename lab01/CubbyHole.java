public class CubbyHole {
    private int contents;   // el datos
    private boolean available = false;  // si hay dato

    // synchronized hace que solo un hilo pueda usar el objeto a la vez
    public synchronized int get() {
        while (available == false) {
            try {
                wait();
            } catch (InterruptedException e) { }
        }
        available = false;
        notifyAll();
        return contents;
    }

    public synchronized void put(int value) {
        // no escribre por que hay dato
        while (available == true) {
            try {
                wait();
            } catch (InterruptedException e) { }
        }
        // escribe el value y lo marca como ocupado
        contents = value;
        available = true;
        notifyAll();
    }
}
