package com.example.testsign;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.Document;

import xades4j.properties.DataObjectDesc;
import xades4j.properties.DataObjectFormatProperty;

import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.production.DataObjectReference;
import xades4j.production.SignedDataObjects;
import xades4j.production.XadesBesSigningProfile;
import xades4j.production.XadesSignatureResult;
import xades4j.providers.impl.FileSystemKeyStoreKeyingDataProvider;
import xades4j.providers.impl.KeyStoreKeyingDataProvider.SigningCertificateSelector;
import xades4j.production.SignedDataObjects;

import xades4j.production.XadesSigner;

import xades4j.production.XadesSigner;

import xades4j.properties.DataObjectFormatProperty;

import xades4j.providers.KeyingDataProvider;

@SpringBootApplication
public class TestSignApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestSignApplication.class, args);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse("C:\\sources\\extffa\\test-sign\\test-sign\\test.xml");
			// Define the signing key/certificate
			KeyingDataProvider kp = FileSystemKeyStoreKeyingDataProvider
					.builder("jks", "C:\\sources\\extffa\\test-sign\\test-sign\\mykeystore.jks",SigningCertificateSelector.single())
					.entryPassword((alias, certificate) -> "Cambiar123".toCharArray()).storePassword(() -> "Cambiar123".toCharArray())
					.build();
			// Define the signed object
			DataObjectDesc obj = new DataObjectReference("")
					.withTransform(new EnvelopedSignatureTransform())
					.withDataObjectFormat(new DataObjectFormatProperty("text/xml"));
			// Create the signature
			XadesSigner signer = new XadesBesSigningProfile(kp).newSigner();
			XadesSignatureResult result = signer.sign(new SignedDataObjects(obj), doc.getDocumentElement());
			System.out.println(result.getSignature().getKeyInfo().getDocument());
			printDocument(doc, System.out);
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	
		transformer.transform(new DOMSource(doc), 
			 new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}




}
