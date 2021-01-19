/**
 * WorkflowServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package cn.com.weaver.services.webservices;

public interface WorkflowServicePortType extends java.rmi.Remote {
    public boolean deleteRequest(int in0, int in1) throws java.rmi.RemoteException;
    public String submitWorkflowRequest(weaver.workflow.webservices.WorkflowRequestInfo in0, int in1, int in2, String in3, String in4) throws java.rmi.RemoteException;
    public int getToDoWorkflowRequestCount(int in0, String[] in1) throws java.rmi.RemoteException;
    public String doCreateWorkflowRequest(weaver.workflow.webservices.WorkflowRequestInfo in0, int in1) throws java.rmi.RemoteException;
    public weaver.workflow.webservices.WorkflowRequestInfo getCreateWorkflowRequestInfo(int in0, int in1) throws java.rmi.RemoteException;
    public weaver.workflow.webservices.WorkflowRequestInfo[] getAllWorkflowRequestList(int in0, int in1, int in2, int in3, String[] in4) throws java.rmi.RemoteException;
    public weaver.workflow.webservices.WorkflowRequestInfo[] getMyWorkflowRequestList(int in0, int in1, int in2, int in3, String[] in4) throws java.rmi.RemoteException;
    public int getProcessedWorkflowRequestCount(int in0, String[] in1) throws java.rmi.RemoteException;
    public weaver.workflow.webservices.WorkflowRequestInfo getWorkflowRequest(int in0, int in1, int in2) throws java.rmi.RemoteException;
    public String getLeaveDays(String in0, String in1, String in2, String in3, String in4) throws java.rmi.RemoteException;
    public weaver.workflow.webservices.WorkflowRequestInfo getWorkflowRequest4Split(int in0, int in1, int in2, int in3) throws java.rmi.RemoteException;
    public weaver.workflow.webservices.WorkflowRequestInfo[] getHendledWorkflowRequestList(int in0, int in1, int in2, int in3, String[] in4) throws java.rmi.RemoteException;
    public int getCreateWorkflowCount(int in0, int in1, String[] in2) throws java.rmi.RemoteException;
    public weaver.workflow.webservices.WorkflowRequestInfo[] getToDoWorkflowRequestList(int in0, int in1, int in2, int in3, String[] in4) throws java.rmi.RemoteException;
    public String[] getWorkflowNewFlag(String[] in0, String in1) throws java.rmi.RemoteException;
    public weaver.workflow.webservices.WorkflowRequestInfo[] getCCWorkflowRequestList(int in0, int in1, int in2, int in3, String[] in4) throws java.rmi.RemoteException;
    public weaver.workflow.webservices.WorkflowBaseInfo[] getCreateWorkflowList(int in0, int in1, int in2, int in3, int in4, String[] in5) throws java.rmi.RemoteException;
    public int getCCWorkflowRequestCount(int in0, String[] in1) throws java.rmi.RemoteException;
    public int getAllWorkflowRequestCount(int in0, String[] in1) throws java.rmi.RemoteException;
    public weaver.workflow.webservices.WorkflowRequestLog[] getWorkflowRequestLogs(String in0, String in1, int in2, int in3, int in4) throws java.rmi.RemoteException;
    public int getMyWorkflowRequestCount(int in0, String[] in1) throws java.rmi.RemoteException;
    public weaver.workflow.webservices.WorkflowRequestInfo[] getProcessedWorkflowRequestList(int in0, int in1, int in2, int in3, String[] in4) throws java.rmi.RemoteException;
    public int getCreateWorkflowTypeCount(int in0, String[] in1) throws java.rmi.RemoteException;
    public String forwardWorkflowRequest(int in0, String in1, String in2, int in3, String in4) throws java.rmi.RemoteException;
    public void writeWorkflowReadFlag(String in0, String in1) throws java.rmi.RemoteException;
    public int getHendledWorkflowRequestCount(int in0, String[] in1) throws java.rmi.RemoteException;
    public weaver.workflow.webservices.WorkflowBaseInfo[] getCreateWorkflowTypeList(int in0, int in1, int in2, int in3, String[] in4) throws java.rmi.RemoteException;
}
