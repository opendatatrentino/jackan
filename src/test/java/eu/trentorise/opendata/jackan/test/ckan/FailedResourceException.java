package eu.trentorise.opendata.jackan.test.ckan;

import eu.trentorise.opendata.jackan.ckan.CkanClient;

/**
 *
 * @author David Leoni
 */
public class FailedResourceException extends RuntimeException {

        CkanClient client;
        String resourceId;
        String datasetName;

        public FailedResourceException(CkanClient client, String msg, String datasetName, String resourceId) {
            super(msg);
            this.client = client;
            this.resourceId = resourceId;
            this.datasetName = datasetName;
        }

        public FailedResourceException(CkanClient client, String msg, String datasetName, String resourceId, Throwable thrwbl) {
            super(msg, thrwbl);
            this.client = client;
            this.resourceId = resourceId;
            this.datasetName = datasetName;
        }

        public CkanClient getClient() {
            return client;
        }

        public String getResourceId() {
            return resourceId;
        }

        public String getDatasetName() {
            return datasetName;
        }

        @Override
        public String toString() {
            String descr;
            try {
                descr = "resource ckan url=" + CkanClient.makeResourceURL(client.getCatalogURL(), datasetName, resourceId);
            }
            catch (Exception ex) {
                descr = "datasetName: " + datasetName + "resourceId: " + resourceId;
            }
            return "FailedResource: \n"
                    + "  client=" + client + "\n"
                    + "  " + descr + "\n";
        }

}