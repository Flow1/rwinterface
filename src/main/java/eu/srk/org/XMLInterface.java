/**
 * @author Theo den Exter, ARS
 * Date: June 25th 2016
 * Version 1.0
 *
 * Conversion to and from CMD 1.0.2 without ramping on/off
 *
 * History
 *
 */

// CMD issue (RWv1.0.2.xsd): 
// Note: RIS messages are missing
// Note: change of data is only 1 record instead of all records
// Note: discern track identification/change data is missing
// Note: Video mode select is missing
// Note: Request status on connection is implemented as an empty RW_Message

package eu.srk.org;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

public class XMLInterface {

	public XMLInterface() {

	}

	private String convertStringXml(String input) {
		// String
		// input="PositionReport;Number of Travels;TravelID;XPos;YPOS;Request Additional Info; TravelID;XPos;YPOS;Request Additional Info";
		// String input="TravelSelected;TravelID;DPiD";
		// String input="ReplyConnectionRequest;connected";

		String[] parts = input.split(";");

		String xml = "";
		if (parts[0].equals("PositionReport")) {
			int n = Integer.valueOf(parts[1]);
			int j = 2;
			for (int i = 0; i < n; i++) {
				long travelId = Integer.valueOf(parts[j++]);
				int xpos = Integer.valueOf(parts[j++]);
				int ypos = Integer.valueOf(parts[j++]);
				int rai = Integer.valueOf(parts[j++]);

				xml = "<PositionReport>";
				if (rai == 0) {
					xml = xml
							+ "<AdditionalInfoRequested>false</AdditionalInfoRequested>";
				} else {
					xml = xml
							+ "<AdditionalInfoRequested>true</AdditionalInfoRequested>";
				}
				xml = xml + "<TravelID>" + travelId + "</TravelID>";
				xml = xml + "<xpos>" + Math.round(xpos * (2000.0 / 1024.0))
						+ "</xpos>";
				xml = xml + "<ypos>" + Math.round(ypos * (2000.0 / 1024.0))
						+ "</ypos>";
				xml = xml + "</PositionReport>";
			}
		} else if (parts[0].equals("TravelSelected")) {
			int j = 1;
			long travelId = Integer.valueOf(parts[j++]);
			int dpid = Integer.valueOf(parts[j++]);

			xml = "<JourneySelection>";
			xml = xml + "<DPID>" + dpid + "</DPID>";
			xml = xml + "<travelId>" + travelId + "</travelId>";
			xml = xml + "</JourneySelection>";

		} else if (parts[0].equals("ReplyConnectionRequest")) {
			xml = "<ConnectionStatus>";
			if (parts[1].equals("connected")) {
				xml = xml + "<Connection>true</Connection>";
			} else {
				xml = xml + "<Connection>false</Connection>";
			}
			xml = xml + "</ConnectionStatus>";
		}

//		ZonedDateTime zdt = ZonedDateTime.now();
//		java.util.Date date = java.util.Date.from(zdt.toInstant());
		String convertedDate = "";
		GregorianCalendar gc = new GregorianCalendar(
				TimeZone.getTimeZone("CET"));
		try {
			convertedDate = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gc).toXMLFormat();
		} catch (DatatypeConfigurationException e) {
		}

		String xml1 = "<RW_Message Timestamp=\"" + convertedDate + "\">";
		xml1 = xml1 + xml;
		xml1 = xml1 + "</RW_Message>";

