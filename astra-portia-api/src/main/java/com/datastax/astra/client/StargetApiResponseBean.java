package com.datastax.astra.client;

/**
 * Wrapper for the API
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargetApiResponseBean<T> {
    
    public String documentId;
    
    private T data;
    
    public StargetApiResponseBean() {}
    
    /**
     * Getter accessor for attribute 'documentId'.
     *
     * @return
     *       current value of 'documentId'
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Setter accessor for attribute 'documentId'.
     * @param documentId
     * 		new value for 'documentId '
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    /**
     * Getter accessor for attribute 'data'.
     *
     * @return
     *       current value of 'data'
     */
    public T getData() {
        return data;
    }

    /**
     * Setter accessor for attribute 'data'.
     * @param data
     * 		new value for 'data '
     */
    public void setData(T data) {
        this.data = data;
    }

}
