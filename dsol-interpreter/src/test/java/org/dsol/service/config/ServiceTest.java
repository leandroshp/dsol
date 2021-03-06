package org.dsol.service.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXB;

import junit.framework.Assert;

import org.junit.Test;

public class ServiceTest {

	@Test
	public void testMarshallRestServiceXML(){
		
		Service service = Service.createRestService("id", "name","url","method","mediaType","requestHeaders", "responseHeaders", null);
		
		service.addAditionalParameter(new AdditionalParameter(0,"name","value"));
		service.addAditionalParameter(new AdditionalParameter(1,"name1","value1"));
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		JAXB.marshal(service, bos);

		System.out.println(new String(bos.toByteArray()));
		
		Service serviceActual = JAXB.unmarshal(new ByteArrayInputStream(bos.toByteArray()), Service.class);
		
		Assert.assertEquals("rest", serviceActual.getType());
		Assert.assertEquals("id", serviceActual.getId());
		Assert.assertEquals("name", serviceActual.getName());
		Assert.assertEquals("url", serviceActual.getUrl());
		Assert.assertEquals("method", serviceActual.getMethod());
		Assert.assertEquals("mediaType", serviceActual.getMediaType());
		Assert.assertEquals("requestHeaders", serviceActual.getRequestHeaders());
		Assert.assertEquals("responseHeaders", serviceActual.getResponseHeaders());
		Assert.assertEquals(2, serviceActual.getAdditionalParameters().size());
		
		Assert.assertEquals(new Integer(0), serviceActual.getAdditionalParameters().get(0).getIndex());
		Assert.assertEquals("name", serviceActual.getAdditionalParameters().get(0).getName());
		Assert.assertEquals("value", serviceActual.getAdditionalParameters().get(0).getValue());
		
		Assert.assertEquals(new Integer(1), serviceActual.getAdditionalParameters().get(1).getIndex());
		Assert.assertEquals("name1", serviceActual.getAdditionalParameters().get(1).getName());
		Assert.assertEquals("value1", serviceActual.getAdditionalParameters().get(1).getValue());
		
		
	}
	
	@Test
	public void testMarshallSoapServiceXML(){
		
		Service service = Service.createSoapService("id", "name","wsdl","operation","uri","requestHeaders", "responseHeaders", null);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		JAXB.marshal(service, bos);

		Service serviceActual = JAXB.unmarshal(new ByteArrayInputStream(bos.toByteArray()), Service.class);
		
		Assert.assertEquals("soap", serviceActual.getType());
		Assert.assertEquals("id", serviceActual.getId());
		Assert.assertEquals("name", serviceActual.getName());
		Assert.assertEquals("wsdl", serviceActual.getWsdl());
		Assert.assertEquals("operation", serviceActual.getOperation());
		Assert.assertEquals("uri", serviceActual.getOperationNamespaceUri());
		Assert.assertEquals("requestHeaders", serviceActual.getRequestHeaders());
		Assert.assertEquals("responseHeaders", serviceActual.getResponseHeaders());

	}
	
	
	@Test
	public void testMarshalServices(){
		Services services = new Services();
		
		services.addService(Service.createRestService("id", "name","url","method","mediaType","requestHeaders", "responseHeaders", null));
		services.addService(Service.createSoapService("id", "name","wsdl","operation","uri","requestHeaders", "responseHeaders", null));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		JAXB.marshal(services, bos);
		
		System.out.println(new String(bos.toByteArray()).trim());
	}
	
	
	
	
	
}

