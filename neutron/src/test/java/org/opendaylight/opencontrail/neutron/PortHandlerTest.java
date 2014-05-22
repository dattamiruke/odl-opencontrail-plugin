/*
 * Copyright (C) 2014 Juniper Networks, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.opendaylight.opencontrail.neutron;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import net.juniper.contrail.api.ApiConnector;
import net.juniper.contrail.api.ApiPropertyBase;
import net.juniper.contrail.api.ObjectReference;
import net.juniper.contrail.api.types.InstanceIp;
import net.juniper.contrail.api.types.VirtualMachine;
import net.juniper.contrail.api.types.VirtualMachineInterface;
import net.juniper.contrail.api.types.VirtualNetwork;

import org.opendaylight.controller.networkconfig.neutron.NeutronPort;
import org.opendaylight.controller.networkconfig.neutron.Neutron_IPs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.easymock.PowerMock.expectNew;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendaylight.controller.networkconfig.neutron.NeutronNetwork;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test Class for Neutron Network.
 */
@RunWith(PowerMockRunner.class)
public class PortHandlerTest {
    PortHandler porthandler;
    PortHandler mockedporthandler = mock(PortHandler.class);
    NeutronNetwork mockedNeutronNetwork = mock(NeutronNetwork.class);
    ApiConnector mockedApiConnector = mock(ApiConnector.class);
    VirtualNetwork mockedVirtualNetwork = mock(VirtualNetwork.class);
    NeutronPort mockedNeutronPort = mock(NeutronPort.class);
    VirtualMachineInterface mockedVirtualMachineInterface = mock(VirtualMachineInterface.class);
    VirtualMachine mockedvirtualMachine = mock(VirtualMachine.class);
    Neutron_IPs mockNeutron_IPs = mock(Neutron_IPs.class);

    @Before
    public void beforeTest() {
        porthandler = new PortHandler();
        assertNotNull(mockedApiConnector);
        assertNotNull(mockedNeutronNetwork);
        assertNotNull(mockedVirtualNetwork);
        assertNotNull(mockedNeutronPort);
        assertNotNull(mockedVirtualMachineInterface);
        assertNotNull(mockedvirtualMachine);
        assertNotNull(mockNeutron_IPs);
    }

    @After
    public void AfterTest() {
        porthandler = null;
        Activator.apiConnector = null;
    }

    /* dummy params for Neutron Port */
    public NeutronPort defaultNeutronPortObject() {
        NeutronPort neutronPort = new NeutronPort();
        neutronPort.setPortUUID("64a271fe-0216-46bc-a3e6-1ff582fbd324");
        neutronPort.setNetworkUUID("54a271fe-0216-46bc-a3e6-1ff582fbd324");
        neutronPort.setMacAddress("02:70:72:93:4d:d6");
        neutronPort.setName("port12");
        neutronPort.setDeviceID("100071fe-0216-46bc-a3e6-1ff582fbd324");
        List<Neutron_IPs> ips = new ArrayList<Neutron_IPs>();
        ips.add(mockNeutron_IPs);
        neutronPort.setFixedIPs(ips);
        return neutronPort;
    }

