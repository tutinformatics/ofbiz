package ee.ttu.objectdistribution.services;

import com.sun.net.httpserver.HttpServer;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.common.FindServices;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.service.*;
import org.apache.ofbiz.service.jms.JmsListenerFactory;
import org.apache.ofbiz.service.job.JobManager;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ObjectDistributionServices {

    public static final String module = ObjectDistributionServices.class.getName();

    public Map<String, Object> createObjectDistribution(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        try {
            Debug.log("[DEBUG]: " + context.toString() + context.keySet() + Arrays.toString(context.values().toArray()));
            GenericValue objectDistribution = delegator.makeValue("ObjectDistribution");
            // Auto generating next sequence of ObjectDistributionId primary key
            objectDistribution.setNextSeqId();
            // Setting up all non primary key field values from context map
            objectDistribution.setNonPKFields(context);
            // Creating record in database for ObjectDistribution entity for prepared value
            objectDistribution = delegator.create(objectDistribution);
            result.put("ObjectDistributionId", objectDistribution.getString("ObjectDistributionId"));

            Debug.log("Started ObjectDistributionService...");
            getData();
            DataTransformer dataTransformer = new DataTransformer();
            dataTransformer.addPublisher();
            dataTransformer.getSubscriptionData();
            search("ARVED");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            Debug.log("Starting ObjectDistributionService Failed...");
            return ServiceUtil.returnError("Error in creating record in ObjectDistribution entity ........" + module);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void getData() throws IOException {
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("President of the United States", "God-Emperor Trump");
        server.createContext("/api/hello", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                String respText = jsonObject.toJSONString();
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            exchange.close();
        }));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public Map<String, Object> search(String topic) {
        ClassLoader classLoader = new ClassLoader() {
            @Override
            public String getName() {
                return super.getName();
            }
        };
        LocalDispatcher localDispatcher = new LocalDispatcher() {
            @Override
            public void disableEcas() {

            }

            @Override
            public void enableEcas() {

            }

            @Override
            public boolean isEcasDisabled() {
                return false;
            }

            @Override
            public Map<String, Object> runSync(String serviceName, Map<String, ?> context) throws GenericServiceException {
                return null;
            }

            @Override
            public Map<String, Object> runSync(String serviceName, Map<String, ?> context, int transactionTimeout, boolean requireNewTransaction) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
                return null;
            }

            @Override
            public Map<String, Object> runSync(String serviceName, int transactionTimeout, boolean requireNewTransaction, Object... context) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
                return null;
            }

            @Override
            public void runSyncIgnore(String serviceName, Map<String, ?> context) throws GenericServiceException {

            }

            @Override
            public void runSyncIgnore(String serviceName, Map<String, ?> context, int transactionTimeout, boolean requireNewTransaction) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

            }

            @Override
            public void runSyncIgnore(String serviceName, int transactionTimeout, boolean requireNewTransaction, Object... context) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

            }

            @Override
            public void runAsync(String serviceName, Map<String, ?> context, GenericRequester requester, boolean persist, int transactionTimeout, boolean requireNewTransaction) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

            }

            @Override
            public void runAsync(String serviceName, GenericRequester requester, boolean persist, int transactionTimeout, boolean requireNewTransaction, Object... context) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

            }

            @Override
            public void runAsync(String serviceName, Map<String, ?> context, GenericRequester requester, boolean persist) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

            }

            @Override
            public void runAsync(String serviceName, GenericRequester requester, boolean persist, Object... context) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

            }

            @Override
            public void runAsync(String serviceName, Map<String, ?> context, GenericRequester requester) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

            }

            @Override
            public void runAsync(String serviceName, GenericRequester requester, Object... context) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

            }

            @Override
            public void runAsync(String serviceName, Map<String, ?> context, boolean persist) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

            }

            @Override
            public void runAsync(String serviceName, boolean persist, Object... context) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

            }

            @Override
            public void runAsync(String serviceName, Map<String, ?> context) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

            }

            @Override
            public GenericResultWaiter runAsyncWait(String serviceName, Map<String, ?> context, boolean persist) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
                return null;
            }

            @Override
            public GenericResultWaiter runAsyncWait(String serviceName, boolean persist, Object... context) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
                return null;
            }

            @Override
            public GenericResultWaiter runAsyncWait(String serviceName, Map<String, ?> context) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
                return null;
            }

            @Override
            public void registerCallback(String serviceName, GenericServiceCallback cb) {

            }

            @Override
            public void schedule(String poolName, String serviceName, Map<String, ?> context, long startTime, int frequency, int interval, int count, long endTime, int maxRetry) throws GenericServiceException {

            }

            @Override
            public void schedule(String poolName, String serviceName, long startTime, int frequency, int interval, int count, long endTime, int maxRetry, Object... context) throws GenericServiceException {

            }

            @Override
            public void schedule(String jobName, String poolName, String serviceName, Map<String, ?> context, long startTime, int frequency, int interval, int count, long endTime, int maxRetry) throws GenericServiceException {

            }

            @Override
            public void schedule(String jobName, String poolName, String serviceName, long startTime, int frequency, int interval, int count, long endTime, int maxRetry, Object... context) throws GenericServiceException {

            }

            @Override
            public void schedule(String serviceName, Map<String, ?> context, long startTime, int frequency, int interval, int count, long endTime) throws GenericServiceException {

            }

            @Override
            public void schedule(String serviceName, long startTime, int frequency, int interval, int count, long endTime, Object... context) throws GenericServiceException {

            }

            @Override
            public void schedule(String serviceName, Map<String, ?> context, long startTime, int frequency, int interval, int count) throws GenericServiceException {

            }

            @Override
            public void schedule(String serviceName, long startTime, int frequency, int interval, int count, Object... context) throws GenericServiceException {

            }

            @Override
            public void schedule(String serviceName, Map<String, ?> context, long startTime, int frequency, int interval, long endTime) throws GenericServiceException {

            }

            @Override
            public void schedule(String serviceName, long startTime, int frequency, int interval, long endTime, Object... context) throws GenericServiceException {

            }

            @Override
            public void schedule(String serviceName, Map<String, ?> context, long startTime) throws GenericServiceException {

            }

            @Override
            public void schedule(String serviceName, long startTime, Object... context) throws GenericServiceException {

            }

            @Override
            public void addRollbackService(String serviceName, Map<String, ?> context, boolean persist) throws GenericServiceException {

            }

            @Override
            public void addRollbackService(String serviceName, boolean persist, Object... context) throws GenericServiceException {

            }

            @Override
            public void addCommitService(String serviceName, Map<String, ?> context, boolean persist) throws GenericServiceException {

            }

            @Override
            public void addCommitService(String serviceName, boolean persist, Object... context) throws GenericServiceException {

            }

            @Override
            public JobManager getJobManager() {
                return null;
            }

            @Override
            public JmsListenerFactory getJMSListeneFactory() {
                return null;
            }

            @Override
            public Delegator getDelegator() {
                return null;
            }

            @Override
            public Security getSecurity() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public DispatchContext getDispatchContext() {
                return null;
            }

            @Override
            public void deregister() {

            }
        };
        DispatchContext dispatchContext = new DispatchContext(topic, classLoader, localDispatcher);
        Map<String, ?> context = new HashMap<>();
        return FindServices.performFind(dispatchContext, context);
    }
}
