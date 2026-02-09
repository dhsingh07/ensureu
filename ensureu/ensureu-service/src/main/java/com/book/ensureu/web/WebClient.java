package com.book.ensureu.web;

import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Provides an encapsulation layer on the underlying HTTP transport library used.
 *
 * 
 */
public interface WebClient {

    /**
     * Perform a HTTP GET request to the specified {@code path} with the given {@code headers}. The returned type will
     * be deserialized from the response to the specified {@code returnType}.
     *
     * @param path       The URL where the GET request will be sent
     * @param headers    The headers to be applied to the request
     * @param returnType The expected return type
     * @return The deserialized response
     * @throws IOException
     * @throws WebClientException
     */
    <T> T get(String path, Map<String, Object> headers, Class<T> returnType) throws IOException, WebClientException;

    /**
     * Perform a HTTP POST request to the specified {@code path} with the given {@code headers}. The {@code mediaType}
     * must correctly represent the {@code data} who is passed as the byte array of the body as a String. The returned
     * type will be deserialized from the response to the specified {@code returnType}.
     *
     * @param path
     * @param mediaType
     * @param data
     * @param headers
     * @param returnType
     * @return The deserialized response
     * @throws IOException
     * @throws WebClientException
     */
    <T> T post(String path, MediaType mediaType, byte[] data,
               Map<String, Object> headers, Class<T> returnType) throws IOException, WebClientException;

    /**
     * Perform a HTTP GET request to the specified {@code path} with the given {@code headers}. The returned type will
     * be the input stream that represents the response.
     *
     * @param path    The URL where the GET request will be sent
     * @param headers The headers to be applied to the request
     * @return The input stream of the response
     * @throws IOException
     * @throws WebClientException
     */
    byte[] getRaw(String path, Map<String, Object> headers) throws IOException, WebClientException;
    
	/**
	 * Perform a HTTP POST request to the specified {@code path} with the given
	 * {@code headers}. The {@code mediaType} must correctly represent the
	 * {@code data} who is passed as the byte array of the body as a String. The
	 * returned type String}.
	 *
	 * @param path
	 * @param mediaType
	 * @param data
	 * @param headers
	 * @throws IOException
	 * @throws WebClientException
	 */

	// JP-2394 Code changes to send POST request
	String post(String path, MediaType mediaType, byte[] data,
			Map<String, Object> headers) throws IOException, WebClientException;
	
	
	public void postNoReturn(String path, String mediaType, byte[] data, Map<String, Object> headers) throws IOException,WebClientException;
}
