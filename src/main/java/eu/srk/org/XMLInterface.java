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
// Note: Request for status on connection is now implemented as an empty RW_Message

// Onramp has to be implemented

package eu.srk.org;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
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

	private ArrayList<String> convertStringXml(String input) {
		// String
		// input="PositionReport;Number of Travels;TravelID;XPos;YPOS;Request Additional Info; TravelID;XPos;YPOS;Request Additional Info";
		// String input="TravelSelected;TravelID;DPiD";
		// String input="ReplyConnectionRequest;connected";

		String[] parts = input.split(";");

		ArrayList<String> list = new ArrayList<String>();
		
		if (parts[0].equals("PositionReport")) {
			int n = Integer.valueOf(parts[1]);
			int j = 2;

			for (int i = 0; i < n; i++) {
				long travelId = Integer.valueOf(parts[j++]);
				int xpos = Integer.valueOf(parts[j++]);
				int ypos = Integer.valueOf(parts[j++]);
				int rai = Integer.valueOf(parts[j++]);

				String xml = "<PositionReport>";
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
				xml1 = onRamp(xml1);
				list.add(xml1);
			}
		} else if (parts[0].equals("TravelSelected")) {
			int j = 1;
			long travelId = Integer.valueOf(parts[j++]);
			int dpid = Integer.valueOf(parts[j++]);

			String xml = "<JourneySelection>";
			xml = xml + "<DPID>" + dpid + "</DPID>";
			xml = xml + "<travelId>" + travelId + "</travelId>";
			xml = xml + "</JourneySelection>";
			
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
			xml1 = onRamp(xml1);
			list.add(xml1);			

		} else if (parts[0].equals("ReplyConnectionRequest")) {
			String xml = "<ConnectionStatus>";
			if (parts[1].equals("connected")) {
				xml = xml + "<Connection>true</Connection>";
			} else {
				xml = xml + "<Connection>false</Connection>";
			}
			xml = xml + "</ConnectionStatus>";
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
			xml1 = onRamp(xml1);
			list.add(xml1);
		}

		return list;
	};

	private String convertXmlString(String input) {

		LoggerObject logs;

		String xsltn = "<?xml version=\"1.0\"?>"
				+ "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" >"
				+ "<xsl:output method=\"text\" omit-xml-declaration=\"yes\" indent=\"no\"/>"
				+ "<xsl:template match=\"TrackIdentification\">"
				+ "Track Identification;"
				+ "<xsl:variable name=\"SL\" select=\"round(number(ShipLength) * 1024 div 1000)\"/>"
				+ "<xsl:variable name=\"SW\" select=\"round(number(ShipWidth) * 1024 div 1000)\"/>"
				+ "<xsl:variable name=\"SD\" select=\"round(number(ShipDraught) * 10)\"/>"
				+ "<xsl:value-of select=\"concat(TravelID,';',ShipLabel,';',$SL,';',$SW,';',$SD,';',DPID,';',SeaVessel,';',Anchored,';',SpecialTransport,';',IMOVessel,';',PilotOnBoard,'&#xA;')\"/>"
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

		// String xsltcn = "<?xml version=\"1.0\"?>"
		// +
		// "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" >"
		// +
		// "<xsl:output method=\"text\" omit-xml-declaration=\"yes\" indent=\"no\"/>"
		// + "<xsl:template match=\"TrackIdentification\">"
		// + "Change of travel data;1;"
		// +
		// "<xsl:variable name=\"SL\" select=\"round(number(ShipLength) * 1024 div 1000)\"/>"
		// +
		// "<xsl:variable name=\"SW\" select=\"round(number(ShipWidth) * 1024 div 1000)\"/>"
		// +
		// "<xsl:variable name=\"SD\" select=\"round(number(ShipDraught) * 10)\"/>"
		// +
		// "<xsl:value-of select=\"concat(TravelID,';',ShipLabel,';',$SL,';',$SW,';',$SD,';',,DPID,';',SeaVessel,';',Anchored,';',SpecialTransport,';',IMOVessel,';',PilotOnBoard,'&#xA;')\"/>"
		// + "</xsl:template>" + "</xsl:stylesheet>";

		// String xsltce = "<?xml version=\"1.0\"?>"
		// +
		// "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" >"
		// +
		// "<xsl:output method=\"text\" omit-xml-declaration=\"yes\" indent=\"no\"/>"
		// + "<xsl:template match=\"TrackIdentification\">"
		// + "Change of travel data Extended;1;"
		// +
		// "<xsl:variable name=\"SL\" select=\"round(number(ShipLength) * 1024 div 1000)\"/>"
		// +
		// "<xsl:variable name=\"SW\" select=\"round(number(ShipWidth) * 1024 div 1000)\"/>"
		// +
		// "<xsl:variable name=\"SD\" select=\"round(number(ShipDraught) *  10)\"/>"
		// +
		// "<xsl:value-of select=\"concat(TravelID,';',ShipLabel,';',ShipName,';',$SL,';',$SW,';',$SD,';',DPID,';',SeaVessel,';',Anchored,';',SpecialTransport,';',IMOVessel,';',PilotOnBoard,'&#xA;')\"/>"
		// + "</xsl:template>" + "</xsl:stylesheet>";

		// Extended version to be used ?
		boolean extended = false;
		if (input.indexOf("ShipName") >= 0)
			extended = true;
		if (input.indexOf("ShipName/>") >= 0)
			extended = false;
		if (input.indexOf("ShipName></ShipName") >= 0)
			extended = false;

		boolean csr = false;
		if (input.indexOf("RW_Message/>") >= 0)
			csr = true;
		if (input.indexOf("RW_Message><RW_Message") >= 0)
			csr = true;

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
			finalstring = "InfoConnectionRequest";
		}

		return finalstring;
	}

	public static String onRamp(String input) {
		// Note: split the positionreport in separate reports

		return input;
	}

	public static ArrayList<String> stringToXML(String input) {
		// String
		// input="PositionReport;Number of Travels;TravelID;XPos;YPOS;Request Additional Info; TravelID;XPos;YPOS;Request Additional Info";
		// String input="TravelSelected;TravelID;DPiD";
		// String input = "ReplyConnectionRequest;connected";

		XMLInterface t = new XMLInterface();
		ArrayList<String> result = t.convertStringXml(input);

		LoggerObject logs;
		logs = LoggerObject.getInstance();
		logs.logDebug("Receiving message: " + input);
		for (int i=0;i<result.size();i++) {
			logs.logDebug("Receiving message: " + result.get(i));
		}
		return result;
	}

	public static String xmlToString(String input) {

		String result = "";

		// If not xml, then it is testcase
		if (input.startsWith("<")) {
			XMLInterface t = new XMLInterface();
			result = t.convertXmlString(input);
		} else {
			result = input;
		}
		LoggerObject logs;
		logs = LoggerObject.getInstance();
		logs.logDebug("Intended Sending message: " + input);
		return result;
	}

	public static void main(String[] args) {

		// String
		// input="PositionReport;Number of Travels;TravelID;XPos;YPOS;Request Additional Info; TravelID;XPos;YPOS;Request Additional Info";
		// String input="TravelSelected;TravelID;DPiD";
		// String input = "ReplyConnectionRequest;connected";

		// XMLInterface k = new XMLInterface();
		// String xml = k.convertStringXml(input);
		// System.out.println(xml);

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

		String s31 = "<Anchored>true</Anchored>\n" + "<DPID>234</DPID>\n"
				+ "<IMOVessel>true</IMOVessel>\n"
				+ "<PilotOnBoard>true</PilotOnBoard>\n"
				+ "<SeaVessel>true</SeaVessel>\n"
				+ "<ShipDraught>123</ShipDraught>\n"
				+ "<ShipLength>232</ShipLength>\n"
				+ "<ShipWidth>3434</ShipWidth>\n"
				+ "<SpecialTransport>true</SpecialTransport>\n"
				+ "<TravelID>1205160002</TravelID>\n"
				+ "<ShipLabel>JAANTJE         </ShipLabel>\n";

		String s32 = "<Anchored>true</Anchored>\n" + "<DPID>234</DPID>\n"
				+ "<IMOVessel>true</IMOVessel>\n"
				+ "<PilotOnBoard>true</PilotOnBoard>\n"
				+ "<SeaVessel>true</SeaVessel>\n"
				+ "<ShipDraught>123</ShipDraught>\n"
				+ "<ShipLength>232</ShipLength>\n"
				+ "<ShipWidth>3434</ShipWidth>\n"
				+ "<SpecialTransport>true</SpecialTransport>\n"
				+ "<TravelID>1205160002</TravelID>\n"
				+ "<ShipLabel>JAANTJE         </ShipLabel>\n"
				+ "<ShipName></ShipName>\n";

		String s33 = "<Anchored>true</Anchored>\n" + "<DPID>234</DPID>\n"
				+ "<IMOVessel>true</IMOVessel>\n"
				+ "<PilotOnBoard>true</PilotOnBoard>\n"
				+ "<SeaVessel>true</SeaVessel>\n"
				+ "<ShipDraught>123</ShipDraught>\n"
				+ "<ShipLength>232</ShipLength>\n"
				+ "<ShipWidth>3434</ShipWidth>\n"
				+ "<SpecialTransport>true</SpecialTransport>\n"
				+ "<TravelID>1205160002</TravelID>\n"
				+ "<ShipLabel>JAANTJE         </ShipLabel>\n" + "<ShipName/>\n";

		String s = s1 + s33 + s2;

		// String xslt1 = "<?xml version=\"1.0\"?>"
		// +
		// "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" >"
		// +
		// "<xsl:output method=\"text\" omit-xml-declaration=\"yes\" indent=\"no\"/>"
		// + "<xsl:template match=\"/\">"
		// +
		// "Anchored,DPID,IMOVessel,PilotOnBoard,SeaVessel,ShipDraught,ShipLength,ShipWidth,SpecialTransport,TravelID,ShipLabel,ShipName"
		// + "<xsl:for-each select=\"//TrackIdentification\">"
		// +
		// "<xsl:value-of select=\"concat(Anchored,';',DPID,';',IMOVessel,';',PilotOnBoard,';',SeaVessel,';',ShipDraught,';',ShipLength,';',ShipWidth,';',SpecialTransport,';',TravelID,';',ShipLabel,';',ShipName,'&#xA;')\"/>"
		// + "</xsl:for-each>" + "</xsl:template>" + "</xsl:stylesheet>";

		XMLInterface k = new XMLInterface();
		String xml1 = k.convertXmlString(s);
		System.out.println(xml1);

	}
}