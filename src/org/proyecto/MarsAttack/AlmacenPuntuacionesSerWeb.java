package org.proyecto.MarsAttack;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;



public class AlmacenPuntuacionesSerWeb implements AlmacenPuntuaciones {
	public Vector<String> listaPuntuaciones(int cantidad) {
		try {
			URL url = new URL(
					"http://158.42.146.127:8080/"
							+ "PuntuacionesServicioWeb/services/PuntuacionesServicioWeb."
							+ "PuntuacionesServicioWebHttpEndpoint/lista");
			HttpURLConnection conecxion = (HttpURLConnection) url
					.openConnection();
			conecxion.setRequestMethod("POST");
			conecxion.setDoOutput(true);
			OutputStreamWriter sal = new OutputStreamWriter(
					conecxion.getOutputStream());
			sal.write("maximo=");
			sal.write(URLEncoder.encode(String.valueOf(cantidad), "UTF-8"));
			sal.flush();
			if (conecxion.getResponseCode() == HttpURLConnection.HTTP_OK) {
				SAXParserFactory fabrica = SAXParserFactory.newInstance();
				SAXParser parser = fabrica.newSAXParser();
				XMLReader lector = parser.getXMLReader();
				ManejadorSerWeb manejadorXML = new ManejadorSerWeb();
				lector.setContentHandler(manejadorXML);
				lector.parse(new InputSource(conecxion.getInputStream()));
				return manejadorXML.getLista();
			} else {
				
				return null;
			}
		} catch (Exception e) {
			
			return null;
		}
	}

	class ManejadorSerWeb extends DefaultHandler {
		private Vector<String> lista;
		private StringBuilder cadena;

		public Vector<String> getLista() {
			return lista;
		}

		@Override
		public void startDocument() throws SAXException {
			cadena = new StringBuilder();
			lista = new Vector<String>();
		}

		@Override
		public void characters(char ch[], int comienzo, int longitud) {
			cadena.append(ch, comienzo, longitud);
		}

		@Override
		public void endElement(String uri, String nombreLocal,
				String nombreCualif) throws SAXException {
			if (nombreLocal.equals("return")) {
				try {
					lista.add(URLDecoder.decode(cadena.toString(), "UTF8"));
				} catch (UnsupportedEncodingException e) {
					
				}
			}
			cadena.setLength(0);
		}
	}

	public void guardarPuntuacion(int puntos, String nombre, long fecha) {
		try {
			URL url = new URL(
					"http://158.42.146.127:8080/"
							+ "PuntuacionesServicioWeb/services/PuntuacionesServicioWeb."
							+ "PuntuacionesServicioWebHttpEndpoint/nueva");
			HttpURLConnection conecxion = (HttpURLConnection) url
					.openConnection();
			conecxion.setRequestMethod("POST");
			conecxion.setDoOutput(true);
			OutputStreamWriter sal = new OutputStreamWriter(
					conecxion.getOutputStream());
			sal.write("puntos=");
			sal.write(URLEncoder.encode(String.valueOf(puntos), "UTF-8"));
			sal.write("&nombre=");
			sal.write(URLEncoder.encode(nombre, "UTF-8"));
			sal.write("&fecha=");
			sal.write(URLEncoder.encode(String.valueOf(fecha), "UTF-8"));
			sal.flush();
			if (conecxion.getResponseCode() == HttpURLConnection.HTTP_OK) {
				SAXParserFactory fabrica = SAXParserFactory.newInstance();
				SAXParser parser = fabrica.newSAXParser();
				XMLReader lector = parser.getXMLReader();
				ManejadorSerWeb manejadorXML = new ManejadorSerWeb();
				lector.setContentHandler(manejadorXML);
				lector.parse(new InputSource(conecxion.getInputStream()));
				if (manejadorXML.getLista().size() != 1
						|| !manejadorXML.getLista().get(0).equals("OK")) {
					
				}
			} else {
				
			}
			conecxion.disconnect();
		} catch (Exception e) {
			
		}
	}
}
