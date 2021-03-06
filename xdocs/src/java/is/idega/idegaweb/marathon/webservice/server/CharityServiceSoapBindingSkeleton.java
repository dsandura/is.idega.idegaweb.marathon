/**
 * CharityServiceSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package is.idega.idegaweb.marathon.webservice.server;

public class CharityServiceSoapBindingSkeleton implements is.idega.idegaweb.marathon.webservice.server.CharityService_PortType, org.apache.axis.wsdl.Skeleton {
    private is.idega.idegaweb.marathon.webservice.server.CharityService_PortType impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "personalID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getCharityInformation", _params, new javax.xml.namespace.QName("", "getCharityInformationReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://illuminati.is", "CharityInformation"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://illuminati.is", "getCharityInformation"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getCharityInformation") == null) {
            _myOperations.put("getCharityInformation", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getCharityInformation")).add(_oper);
    }

    public CharityServiceSoapBindingSkeleton() {
        this.impl = new is.idega.idegaweb.marathon.webservice.server.CharityServiceSoapBindingImpl();
    }

    public CharityServiceSoapBindingSkeleton(is.idega.idegaweb.marathon.webservice.server.CharityService_PortType impl) {
        this.impl = impl;
    }
    public is.idega.idegaweb.marathon.webservice.server.CharityInformation getCharityInformation(java.lang.String personalID) throws java.rmi.RemoteException
    {
        is.idega.idegaweb.marathon.webservice.server.CharityInformation ret = impl.getCharityInformation(personalID);
        return ret;
    }

}
