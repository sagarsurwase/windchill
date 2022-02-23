package ext.piterion.part.Innovation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import wt.fc.PersistenceHelper;
import wt.fc.collections.WTArrayList;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class TeamCenterToWindchill implements RemoteAccess {

	public static void main(String[] args) throws WTPropertyVetoException, WTException {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");

		try {
			rms.invoke("createPartFromXml", TeamCenterToWindchill.class.getName(), null, new Class[] {},
					new Object[] {});
		} catch (RemoteException | InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	public static void createPartFromXml() throws WTException {
		System.out.println("Inside the createPartFromXml");

		Map numberName = readXML();
		
		System.out.println("numberName >>>>>>> " + numberName);
		try {
			WTArrayList partArrayList = createPart((HashMap) numberName);
			System.out.println("Size of parts created >>>> " + partArrayList);
			PersistenceHelper.manager.save(partArrayList);
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}

	private static WTArrayList createPart(HashMap numberName) throws WTException,
			WTPropertyVetoException {
		WTArrayList arrayList = new WTArrayList();
		for (Object object : numberName.entrySet()) {
			if (object instanceof Map.Entry) {
				Map.Entry<String, String> set = (Entry<String, String>) object;
				
				String name = set.getKey();
				String number = set.getValue();
				System.out.println("Name >>>  " + name );
				System.out.println("Number >>>  " + number );
				WTPart part = WTPart.newWTPart();
				System.out.println("Part >>> " + part);
				part.setName(name);
				part.setNumber(number);
				arrayList.add(part);
			}

		}
		return arrayList;
	}

	// part.setNumber(numberName.);

	private static HashMap readXML() {
		System.out.println("Inside readXML...............");
		final String FILENAME = "C:\\ptc\\Windchill_12.0\\Windchill\\TWsample.xml";
		
		// Instantiate the Factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		HashMap<String,String> nameNumber = new HashMap<String,String>();
		try {
			// optional, but recommended
			// process XML securely, avoid attacks like XML External Entities (XXE)
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			
			// parse XML file
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc;

			doc = db.parse(new File(FILENAME));
			// optional, but recommended
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
			System.out.println("------");
			System.out.println("");

			// get <part>
			NodeList list = doc.getElementsByTagName("Part");

			for (int temp = 0; temp < list.getLength(); temp++) {

				Node node = list.item(temp);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					System.out.println("temp >>>>>>>>>" + temp);
					Element element = (Element) node;

					// get staff's attribute
					// String id = element.getAttribute("id");

					// get text
					String number = element.getElementsByTagName("number").item(0).getTextContent();
					String name = element.getElementsByTagName("name").item(0).getTextContent();
					String version = element.getElementsByTagName("version").item(0).getTextContent();
					String iteration = element.getElementsByTagName("iteration").item(0).getTextContent();
					String view = element.getElementsByTagName("view").item(0).getTextContent();
					
					System.out.println("Current Element :" + node.getNodeName());
					System.out.println("number : " + number);
					System.out.println(" Name : " + name);
					System.out.println("version : " + version);
					System.out.printf("iteration : " + iteration);
					System.out.printf("view : " + view);
					
					nameNumber.put(name, number);

				}
			}

		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nameNumber;
	}
}
