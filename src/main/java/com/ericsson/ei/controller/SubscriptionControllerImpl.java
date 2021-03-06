/***********************************************************************
 *                                                                     *
 * Copyright Ericsson AB 2017                                          *
 *                                                                     * 
 * No part of this software may be reproduced in any form without the  *   
 * written permission of the copyright owner.                          *             
 *                                                                     *
 ***********************************************************************/
package com.ericsson.ei.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.ericsson.ei.controller.model.Subscription;
import com.ericsson.ei.controller.model.SubscriptionResponse;
import com.ericsson.ei.exception.SubscriptionNotFoundException;
import com.ericsson.ei.services.ISubscriptionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@CrossOrigin
@Api(value = "subscription", description = "The Subscription API for the store and retrieve the subscriptions from the database")
public class SubscriptionControllerImpl implements SubscriptionController {
    
    @Autowired
    private ISubscriptionService subscriptionService;
    
    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionControllerImpl.class);
    
    @Override
    @CrossOrigin
    @ApiOperation(value = "Creates the subscription")
    public ResponseEntity<SubscriptionResponse> createSubscription(@RequestBody Subscription subscription) {
        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        if (!subscriptionService.doSubscriptionExist(subscription.getSubscriptionName())) {
            subscriptionService.addSubscription(subscription);
            LOG.info("Subscription :" + subscription.getSubscriptionName() + " Inserted Successfully");
            subscriptionResponse.setMsg("Inserted Successfully"); subscriptionResponse.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<SubscriptionResponse>(subscriptionResponse, HttpStatus.OK);
        } else {
            LOG.error("Subscription :" + subscription.getSubscriptionName() + " identified as duplicate subscription");
            subscriptionResponse.setMsg("Duplicate Subscription"); subscriptionResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<SubscriptionResponse>(subscriptionResponse, HttpStatus.BAD_REQUEST);
        }
        
    }
    
    @Override
    @CrossOrigin
    @ApiOperation(value = "Returns the subscription rules for given subscription name")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable String subscriptionName) {
        Subscription subscription = null;
        try {
            LOG.info("Subscription :" + subscriptionName + " fetch started");
            subscription = subscriptionService.getSubscription(subscriptionName);
            LOG.info("Subscription :" + subscriptionName + " fetched");
            return new ResponseEntity<Subscription>(subscription, HttpStatus.OK);
        } catch (SubscriptionNotFoundException e) {
            LOG.error("Subscription :" + subscriptionName + " not found in records");
            return new ResponseEntity<Subscription>(subscription, HttpStatus.NOT_FOUND);
        }
        
    }
    
    @Override
    @CrossOrigin
    @ApiOperation(value = "Update the existing subscription by the subscription name")
    public ResponseEntity<SubscriptionResponse> updateSubscriptionById(@PathVariable String subscriptionName, @RequestBody Subscription subscription) {
        LOG.info("Subscription :" + subscriptionName + " update started");
        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        if (subscriptionService.doSubscriptionExist(subscription.getSubscriptionName())) {
            subscriptionService.modifySubscription(subscription, subscriptionName);
            LOG.info("Subscription :" + subscriptionName + " update completed");
            subscriptionResponse.setMsg("Updated Successfully"); subscriptionResponse.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<SubscriptionResponse>(subscriptionResponse, HttpStatus.OK);
            
        } else {
            LOG.error("Subscription :" + subscription.getSubscriptionName() + " can't be found.");
            subscriptionResponse.setMsg("Subscription can't be found"); subscriptionResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<SubscriptionResponse>(subscriptionResponse, HttpStatus.BAD_REQUEST);
        }
        
    }
    
    @Override
    @CrossOrigin
    @ApiOperation(value = "Removes the subscription from the database")
    public ResponseEntity<SubscriptionResponse> deleteSubscriptionById(@PathVariable String subscriptionName) {
        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        LOG.info("Subscription :" + subscriptionName + " delete started");
        if (subscriptionService.deleteSubscription(subscriptionName)) {
            LOG.info("Subscription :" + subscriptionName + " deleted Successfully");
            subscriptionResponse.setMsg("Deleted Successfully"); subscriptionResponse.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<SubscriptionResponse>(subscriptionResponse, HttpStatus.OK);
        } else {
            LOG.info("Subscription :" + subscriptionName + " delete completed :: Record not found for delete");
            subscriptionResponse.setMsg("Record not found for delete"); subscriptionResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<SubscriptionResponse>(subscriptionResponse, HttpStatus.BAD_REQUEST);
        }
        
    }
    
    @Override
    @CrossOrigin
    @ApiOperation(value = "Retrieve all the subscriptions")
    public ResponseEntity<List<Subscription>> getSubscriptions() {
        LOG.info("Subscription : get all records started");
        List<Subscription> subscriptionList = new ArrayList<Subscription>();
        try {
            subscriptionList = subscriptionService.getSubscription();
            return new ResponseEntity<List<Subscription>>(subscriptionList, HttpStatus.OK);
        } catch (SubscriptionNotFoundException e) {
            LOG.error(e.getLocalizedMessage());
            return new ResponseEntity<List<Subscription>>(subscriptionList, HttpStatus.NOT_FOUND);
        }
    }
}
