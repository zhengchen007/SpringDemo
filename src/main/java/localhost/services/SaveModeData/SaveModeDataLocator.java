/**
 * SaveModeDataLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package localhost.services.SaveModeData;

public class SaveModeDataLocator extends org.apache.axis.client.Service implements localhost.services.SaveModeData.SaveModeData {

    public SaveModeDataLocator() {
    }


    public SaveModeDataLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SaveModeDataLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SaveModeDataHttpPort
    private java.lang.String SaveModeDataHttpPort_address = "http://192.168.2.83//services/SaveModeData";

    public java.lang.String getSaveModeDataHttpPortAddress() {
        return SaveModeDataHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SaveModeDataHttpPortWSDDServiceName = "SaveModeDataHttpPort";

    public java.lang.String getSaveModeDataHttpPortWSDDServiceName() {
        return SaveModeDataHttpPortWSDDServiceName;
    }

    public void setSaveModeDataHttpPortWSDDServiceName(java.lang.String name) {
        SaveModeDataHttpPortWSDDServiceName = name;
    }

    public localhost.services.SaveModeData.SaveModeDataPortType getSaveModeDataHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SaveModeDataHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSaveModeDataHttpPort(endpoint);
    }

    public localhost.services.SaveModeData.SaveModeDataPortType getSaveModeDataHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            localhost.services.SaveModeData.SaveModeDataHttpBindingStub _stub = new localhost.services.SaveModeData.SaveModeDataHttpBindingStub(portAddress, this);
            _stub.setPortName(getSaveModeDataHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSaveModeDataHttpPortEndpointAddress(java.lang.String address) {
        SaveModeDataHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (localhost.services.SaveModeData.SaveModeDataPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                localhost.services.SaveModeData.SaveModeDataHttpBindingStub _stub = new localhost.services.SaveModeData.SaveModeDataHttpBindingStub(new java.net.URL(SaveModeDataHttpPort_address), this);
                _stub.setPortName(getSaveModeDataHttpPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SaveModeDataHttpPort".equals(inputPortName)) {
            return getSaveModeDataHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://localhost/services/SaveModeData", "SaveModeData");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://localhost/services/SaveModeData", "SaveModeDataHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SaveModeDataHttpPort".equals(portName)) {
            setSaveModeDataHttpPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
