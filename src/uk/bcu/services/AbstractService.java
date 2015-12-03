/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.bcu.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Yacub
 */
public abstract class AbstractService implements Serializable, Runnable {
    private ArrayList<ServiceListener> listeners ;
    private boolean error;
    
        public AbstractService() {
        listeners = new ArrayList<ServiceListener>();
    }
    
    public void addListener(ServiceListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(ServiceListener listener) {
        listeners.remove(listener);
    }
    
    public boolean hasError() {
        return error;
    }
    
    public void serviceComplete(boolean error) {
        this.error = error;
        
        Message m =_handler.obtainMessage();
        Bundle b = new Bundle();
        b.putSerializable("service", this);
        m.setData(b);
        _handler.sendMessage(m);
    }
    
    final Handler _handler = new Handler() {
        @Override
        
        public void handleMessage(Message msg) {
            AbstractService service =
                    (AbstractService)msg.getData().getSerializable("service");
            
            for (ServiceListener listener : service.listeners) {
                listener.ServiceComplete(service);
                
            }
        }
    };
}