		return xml1;
	}

	private String convertXmlString(String input) {

		LoggerObject logs;
		
		// String s1 =
		// "<RW_Message Timestamp=\"2016-06-24T20:43:48.434+02:00\"><TrackIdentification>";

		// String s2 = "</TrackIdentification></RW_Message>";

		// String s3 = "<Anchored>true</Anchored>\n" + "<DPID>234</DPID>\n"
		// + "<IMOVessel>true</IMOVessel>\n"
		// + "<PilotOnBoard>true</PilotOnBoard>\n"
		// + "<SeaVessel>true</SeaVessel>\n"
		// + "<ShipDraught>123</ShipDraught>\n"
		// + "<ShipLength>232</ShipLength>\n"
		// + "<ShipWidth>3434</ShipWidth>\n"
		// + "<SpecialTransport>true</SpecialTransport>\n"
		// + "<TravelID>1205160002</TravelID>\n"
		// + "<ShipLabel>JAANTJE         </ShipLabel>\n"
		// + "<ShipName>JAANTJE</ShipName>\n";

		// String s = s1 + s3 + s2;

		String xsltn = "<?xml version=\"1.0\"?>"
				+ "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" >"
				+ "<xsl:output method=\"text\" omit-xml-declaration=\"yes\" indent=\"no\"/>"
				+ "<xsl:template match=\"TrackIdentification\">"
				+ "Track Identification;"
				+ "<xsl:variable name=\"SL\" select=\"round(number(ShipLength) * 1024 div 1000)\"/>"
				+ "<xsl:variable name=\"SW\" select=\"round(number(ShipWidth) * 1024 div 1000)\"/>"
				+ "<xsl:variable name=\"SD\" select=\"round(number(ShipDraught) * 10)\"/>"
				+ "<xsl:value-of select=\"concat(TravelID,';',ShipLabel,';',$SL,';',$SW,';',$SD,';',,DPID,';',SeaVessel,';',Anchored,';',SpecialTransport,';',IMOVessel,';',PilotOnBoard,'&#xA;')\"/>"
				+ "</xsl:template>" + "</xsl:stylesheet>";

		String xslte = "<?xml version=\"1.0\"?>"
				+ "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" >"
				+ "<xsl:output method=\"text\" omit-xml-declaration=\"yes\" indent=\"no\"/>"
				+ "<xsl:template match=\"TrackIdentification\">"
				+ "Track Identification Extended;"
				+ "<xsl:variable name=\"SL\" select=\"round(number(ShipLength) * 1024 div 1000)\"/>"
				+ "<xsl:variable name=\"SW\" select=\"round(number(ShipWidth) * 1024 div 1000)\"/>"
				+ "<xsl:variable name=\"SD\" select=\"round(number(ShipDraught) *  10)\"/>"
				+ "<xsl:value-of select=\"concat(TravelID,';',ShipLabel,';',ShipName,';',$SL,';',$SW,';',$SD,';',DPID,';',SeaVessel,';',Anchored,';',SpecialTransport,';',IMOVessel,';',PilotOnBoard,'&#xA;')\"/>"
				+ "</xsl:template>" + "</xsl:stylesheet>";

//		String xsltcn = "<?xml version=\"1.0\"?>"
//				+ "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" >"
//				+ "<xsl:output method=\"text\" omit-xml-declaration=\"yes\" indent=\"no\"/>"
//				+ "<xsl:template match=\"TrackIdentification\">"
//				+ "Change of travel data;1;"
//				+ "<xsl:variable name=\"SL\" select=\"round(number(ShipLength) * 1024 div 1000)\"/>"
//				+ "<xsl:variable name=\"SW\" select=\"round(number(ShipWidth) * 1024 div 1000)\"/>"
//				+ "<xsl:variable name=\"SD\" select=\"round(number(ShipDraught) * 10)\"/>"
//				+ "<xsl:value-of select=\"concat(TravelID,';',ShipLabel,';',$SL,';',$SW,';',$SD,';',,DPID,';',SeaVessel,';',Anchored,';',SpecialTransport,';',IMOVessel,';',PilotOnBoard,'&#xA;')\"/>"
//				+ "</xsl:template>" + "</xsl:stylesheet>";

//		String xsltce = "<?xml version=\"1.0\"?>"
//				+ "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" >"
//				+ "<xsl:output method=\"text\" omit-xml-declaration=\"yes\" indent=\"no\"/>"
//				+ "<xsl:template match=\"TrackIdentification\">"
//				+ "Change of travel data Extended;1;"
//				+ "<xsl:variable name=\"SL\" select=\"round(number(ShipLength) * 1024 div 1000)\"/>"
//				+ "<xsl:variable name=\"SW\" select=\"round(number(ShipWidth) * 1024 div 1000)\"/>"
//				+ "<xsl:variable name=\"SD\" select=\"round(number(ShipDraught) *  10)\"/>"
//				+ "<xsl:value-of select=\"concat(TravelID,';',ShipLabel,';',ShipName,';',$SL,';',$SW,';',$SD,';',DPID,';',SeaVessel,';',Anchored,';',SpecialTransport,';',IMOVessel,';',PilotOnBoard,'&#xA;')\"/>"
//				+ "</xsl:template>" + "</xsl:stylesheet>";
		
		// Extended version to be used ?
		boolean extended = false;
		if (input.indexOf("ShipName") >= 0)
			extended = true;
		if (input.indexOf("ShipName/>") >= 0)
			extended = false;
		if (input.indexOf("ShipName><ShipName") >= 0)
			extended = false;

		boolean csr=true;
		if (input.indexOf("RW_Message/>") >= 0)
			csr= false;
		if (input.indexOf("RW_Message><RW_Message") >= 0)
			csr = false;	

		String finalstring = "";
		if (!csr) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream in = new ByteArrayInputStream(
					input.getBytes(StandardCharsets.UTF_8));

			String xslt = xsltn;
			if (extended)
				xslt = xslte;

			InputStream ins = new ByteArrayInputStream(
					xslt.getBytes(StandardCharsets.UTF_8));
			Document document = builder.parse(in);

			StreamSource stylesource = new StreamSource(ins);
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer(stylesource);
			Source source = new DOMSource(document);
			StringWriter outWriter = new StringWriter();
			StreamResult result = new StreamResult(outWriter);

			transformer.transform(source, result);
			StringBuffer sb = outWriter.getBuffer();
			finalstring = sb.toString();

		} catch (Exception e) {
			logs = LoggerObject.getInstance();
			logs.logError(e.toString());
		}
		} else {
			finalstring="InfoConnectionRequest";
		}

		return finalstring;
	}

	
	public static String stringToXML(String input) {
		// String
		// input="PositionReport;Number of Travels;TravelID;XPos;YPOS;Request Additional Info; TravelID;XPos;YPOS;Request Additional Info";
		// String input="TravelSelected;TravelID;DPiD";
		// String input = "ReplyConnectionRequest;connected";

		XMLInterface t = new XMLInterface();
		String result = t.convertStringXml(input);
		
		LoggerObject logs;
		logs = LoggerObject.getInstance();
		logs.logDebug("Receiving message: " + input);
		
		return result;
	}

	public static String xmlToString(String input) {
		String s1 = "<RW_Message Timestamp=\"2016-06-24T20:43:48.434+02:00\"><TrackIdentification>";

		String s2 = "</TrackIdentification></RW_Message>";

		String s3 = "<Anchored>true</Anchored>\n" + "<DPID>234</DPID>\n"
				+ "<IMOVessel>true</IMOVessel>\n"
				+ "<PilotOnBoard>true</PilotOnBoard>\n"
				+ "<SeaVessel>true</SeaVessel>\n"
				+ "<ShipDraught>123</ShipDraught>\n"
				+ "<ShipLength>232</ShipLength>\n"
				+ "<ShipWidth>3434</ShipWidth>\n"
				+ "<SpecialTransport>true</SpecialTransport>\n"
				+ "<TravelID>1205160002</TravelID>\n"
				+ "<ShipLabel>JAANTJE         </ShipLabel>\n"
				+ "<ShipName>JAANTJE</ShipName>\n";

		String s = s1 + s3 + s2;
		
		XMLInterface t = new XMLInterface();
		String result = t.convertXmlString(s);
		
		LoggerObject logs;
		logs = LoggerObject.getInstance();
		logs.logDebug("Intended Sending message: " + input);
		
		return result;
	}

}