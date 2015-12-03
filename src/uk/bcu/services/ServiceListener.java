/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.bcu.services;

/**
 *
 * @author Yacub
 */

//when a service has completed or failed.
public interface ServiceListener {
        public void ServiceComplete(AbstractService service);
}
