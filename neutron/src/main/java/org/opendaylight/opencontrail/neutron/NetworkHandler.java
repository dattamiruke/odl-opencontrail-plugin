/*
 * Copyright (C) 2014 Juniper Networks, Inc.
 */

package org.opendaylight.opencontrail.neutron;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.UUID;

import net.juniper.contrail.api.ApiConnector;
import net.juniper.contrail.api.types.VirtualNetwork;

import org.opendaylight.controller.networkconfig.neutron.INeutronNetworkAware;
import org.opendaylight.controller.networkconfig.neutron.NeutronNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle requests for Neutron Network.
 */
public class NetworkHandler implements INeutronNetworkAware {

/**
 * Logger instance.
 */
     static final Logger LOGGER = LoggerFactory.getLogger(NetworkHandler.class);
     static ApiConnector apiConnector;

   /**
    * Invoked when a network creation is requested to check if the specified
    * network can be created and then creates the network
    *
    * @param network
    * An instance of proposed new Neutron Network object.
    *
    * @return A HTTP status code to the creation request.
     */
     @Override
     public int canCreateNetwork(NeutronNetwork network) {
         if (network == null){
              LOGGER.error("Network object can't be null..");
              return HttpURLConnection.HTTP_BAD_REQUEST;
         }

         if (network.getShared() == null) {
              LOGGER.info("Network shared attribute not available in request..");
              return HttpURLConnection.HTTP_BAD_REQUEST;
         }

         if (network.isShared()) {
              LOGGER.info("Network shared attribute not supported ");
              return HttpURLConnection.HTTP_NOT_ACCEPTABLE;
         }

         LOGGER.debug("Network object " + network);

         apiConnector = Activator.apiConnector;

         if(network.getNetworkUUID() == null || network.getNetworkName() == null || network.getNetworkUUID().equals("")) {
             LOGGER.error("Network UUID and Network Name can't be null/empty...");
             return HttpURLConnection.HTTP_BAD_REQUEST;
         }
         try{
             return createNetwork(network);
         }
         catch(IOException ie){
             LOGGER.error("IOException :   "+ie);
             return HttpURLConnection.HTTP_INTERNAL_ERROR;
         }
         catch(Exception e){
             LOGGER.error("Exception :   "+e);
             return HttpURLConnection.HTTP_INTERNAL_ERROR;
         }

     }

  /**
   * Invoked to take action after a network has been created.
   *
   * @param network
   *            An instance of new Neutron Network object.
   */
      @Override
      public void neutronNetworkCreated(NeutronNetwork network) {
          VirtualNetwork virtualNetwork = null;
          try{
          virtualNetwork = (VirtualNetwork) apiConnector.findById(VirtualNetwork.class, network.getNetworkUUID());
          if(virtualNetwork != null)
          {
               LOGGER.info("Network creation verified....");
          }
          }
          catch(Exception e){
               LOGGER.error("Exception :    "+e);
          }
      }

      /**
       * Invoked to create the specified Neutron Network.
       *
       * @param network
       *            An instance of new Neutron Network object.
       *
       * @return A HTTP status code to the creation request.
       */
      private int createNetwork(NeutronNetwork network) throws IOException{
           VirtualNetwork virtualNetwork = null;
           String networkUUID=null;
           try{
               networkUUID=UUID.fromString(network.getNetworkUUID()).toString();
           }catch(Exception ex){
               LOGGER.error("networkUUID input incorrect",ex);
               return HttpURLConnection.HTTP_BAD_REQUEST;
           }
           virtualNetwork = (VirtualNetwork) apiConnector.findById(VirtualNetwork.class, networkUUID);
           if(virtualNetwork != null)
           {
               LOGGER.warn("Network already exists..");
               return HttpURLConnection.HTTP_FORBIDDEN;
           }
           virtualNetwork = new VirtualNetwork();
           // map neutronNetwork to virtualNetwork
           virtualNetwork = mapNetworkProperties(network, virtualNetwork);
           boolean networkCreated = apiConnector.create(virtualNetwork);
           LOGGER.debug("networkCreated:   "+networkCreated);
           if (!networkCreated) {
               LOGGER.warn("Network creation failed..");
               return HttpURLConnection.HTTP_INTERNAL_ERROR;
           }
           LOGGER.info("Network : " + virtualNetwork.getName() +
                      "  having UUID : " + virtualNetwork.getUuid() +
                      "  sucessfully created...");
           return HttpURLConnection.HTTP_OK;
      }


   /**
   * Invoked to map the NeutronNetwork object properties to the virtualNetwork object.
   * @param neutronNetwork
   *        An instance of new Neutron Network object.
   * @param virtualNetwork
   *        An instance of new virtualNetwork object.
   * @return {@link VirtualNetwork}
   */
      private VirtualNetwork mapNetworkProperties(NeutronNetwork neutronNetwork,VirtualNetwork virtualNetwork) {
           String networkUUID = neutronNetwork.getNetworkUUID();
           String netWorkname = neutronNetwork.getNetworkName();
           virtualNetwork.setName(netWorkname);
           virtualNetwork.setUuid(networkUUID);
           return virtualNetwork;
      }


  /**
    * Invoked when a network update is requested to indicate if the specified
    * network can be changed using the specified delta.
    * @param delta
    *        Updates to the network object using patch semantics.
    * @param original
    *        An instance of the Neutron Network object to be updated.
    *  @return A HTTP status code to the update request.
    */
      @Override
      public int canUpdateNetwork(NeutronNetwork delta, NeutronNetwork original) {
          return HttpURLConnection.HTTP_OK;
      }

   /**
    * Invoked to take action after a network has been updated.
    * @param network
    *            An instance of modified Neutron Network object.
    */
      @Override
      public void neutronNetworkUpdated(NeutronNetwork network) {
           return;
      }

   /**
     * Invoked when a network deletion is requested to indicate if the specified
     * network can be deleted.
     *  @param network
     *         An instance of the Neutron Network object to be deleted.
     *  @return A HTTP status code to the deletion request.
     */
      @Override
     public int canDeleteNetwork(NeutronNetwork network) {
          //TODO dummy method-functionality needs to be implemented
          return HttpURLConnection.HTTP_OK;
     }

    /**
     * Invoked to take action after a network has been deleted.
     * @param network
     *            An instance of deleted Neutron Network object.
     */
      @Override
      public void neutronNetworkDeleted(NeutronNetwork network) {
      //TODO dummy method-functionality needs to be implemented
           int result = canDeleteNetwork(network);
           if (result != HttpURLConnection.HTTP_OK) {
                LOGGER.error(" deleteNetwork validation failed for result - {} ",result);
                return;
           }
      }

}