    /* Test method to check if neutron port is null */
    @Test
    public void testCanCreatePortNull() {
        Activator.apiConnector = mockedApiConnector;
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, porthandler.canCreatePort(null));
    }

    /* Test method to check if neutron port name is null */
    @Test
    public void testCanCreatePortNameNull() {
        Activator.apiConnector = mockedApiConnector;
        when(mockedNeutronPort.getName()).thenReturn(null);
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, porthandler.canCreatePort(null));
    }

    /* Test method to check if neutron port name is empty */
    @Test
    public void testCanCreatePortNameEmtpy() {
        Activator.apiConnector = mockedApiConnector;
        when(mockedNeutronPort.getName()).thenReturn("");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, porthandler.canCreatePort(null));
    }

    /* Test method to check if neutron port device ID is null */
    @Test
    public void testCanCreateDeviceIdNull() {
        Activator.apiConnector = mockedApiConnector;
        when(mockedNeutronPort.getDeviceID()).thenReturn(null);
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, porthandler.canCreatePort(null));
    }

    /* Test method to check if neutron port device ID is empty */
    @Test
    public void testCanCreateDeviceIdEmtpy() {
        Activator.apiConnector = mockedApiConnector;
        when(mockedNeutronPort.getDeviceID()).thenReturn("");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, porthandler.canCreatePort(null));
    }

    /* Test method to check if neutron port device ID is null */
    @Test
    public void testCanCreateIdNull() {
        Activator.apiConnector = mockedApiConnector;
        when(mockedNeutronPort.getID()).thenReturn(null);
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, porthandler.canCreatePort(null));
    }

    /* Test method to check if neutron port device ID is empty */
    @Test
    public void testCanCreateIdEmtpy() {
        Activator.apiConnector = mockedApiConnector;
        when(mockedNeutronPort.getID()).thenReturn("");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, porthandler.canCreatePort(null));
    }

    /* Test method to check if neutron port fixed IP is null */
    @Test
    public void testCanCreateFixedIPNull() {
        Activator.apiConnector = mockedApiConnector;
        NeutronPort neutronPort = defaultNeutronPortObject();
        neutronPort.setFixedIPs(null);
        when(mockedNeutronPort.getFixedIPs()).thenReturn(null);
        assertEquals(HttpURLConnection.HTTP_FORBIDDEN, porthandler.canCreatePort(neutronPort));
    }

    /* Test method to check if neutron port is already exist */
    @Test
    public void testCanCreatePortExist() throws IOException {
        Activator.apiConnector = mockedApiConnector;
        NeutronPort neutronPort = defaultNeutronPortObject();
        when(mockedApiConnector.findById(VirtualMachineInterface.class, neutronPort.getID())).thenReturn(mockedVirtualMachineInterface);
        assertEquals(HttpURLConnection.HTTP_FORBIDDEN, porthandler.canCreatePort(neutronPort));
    }

    /* Test method to check if Virtual Machine Is Not Created */
    @PrepareForTest({ PortHandler.class, VirtualMachine.class })
    @Test
    public void testCanCreatePortVirtualMachineNotCreated() throws Exception {
        Activator.apiConnector = mockedApiConnector;
        NeutronPort neutronPort = defaultNeutronPortObject();
        VirtualMachine mockVirtualMachine = PowerMock.createNiceMock(VirtualMachine.class);
        expectNew(VirtualMachine.class).andReturn(mockVirtualMachine);
        when(mockedNeutronPort.getFixedIPs()).thenReturn(neutronPort.getFixedIPs());
        when(mockedApiConnector.findById(VirtualMachineInterface.class, neutronPort.getPortUUID())).thenReturn(null);
        when(mockedApiConnector.findById(VirtualMachine.class, neutronPort.getDeviceID())).thenReturn(null);
        when(mockedApiConnector.create(mockVirtualMachine)).thenReturn(false);
        PowerMock.replay(mockVirtualMachine, VirtualMachine.class);
        assertEquals(HttpURLConnection.HTTP_INTERNAL_ERROR, porthandler.canCreatePort(neutronPort));
    }

    /* Test method to check if Virtual Network does not exist */
    @Test
    public void testCanCreatePortVirtualNetworkNotExist() throws IOException {
        Activator.apiConnector = mockedApiConnector;
        NeutronPort neutronPort = defaultNeutronPortObject();
        when(mockedApiConnector.findById(VirtualMachineInterface.class, neutronPort.getID())).thenReturn(null);
        when(mockedApiConnector.findById(VirtualMachine.class, neutronPort.getDeviceID())).thenReturn(mockedvirtualMachine);
        when(mockedApiConnector.findById(VirtualNetwork.class, neutronPort.getNetworkUUID())).thenReturn(null);
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, porthandler.canCreatePort(neutronPort));
    }

    /* Test method to check if virtual machine interface creation failed */
    @PrepareForTest({ PortHandler.class, VirtualMachineInterface.class })
    @Test
    public void testCanCreateVirtualMachineInterfaceFail() throws Exception {
        Activator.apiConnector = mockedApiConnector;
        NeutronPort neutronPort = defaultNeutronPortObject();
        VirtualMachineInterface mockedVirtualMachineInterface = PowerMock.createNiceMock(VirtualMachineInterface.class);
        expectNew(VirtualMachineInterface.class).andReturn(mockedVirtualMachineInterface);
        when(mockedApiConnector.findById(VirtualMachineInterface.class, neutronPort.getID())).thenReturn(null);
        when(mockedApiConnector.findById(VirtualMachine.class, neutronPort.getDeviceID())).thenReturn(mockedvirtualMachine);
        when(mockedApiConnector.findById(VirtualNetwork.class, neutronPort.getNetworkUUID())).thenReturn(mockedVirtualNetwork);
        when(mockedApiConnector.create(mockedVirtualMachineInterface)).thenReturn(false);
        PowerMock.replay(mockedVirtualMachineInterface, VirtualMachineInterface.class);
        assertEquals(HttpURLConnection.HTTP_INTERNAL_ERROR, porthandler.canCreatePort(neutronPort));
    }

    /* Test method to check port is created */
    @PrepareForTest({ PortHandler.class, VirtualMachineInterface.class, InstanceIp.class })
    @Test
    public void testCanCreatePortOk() throws Exception {
        Activator.apiConnector = mockedApiConnector;
        NeutronPort neutronPort = defaultNeutronPortObject();
        VirtualMachineInterface mockedVirtualMachineInterface = PowerMock.createNiceMock(VirtualMachineInterface.class);
        expectNew(VirtualMachineInterface.class).andReturn(mockedVirtualMachineInterface);
        InstanceIp mockedInstanceIp = PowerMock.createNiceMock(InstanceIp.class);
        expectNew(InstanceIp.class).andReturn(mockedInstanceIp);
        when(mockedApiConnector.findById(VirtualMachineInterface.class, neutronPort.getPortUUID())).thenReturn(null);
        when(mockedApiConnector.findById(VirtualMachine.class, neutronPort.getDeviceID())).thenReturn(mockedvirtualMachine);
        when(mockedApiConnector.findById(VirtualNetwork.class, neutronPort.getNetworkUUID())).thenReturn(mockedVirtualNetwork);
        when(mockedApiConnector.create(mockedVirtualMachineInterface)).thenReturn(true);
        PowerMock.replay(mockedVirtualMachineInterface, VirtualMachineInterface.class);
        when(neutronPort.getFixedIPs().get(0).getIpAddress()).thenReturn("10.0.0.1");
        when(mockedApiConnector.create(mockedInstanceIp)).thenReturn(true);
        PowerMock.replay(mockedInstanceIp, InstanceIp.class);
        assertEquals(HttpURLConnection.HTTP_OK, porthandler.canCreatePort(neutronPort));
    }

    /* Test method to check if neutron port is null for delete */
    @Test
    public void testcanDeletePortNull() throws IOException {
        Activator.apiConnector = mockedApiConnector;
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, porthandler.canDeletePort(null));
    }

    /* Test method to check if virtual machine interface is null */
    @Test
    public void testcanDeletePortVirtualMachineInterfaceNull() throws IOException {
        Activator.apiConnector = mockedApiConnector;
        NeutronPort neutronPort = defaultNeutronPortObject();
        when(mockedApiConnector.findById(VirtualMachineInterface.class, neutronPort.getID())).thenReturn(null);
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, porthandler.canDeletePort(neutronPort));
    }

    /* Test method to check port delete with instanceIPs null */
    @Test
    public void testcanDeletePortOK() throws IOException {
        Activator.apiConnector = mockedApiConnector;
        NeutronPort neutronPort = defaultNeutronPortObject();
        when(mockedApiConnector.findById(VirtualMachineInterface.class, neutronPort.getID())).thenReturn(mockedVirtualMachineInterface);
        when(mockedVirtualMachineInterface.getInstanceIpBackRefs()).thenReturn(null);
        assertEquals(HttpURLConnection.HTTP_OK, porthandler.canDeletePort(neutronPort));
    }

    /* Test method to check port delete with instanceIPs not null */
    @Test
    public void testcanDeletePortOKInstanceIPsNotNull() throws IOException {
        Activator.apiConnector = mockedApiConnector;
        NeutronPort neutronPort = defaultNeutronPortObject();
        when(mockedApiConnector.findById(VirtualMachineInterface.class, neutronPort.getID())).thenReturn(mockedVirtualMachineInterface);
        List<ObjectReference<ApiPropertyBase>> instanceIPs = new ArrayList<ObjectReference<ApiPropertyBase>>();
        when(mockedVirtualMachineInterface.getInstanceIpBackRefs()).thenReturn(instanceIPs);
        assertEquals(HttpURLConnection.HTTP_OK, porthandler.canDeletePort(neutronPort));
    }
}
