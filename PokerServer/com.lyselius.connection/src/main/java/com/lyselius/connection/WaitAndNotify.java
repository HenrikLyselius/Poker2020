package com.lyselius.connection;

public class WaitAndNotify {

    private Monitor monitor = new Monitor();
    private boolean wasSignalled = false;



    public void doWait()
    {
        synchronized (monitor)
        {
            if(!wasSignalled)
            {
                try
                {
                    monitor.wait();
                }
                catch(InterruptedException e) {}
            }
            wasSignalled = false;
        }
    }



    public void doNotify()
    {
        synchronized (monitor)
        {
            wasSignalled = true;
            monitor.notify();
        }
    }
}
