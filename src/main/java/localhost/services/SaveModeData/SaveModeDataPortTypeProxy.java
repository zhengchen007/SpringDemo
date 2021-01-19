package localhost.services.SaveModeData;

public class SaveModeDataPortTypeProxy implements localhost.services.SaveModeData.SaveModeDataPortType {
  private String _endpoint = null;
  private localhost.services.SaveModeData.SaveModeDataPortType saveModeDataPortType = null;
  
  public SaveModeDataPortTypeProxy() {
    _initSaveModeDataPortTypeProxy();
  }
  
  public SaveModeDataPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initSaveModeDataPortTypeProxy();
  }
  
  private void _initSaveModeDataPortTypeProxy() {
    try {
      saveModeDataPortType = (new localhost.services.SaveModeData.SaveModeDataLocator()).getSaveModeDataHttpPort();
      if (saveModeDataPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)saveModeDataPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)saveModeDataPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (saveModeDataPortType != null)
      ((javax.xml.rpc.Stub)saveModeDataPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public localhost.services.SaveModeData.SaveModeDataPortType getSaveModeDataPortType() {
    if (saveModeDataPortType == null)
      _initSaveModeDataPortTypeProxy();
    return saveModeDataPortType;
  }
  
  public int createYxb(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException{
    if (saveModeDataPortType == null)
      _initSaveModeDataPortTypeProxy();
    return saveModeDataPortType.createYxb(in0, in1, in2, in3);
  }
  
  
}